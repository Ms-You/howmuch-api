package com.dutchpay.howmuch.controller;

import com.dutchpay.howmuch.common.response.BasicResponse;
import com.dutchpay.howmuch.domain.dto.DutchPayDTO;
import com.dutchpay.howmuch.service.DutchPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DutchPayController {
    private final DutchPayService dutchPayService;

    /**
     * 더치페이 로직 수행
     * @param dutchPayReq
     * @return
     */
    @PostMapping("/dutch-pay")
    public ResponseEntity<BasicResponse> dutchPay(@RequestBody DutchPayDTO.DutchPayReq dutchPayReq) {
        List<DutchPayDTO.DutchPayResp> dutchPayRespList = dutchPayService.dutchPay(dutchPayReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "더치페이 로직 수행", dutchPayRespList));
    }

}
