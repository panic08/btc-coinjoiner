package ru.marthastudios.coinjoiner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.marthastudios.coinjoiner.payload.AuthorizationPayload;
import ru.marthastudios.coinjoiner.dto.UserDto;
import ru.marthastudios.coinjoiner.service.implement.AuthorizationServiceImpl;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationServiceImpl authorizationService;

    @GetMapping("/oauth/redirectVk")
    public Mono<ResponseEntity<Void>> redirectVk(){
        return authorizationService.redirectVk();
    }

    @PostMapping("/oauth/authorizeByVk")
    public Mono<AuthorizationPayload> authorizeByVk(@RequestBody AuthorizationPayload authorizationPayload){
        return authorizationService.handleAuthorizeByVk(authorizationPayload);
    }

    @GetMapping("/info")
    public Mono<UserDto> getInfoByAccessToken(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken){
        return authorizationService.getInfoByAccessToken(Long.parseLong(usernamePasswordAuthenticationToken.getName()));
    }
}
