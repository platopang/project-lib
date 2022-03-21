package com.example.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final long ACCESS_TOKEN_VALIDITY_IN_SECOND = 24 * 60 * 60L;
    private static final long REFRESH_TOKEN_VALIDITY_IN_SECOND = 365 * 24 * 60 * 60L;
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

    public enum TokenType {
        ACCESS, REFRESH
    }

    public static String generateToken(UserDetails userDetails, String tokenCode, TokenType tokenType, String signingSecret) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", tokenType);
        claims.put("tokenCode", tokenCode);
        return doGenerateToken(claims, userDetails.getUsername(), tokenType, signingSecret);
    }

    private static String doGenerateToken(Map<String, Object> claims, String subject, TokenType tokenType, String signingSecret) {
        long tokenValiditySecond = tokenType.equals(TokenType.ACCESS) ? ACCESS_TOKEN_VALIDITY_IN_SECOND : REFRESH_TOKEN_VALIDITY_IN_SECOND;
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValiditySecond * 1000))
                .signWith(getSigningKey(signingSecret), signatureAlgorithm);

        return builder.compact();
    }

    private static Key getSigningKey(String signingSecret) {
        byte[] apiKeySecretBytes = Base64.encodeBase64(signingSecret.getBytes());
        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }

    public static String getUsernameFromToken(String token, String signingSecret) {
        return getClaimFromToken(token, Claims::getSubject, signingSecret);
    }

    public static TokenType getTokenTypeFromToken(String token, String signingSecret) {
        final Claims claims = getAllClaimsFromToken(token, signingSecret);
        String tokenType = (String) claims.get("tokenType");
        return TokenType.valueOf(tokenType);
    }

    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver, String signingSecret) {
        final Claims claims = getAllClaimsFromToken(token, signingSecret);
        return claimsResolver.apply(claims);
    }

    private static Claims getAllClaimsFromToken(String token, String signingSecret) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(signingSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
