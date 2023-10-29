package ru.marthastudios.coinjoiner.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.marthastudios.coinjoiner.model.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;

    public String generateAccessToken(User user){
        Map<String, Object> claims = new HashMap<>();

        claims.put("name", user.getUsername());

        return createAccessToken(claims, user.getId());
    }

    public Long extractIdFromAccessToken(String token){
        return extractAccessClaim(token, claims -> Long.parseLong(claims.getSubject()));
    }

    private Claims extractAccessAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes()).build().parseClaimsJws(token).getBody();
    }

    public <T> T extractAccessClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAccessAllClaims(token);
        return claimsResolver.apply(claims);
    }


    private String createAccessToken(Map<String, Object> claims, long subject){
        Date dateNow = new Date();
        Date expirationDate = new Date(dateNow.getTime() + 1000 * 60 * 60 * 23);

        byte[] signingKeyBytes = jwtSecret.getBytes();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setSubject(String.valueOf(subject))
                .setIssuedAt(dateNow)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(signingKeyBytes), SignatureAlgorithm.HS256)
                .compact();
    }
}