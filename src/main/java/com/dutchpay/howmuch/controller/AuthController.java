package com.dutchpay.howmuch.controller;

import com.dutchpay.howmuch.common.response.BasicResponse;
import com.dutchpay.howmuch.config.jwt.TokenDTO;
import com.dutchpay.howmuch.domain.dto.MemberDTO;
import com.dutchpay.howmuch.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
}
