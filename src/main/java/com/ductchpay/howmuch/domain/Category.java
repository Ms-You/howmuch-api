package com.ductchpay.howmuch.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    DUTCH_PAY("단순 더치페이", 1),
    BAR("술집", 2),
    RESTAURANT("식당", 3),
    GROCERY("식자재", 4),
    ;

    private String name;
    private int code;
}
