package com.dutchpay.howmuch.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CategoryDTO {
    @Getter
    @NoArgsConstructor
    public static class CreateReq {
        private String name;
        private int code;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InfoResp {
        private Long categoryId;
        private String name;
        private int code;
    }
}
