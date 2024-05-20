package com.explorer.user.global.component.jwt;

import com.explorer.user.global.common.dto.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProps props;

    private static final String CLAIM_NICKNAME = "nickname";
    private static final String CLAIM_AVATAR = "avatar";

    public String issueAccessToken(Long userId, String nickname, int avatar) {
        Claims claims = Jwts.claims()
                .id(String.valueOf(userId))
                .add(CLAIM_NICKNAME, nickname)
                .add(CLAIM_AVATAR, avatar)
                .build();

        return issueToken(claims, props.accessExpiration(), props.accessKey());
    }

    public String issueRefreshToken() {
        return issueToken(null, props.accessExpiration(), props.refreshKey());
    }

    private String issueToken(Claims claims, Duration expiration, String secretKey) {
        Date now = new Date();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration.toMillis()))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public UserInfo parseAccessTokenByBase64(String accessToken) {
        String payload = accessToken.split("\\.")[1];

        String decodePayload = new String(Base64.getUrlDecoder().decode(payload));

        BasicJsonParser jsonParser = new BasicJsonParser();

        Map<String, Object> map = jsonParser.parseMap(decodePayload);

        return UserInfo.builder()
                .userId(Long.valueOf((String) (map.get("jti"))))
                .nickname((String) map.get(CLAIM_NICKNAME))
                .avatar(Integer.parseInt((String.valueOf(map.get(CLAIM_AVATAR)))))
                .build();
    }

    private Claims getAccessTokenPayload(String accessToken) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(props.accessKey().getBytes()))
                .build()
                .parseSignedClaims(accessToken).getPayload();
    }

    public Date getAccessTokenExpirationDate(String accessToken) {
        return getAccessTokenPayload(accessToken).getExpiration();
    }

    private Claims getRefreshTokenPayload(String refreshToken) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(props.refreshKey().getBytes()))
                .build()
                .parseSignedClaims(refreshToken).getPayload();
    }

    public Date getRefreshTokenExpirationDate(String refreshToken) {
        return getRefreshTokenPayload(refreshToken).getExpiration();
    }

}
