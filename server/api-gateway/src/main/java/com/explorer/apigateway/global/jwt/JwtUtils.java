package com.explorer.apigateway.global.jwt;

import com.explorer.apigateway.global.common.dto.TokenInfo;
import com.explorer.apigateway.global.jwt.repository.BlackListRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProps props;
    private final BlackListRepository blackListRepository;

    private static final String CLAIM_NICKNAME = "nickname";
    private static final String CLAIM_AVATAR = "avatar";

    public TokenInfo parseToken(String token) {
        String userId = getPayload(token).getId();

        String nickname = (String) getPayload(token).get(CLAIM_NICKNAME);
        int avatar = (int) getPayload(token).get(CLAIM_AVATAR);

        return TokenInfo.builder()
                .userId(userId)
                .nickname(nickname)
                .avatar(avatar)
                .build();
    }

    private Claims getPayload(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(props.accessKey().getBytes()))
                .build()
                .parseSignedClaims(token).getPayload();
    }

    public Date getExpirationDate(String token) {
        return getPayload(token).getExpiration();
    }

    public boolean isBlackListed(String token) {
        return blackListRepository.exist(token);
    }

}
