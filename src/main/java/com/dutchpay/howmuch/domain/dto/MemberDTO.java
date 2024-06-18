package com.dutchpay.howmuch.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDTO {
    @Getter
    @NoArgsConstructor
    public static class SignUpReq {
        private String email;
        private String password;
        private String passwordConfirm;
        private String name;
        private int age;
        private String gender;
    }

    @Getter
    @NoArgsConstructor
    public static class SignInReq {
        private String email;
        private String password;
    }
}
