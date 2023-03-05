package com.evaluation.globallogic.config;

import com.evaluation.globallogic.entity.Account;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtProvider {

    private final TokenProperties tokenProperties;

    @Autowired
    public JwtProvider(TokenProperties tokenProperties) {
        this.tokenProperties = tokenProperties;
    }

    public String generateToken(Account account) {
        val now = Instant.now();
        val jwtBuilder = Jwts.builder()
                .setSubject(account.getEmail())
                .setIssuedAt(Date.from(now));
        Optional.ofNullable(account.getLastLogin())
                .ifPresent(lastLogin -> jwtBuilder.claim("lastLogin", lastLogin));
        return jwtBuilder
                .setExpiration(Date.from(now.plusMillis(tokenProperties.getExpiration())))
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getSecret())
                .compact();
    }

    String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(tokenProperties.getSecret())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    String getToken(HttpServletRequest request) {
        val authReq = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authReq != null && authReq.startsWith(tokenProperties.getPrefix()))
            return authReq.replace(tokenProperties.getPrefix(), "");
        return null;
    }
}
