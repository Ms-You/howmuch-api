package com.ductchpay.howmuch.config.jwt;

import com.ductchpay.howmuch.common.exception.ErrorCode;
import com.ductchpay.howmuch.common.exception.GlobalException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";   // 토큰의 Claim 에서 사용자 권한 정보(role)를 저장하기 위한 키로 사용
    private static final long ACCESS_TOKEN_EXPIRES_IN = 1000 * 60 * 60; // 1시간
    private static final long REFRESH_TOKEN_EXPIRES_IN = 1000 * 60 * 60 * 24 * 7;   // 7일
    private final Key key;  // JWT 서명에 사용되는 비밀 키

    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        // 주입받은 비밀 키를 BASE64 URL 형식으로 디코딩함
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        // 디코딩된 키를 사용하여 HMAC-SHA 알고리즘을 위한 Key 객체를 생성함
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Authentication 객체를 전달 받아 토큰 생성
     * @param authentication
     * @return
     */
    public TokenDTO generateToken(Authentication authentication) {
        // 사용자의 권한 정보 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // accessToken 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRES_IN))
                .signWith(key, SignatureAlgorithm.HS256)
                .setIssuer("how-much")
                .setIssuedAt(new Date(now))
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRES_IN))
                .signWith(key, SignatureAlgorithm.HS256)
                .setIssuer("how-much")
                .setIssuedAt(new Date(now))
                .compact();

        return new TokenDTO(accessToken, refreshToken);
    }

    /**
     * AccessToken 토큰으로 Authentication 객체 추출
     * @param accessToken
     * @return
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if(claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // Claim 객체에서 사용자 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체로 인증 객체 리턴
        // JwtAuthenticationFilter 에서 검증했으므로 비밀번호는 필요 없음
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 토큰 복호화
     * @param accessToken
     * @return
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * 토큰 검증 메서드
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("유효하지 않은 토큰입니다.", e);
            throw new GlobalException(ErrorCode.ACCESS_TOKEN_NOT_VALIDATE);
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다.", e);
            throw new GlobalException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 토큰입니다.", e);
            throw new GlobalException(ErrorCode.ACCESS_TOKEN_UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            log.info("JWT 클레임 문자열이 비었습니다.", e);
            throw new GlobalException(ErrorCode.ACCESS_TOKEN_CLAIM_EMPTY);
        }
    }

    /**
     * 토큰 만료 시간 반환
     * @param accessToken
     * @return
     */
    public Long getExpiration(String accessToken) {
        Claims claims = parseClaims(accessToken);

        return claims.getExpiration().getTime();
    }

}
