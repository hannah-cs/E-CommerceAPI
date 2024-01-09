package com.startsteps.Final.Project.ECommerce.JWTConfig;

import io.jsonwebtoken.*;

import java.util.Date;

public class JwtTokenProvider {

    private final String secret;
    private final long expiration;

    public JwtTokenProvider(String secret, long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String generateToken(Integer userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getIdFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            // Invalid signature/claims
        } catch (ExpiredJwtException ex) {
            // Expired token
        } catch (UnsupportedJwtException ex) {
            // Unsupported JWT token
        } catch (MalformedJwtException ex) {
            // Malformed JWT token
        } catch (IllegalArgumentException ex) {
            // JWT token is empty
        }
        return false;
    }

}
