package com.dutchpay.howmuch.controller;

import com.dutchpay.howmuch.common.response.BasicResponse;
import com.dutchpay.howmuch.config.jwt.TokenDTO;
import com.dutchpay.howmuch.domain.dto.MemberDTO;
import com.dutchpay.howmuch.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    /**
     * 회원가입
     * @param signUpReq
     * @return
     */
    @PostMapping("/auth")
    public ResponseEntity<BasicResponse> signUp(@RequestBody MemberDTO.SignUpReq signUpReq) {
        authService.register(signUpReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.CREATED.value(), "회원가입 성공"));
    }

    /**
     * 로그인
     * @param signInReq
     * @param response
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<BasicResponse> signIn(@RequestBody MemberDTO.SignInReq signInReq, HttpServletResponse response) {
        TokenDTO tokenDTO = authService.login(signInReq);

        ResponseCookie cookie = ResponseCookie.from("RefreshToken", tokenDTO.getRefreshToken())
                .maxAge(60 * 60 * 24 * 7)   // 7일
                .path("/")
                // Https 환경에서만 동작
//                .secure(true)
//                .sameSite("None")
                .httpOnly(true)
                .build();

        response.setHeader("Authorization", "Bearer " + tokenDTO.getAccessToken());
        response.setHeader("Set-Cookie", cookie.toString());


        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "로그인 성공"));
    }

    /**
     * 로그아웃
     * @param accessToken
     * @return
     */
    @PostMapping("/member/logout")
    public ResponseEntity<BasicResponse> logout(@RequestHeader("Authorization") String accessToken) {
        if(StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
            authService.logout(accessToken.substring(7));
        }

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "로그아웃 되었습니다."));
    }

    /**
     * 토큰 재발급
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/reissue")
    public ResponseEntity<BasicResponse> reissue(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if("RefreshToken".equals(cookie.getName())) {
                    String refreshToken = cookie.getValue();

                    TokenDTO tokenDTO = authService.reissue(refreshToken);

                    ResponseCookie responseCookie = ResponseCookie.from("RefreshToken", tokenDTO.getRefreshToken())
                            .maxAge(60 * 60 * 24 * 7)   // 7일
                            .path("/")
                            .httpOnly(true)
                            .build();

                    response.setHeader("Authorization", "Bearer " + tokenDTO.getAccessToken());
                    response.setHeader("Set-Cookie", responseCookie.toString());

                    return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "토큰이 재발급되었습니다."));
                }
            }
        }

        return ResponseEntity.ok(new BasicResponse(HttpStatus.FORBIDDEN.value(), "토큰을 재발급할 수 없습니다."));
    }

}
