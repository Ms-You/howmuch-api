package com.dutchpay.howmuch.controller;

import com.dutchpay.howmuch.common.response.BasicResponse;
import com.dutchpay.howmuch.domain.dto.CategoryDTO;
import com.dutchpay.howmuch.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * 새로운 카테고리 등록
     * @param createReq
     * @return
     */
    @PostMapping("/admin/category")
    public ResponseEntity<BasicResponse> enrollCategory(@RequestBody CategoryDTO.CreateReq createReq) {
        categoryService.enrollNewCategory(createReq);

        return ResponseEntity.ok(new BasicResponse(HttpStatus.CREATED.value(), "카테고리가 등록되었습니다."));
    }

    /**
     * 카테고리 목록 조회
     * @return
     */
    @GetMapping("/category")
    public ResponseEntity<BasicResponse> getCategoryList() {
        List<CategoryDTO.InfoResp> categoryList = categoryService.getCategoryList();

        return ResponseEntity.ok(new BasicResponse(HttpStatus.OK.value(), "카테고리 목록 조회", categoryList));
    }


}
