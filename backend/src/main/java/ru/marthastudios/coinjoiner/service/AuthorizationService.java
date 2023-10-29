package ru.marthastudios.coinjoiner.service;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import ru.marthastudios.coinjoiner.payload.AuthorizationPayload;
import ru.marthastudios.coinjoiner.dto.UserDto;

public interface AuthorizationService {
    Mono<ResponseEntity<Void>> redirectVk();
    Mono<AuthorizationPayload> handleAuthorizeByVk(AuthorizationPayload authorizationPayload);
    Mono<UserDto> getInfoByAccessToken(long principalId);
}
