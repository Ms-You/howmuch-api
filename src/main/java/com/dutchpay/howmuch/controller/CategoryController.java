package com.dutchpay.howmuch.controller;

import com.dutchpay.howmuch.common.response.BasicResponse;
import com.dutchpay.howmuch.domain.dto.CategoryDTO;
import com.dutchpay.howmuch.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<BasicResponse> enrollCategory(@RequestPart(name = "image", required = true) MultipartFile multipartFile,
                                                        @RequestPart(name = "categoryReq", required = true) CategoryDTO.CreateReq createReq) throws IOException {
        categoryService.enrollNewCategory(multipartFile, createReq);

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
