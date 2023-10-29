package ru.marthastudios.coinjoiner.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.marthastudios.coinjoiner.security.jwt.JwtUtil;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        Long userId;

        try {
            userId = jwtUtil.extractIdFromAccessToken(token);
        }catch (Exception e){
            userId = null;
        }

        if (userId == null){
            return Mono.empty();
        }


        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        null
                );

        return Mono.just(usernamePasswordAuthenticationToken);
    }
}