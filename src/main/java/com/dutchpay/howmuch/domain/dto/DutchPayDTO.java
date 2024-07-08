package com.dutchpay.howmuch.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DutchPayDTO {
    @Getter
    @NoArgsConstructor
    public static class DutchPayReq {
        private List<AttendeeReq> attendees;    // 참석자 목록
        private List<MenuReq> menus;    // 메뉴 목록
        private Map<String, List<AttendeeMenuReq>> attendeeMenus;  // 참석자 별 메뉴 목록
    }

    @Getter
    @NoArgsConstructor
    public static class AttendeeReq {
        private String name;    // 참석자 명
    }

    @Getter
    @NoArgsConstructor
    public static class MenuReq {
        private String name;    // 메뉴 명
        private int price;  // 메뉴 가격
        private int quantity;   // 메뉴 수량
    }

    @Getter
    @NoArgsConstructor
    public static class AttendeeMenuReq {
        private String name;    // 메뉴 명
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DutchPayResp {
        private String name;    // 참석자 명
        private BigDecimal price;   // 참석자 별 내야할 금액
    }
}
