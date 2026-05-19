package roomescape.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret:intelligent-life-on-a-planet-comes-of-age-when-it-first-works-out-the-reason-for-its-own-existence}") String secret,
            @Value("${jwt.access-token-expiration-seconds:3600}") long accessTokenExpirationSeconds
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("jwt.secret는 32자 이상이어야 합니다.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationSeconds * 1000L;
    }

    public String createAccessToken(Long userId, String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpirationMs);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extractUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    public String extractEmail(String token) {
        Claims claims = parseClaims(token);
        return claims.get("email", String.class);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isExpiredToken(String token) {
        try {
            parseClaims(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public String resolveToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }
}
