package app.configs.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import app.configs.security.TokenInfo;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JwtHelper {

    private final SecretKey key;

    public JwtHelper(String key) {
        this.key = Keys.hmacShaKeyFor(key.getBytes());
    }

    public String encode(TokenInfo tokenInfo) {
        return encode(tokenInfo, LocalDateTime.now().plusMinutes(15));
    }

    public String encode(TokenInfo tokenInfo, LocalDateTime expiration) {
        return Jwts.builder()
                .signWith(key, Jwts.SIG.HS512)
                .subject(tokenInfo.getUsername())
                .expiration(asDate(expiration))
                .claim("roles", tokenInfo.getRolesAsString())
                .compact();

    }

    public TokenInfo decode(String token) {

        token = token.replace("Bearer ", "");

        Claims body = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new TokenInfo(
                body.getSubject(),
                body.get("roles", String.class));
    }

    private Date asDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
