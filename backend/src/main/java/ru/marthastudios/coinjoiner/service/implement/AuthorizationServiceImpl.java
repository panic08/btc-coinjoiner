package ru.marthastudios.coinjoiner.service.implement;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.marthastudios.coinjoiner.payload.AuthorizationPayload;
import ru.marthastudios.coinjoiner.dto.UserDto;
import ru.marthastudios.coinjoiner.dto.vk.VkAccessTokenDto;
import ru.marthastudios.coinjoiner.dto.vk.VkUserDto;
import ru.marthastudios.coinjoiner.enums.UserLinkedSocialType;
import ru.marthastudios.coinjoiner.mapper.UserToUserDtoMapperImpl;
import ru.marthastudios.coinjoiner.model.User;
import ru.marthastudios.coinjoiner.model.UserLinkedSocial;
import ru.marthastudios.coinjoiner.property.VkOAuthProperty;
import ru.marthastudios.coinjoiner.repository.UserLinkedSocialRepository;
import ru.marthastudios.coinjoiner.repository.UserRepository;
import ru.marthastudios.coinjoiner.security.jwt.JwtUtil;
import ru.marthastudios.coinjoiner.service.AuthorizationService;
import ru.marthastudios.coinjoiner.util.HexGeneratorUtil;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {
    private final JwtUtil jwtUtil;

    private final UserLinkedSocialRepository userLinkedSocialRepository;
    private final UserRepository userRepository;

    private final VkOAuthProperty vkOAuthProperty;
    private final UserToUserDtoMapperImpl userToUserDtoMapper;
    private static String OAUTH_REDIRECT_VK_URL;
    private static final String GET_VK_ACCESS_CODE_URL = "https://oauth.vk.com/access_token";
    private static final String GET_VK_USER_URL = "https://api.vk.com/method/users.get";

    @PostConstruct
    public void init(){
        OAUTH_REDIRECT_VK_URL = "https://oauth.vk.com/authorize?client_id="
                + vkOAuthProperty.getClientId() + "&display=page&redirect_uri="
                + vkOAuthProperty.getRedirectUrl() + "&scope="
                + vkOAuthProperty.getScopes() + "&response_type=code&v=5.131&state=123456";
    }

    @Override
    public Mono<ResponseEntity<Void>> redirectVk() {
        return Mono.fromSupplier(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(OAUTH_REDIRECT_VK_URL));

            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        });
    }

    @Override
    @Transactional
    public Mono<AuthorizationPayload> handleAuthorizeByVk(AuthorizationPayload authorizationPayload) {
        return getVKAccessTokenByCode(authorizationPayload.getCode())
                .flatMap(vkAccessTokenDto -> getVKUserByAccessToken(vkAccessTokenDto.getAccessToken()))
                .flatMap(vkUserDto -> {
                    String identifier = vkUserDto.getResponse()[0].getId() + "_vk";

                    return userLinkedSocialRepository.findByIdentifierAndType(identifier, UserLinkedSocialType.VK)
                            .flatMap(userLinkedSocial -> {
                                return userRepository.findById(userLinkedSocial.getUserId())
                                        .map(jwtUtil::generateAccessToken)
                                        .map(accessToken -> new AuthorizationPayload(null, accessToken));
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                String username = vkUserDto.getResponse()[0].getFirstName() + " "
                                        + vkUserDto.getResponse()[0].getLastName().toCharArray()[0] + ".";

                                User user = new User();

                                user.setUsername(username);
                                user.setPassword(HexGeneratorUtil.generateHex());
                                user.setRegisteredAt(System.currentTimeMillis());

                                return userRepository.save(user)
                                        .flatMap(savedUser -> {
                                            UserLinkedSocial userLinkedSocial = new UserLinkedSocial();

                                            userLinkedSocial.setUserId(savedUser.getId());
                                            userLinkedSocial.setType(UserLinkedSocialType.VK);
                                            userLinkedSocial.setIdentifier(identifier);

                                            Mono<UserLinkedSocial> userLinkedSocialMono =
                                                    userLinkedSocialRepository.save(userLinkedSocial);

                                            return userLinkedSocialMono
                                                    .thenReturn(new AuthorizationPayload(null, jwtUtil.generateAccessToken(savedUser)));

                                        });
                            }));
                });
    }

    @Override
    public Mono<UserDto> getInfoByAccessToken(long principalId) {
        return userRepository.findById(principalId)
                .map(userToUserDtoMapper::userToUserDto);
    }

    private Mono<VkAccessTokenDto> getVKAccessTokenByCode(String code){
        WebClient.Builder webClient = WebClient.builder();
        return webClient
                .baseUrl(GET_VK_ACCESS_CODE_URL + "?client_id="
                        + vkOAuthProperty.getClientId()
                        + "&client_secret=" + vkOAuthProperty.getClientSecret()
                        + "&redirect_uri=" + vkOAuthProperty.getRedirectUrl()
                        + "&code=" + code
                )
                .build()
                .post()
                .retrieve()
                .bodyToMono(VkAccessTokenDto.class);
    }

    private Mono<VkUserDto> getVKUserByAccessToken(String accessToken){
        return WebClient.builder()
                .baseUrl(GET_VK_USER_URL + "?v=5.131&fields=has_photo,photo_max")
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build()
                .get()
                .retrieve()
                .bodyToMono(VkUserDto.class)
                .cache();
    }
}
