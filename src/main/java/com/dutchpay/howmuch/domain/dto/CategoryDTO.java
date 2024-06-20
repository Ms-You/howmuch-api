package com.dutchpay.howmuch.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class CategoryDTO {
    @Getter
    @NoArgsConstructor
    public static class CreateReq {
        private String name;
        private int code;
    }

}
