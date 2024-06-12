package com.ductchpay.howmuch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "menu")
@Entity
public class Menu {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    private String name;
    private int price;
    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;


    @Builder
    private Menu(String name, int price, int count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public void settingRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;

        restaurant.getMenuList().add(this);
    }
}
