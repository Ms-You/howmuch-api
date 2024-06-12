package com.ductchpay.howmuch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "restaurant")
@Entity
public class Restaurant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "restaurant", orphanRemoval = true)
    private List<Menu> menuList = new ArrayList<>();


    @Builder
    private Restaurant(String name) {
        this.name = name;
    }

}
