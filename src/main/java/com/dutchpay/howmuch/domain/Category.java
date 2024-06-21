package com.dutchpay.howmuch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
@Entity
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;
    private String path;   // 동적 라우팅을 위한 경로
    private String url; // 이미지 경로


    @Builder
    private Category(String name, String path, String url) {
        this.name = name;
        this.path = path;
        this.url = url;
    }

}
