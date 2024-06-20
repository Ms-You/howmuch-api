package com.dutchpay.howmuch.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."),
    // 405
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "허용되지 않은 요청입니다."),
    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "내부 서버 오류입니다."),


    // 토큰 에러
    ACCESS_TOKEN_NOT_VALIDATE(600, "토큰이 유효하지 않습니다."),
    ACCESS_TOKEN_EXPIRED(601, "만료된 토큰입니다."),
    ACCESS_TOKEN_UNSUPPORTED(602, "지원하지 않는 토큰입니다."),
    ACCESS_TOKEN_CLAIM_EMPTY(603, "JWT 클레임 문자열이 비었습니다."),
    REFRESH_TOKEN_NOT_VALIDATE(700, "리프레시 토큰이 유효하지 않습니다."),
    REFRESH_TOKEN_NOT_MATCHED(701, "리프레시 토큰이 일치하지 않습니다."),


    // Member Error
    ID_OR_PASSWORD_WRONG(HttpStatus.BAD_REQUEST.value(), "아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 이메일입니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다."),


    // Category Error
    CATEGORY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 카테고리입니다."),
    ;

    private int code;
    private String message;
}
