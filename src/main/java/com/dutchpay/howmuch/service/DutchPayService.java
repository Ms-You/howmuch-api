package com.dutchpay.howmuch.service;

import com.dutchpay.howmuch.domain.dto.DutchPayDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Transactional
@Service
public class DutchPayService {

    /**
     * 참석자 별 내야할 금액 산정
     * @param dutchPayReq
     * @return
     */
    public List<DutchPayDTO.DutchPayResp> dutchPay(DutchPayDTO.DutchPayReq dutchPayReq) {
        // (메뉴, 총 가격)을 저장 (총 가격 = price * quantity)
        Map<String, Integer> menuMap = new HashMap<>();
        // (참석자, 내야할 금액)을 저장
        Map<String, BigDecimal> attendeePriceMap = new HashMap<>();

        // 메뉴 별 총 가격 계산
        for(DutchPayDTO.MenuReq menu : dutchPayReq.getMenus()) {
            menuMap.put(menu.getName(), menu.getPrice() * menu.getQuantity());
        }

        // 참석자 별 내야할 금액 계산
        // 참석자가 소비한 메뉴 확인해서 메뉴 가격을 소비한 참석자의 수로 나눔
        for(Map.Entry<String, List<DutchPayDTO.AttendeeMenuReq>> entry : dutchPayReq.getAttendeeMenus().entrySet()) {
            String attendeeName = entry.getKey();   // 참석자 명
            List<DutchPayDTO.AttendeeMenuReq> attendeeMenuReqList = entry.getValue();   // 참석자가 소비한 메뉴 목록
            BigDecimal totalPrice = BigDecimal.ZERO;    // 정확한 계산을 위해 BigDecimal 사용

            for(DutchPayDTO.AttendeeMenuReq attendeeMenuReq : attendeeMenuReqList) {
                String menuName = attendeeMenuReq.getName();    // 참석자가 소비한 메뉴 명

                if(menuMap.containsKey(menuName)) {
                    int menuPrice = menuMap.get(menuName);  // 해당 메뉴의 총 가격

                    // 해당 메뉴를 소비한 참석자 수
                    long attendeeCnt = dutchPayReq.getAttendeeMenus().values().stream()
                            .flatMap(List::stream)
                            .filter(attendeeMenu -> attendeeMenu.getName().equals(menuName))
                            .count();

                    // 참석자 별로 내야할 금액 계산
                    BigDecimal pricePerAttendee = BigDecimal.valueOf(menuPrice)
                            .divide(BigDecimal.valueOf(attendeeCnt), 1, RoundingMode.HALF_UP); // 소수점 둘 째자리에서 반올림

                    totalPrice = totalPrice.add(pricePerAttendee);
                }
            }

            attendeePriceMap.put(attendeeName, totalPrice);
        }

        // 참석자 별로 내야할 금액 산정
        List<DutchPayDTO.DutchPayResp> dutchPayRespList = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : attendeePriceMap.entrySet()) {
            dutchPayRespList.add(new DutchPayDTO.DutchPayResp(entry.getKey(), entry.getValue()));
        }

        return dutchPayRespList;
    }

}
