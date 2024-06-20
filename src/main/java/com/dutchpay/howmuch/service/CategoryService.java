package com.dutchpay.howmuch.service;

import com.dutchpay.howmuch.common.exception.ErrorCode;
import com.dutchpay.howmuch.common.exception.GlobalException;
import com.dutchpay.howmuch.domain.Category;
import com.dutchpay.howmuch.domain.dto.CategoryDTO;
import com.dutchpay.howmuch.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 등록
     * @param createReq
     */
    public void enrollNewCategory(CategoryDTO.CreateReq createReq) {
        if(categoryRepository.findByName(createReq.getName()).isPresent()) {
            throw new GlobalException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        Category category = Category.builder()
                .name(createReq.getName())
                .code(createReq.getCode())
                .build();

        categoryRepository.save(category);
    }

    /**
     * 카테고리 목록 조회
     * @return
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO.InfoResp> getCategoryList() {
        return categoryRepository.findAll().stream()
                .map(category -> CategoryDTO.InfoResp.builder()
                        .categoryId(category.getId())
                        .name(category.getName())
                        .code(category.getCode())
                        .build())
                .collect(Collectors.toList());
    }

}
