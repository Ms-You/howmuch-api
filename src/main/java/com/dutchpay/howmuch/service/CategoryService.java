package com.dutchpay.howmuch.service;

import com.dutchpay.howmuch.common.exception.ErrorCode;
import com.dutchpay.howmuch.common.exception.GlobalException;
import com.dutchpay.howmuch.common.file.FileHandler;
import com.dutchpay.howmuch.domain.Category;
import com.dutchpay.howmuch.domain.dto.CategoryDTO;
import com.dutchpay.howmuch.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final FileHandler fileHandler;

    /**
     * 카테고리 등록
     * @param multipartFile
     * @param createReq
     */
    public void enrollNewCategory(MultipartFile multipartFile, CategoryDTO.CreateReq createReq) throws IOException {
        if(categoryRepository.findByName(createReq.getName()).isPresent()) {
            throw new GlobalException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        String savedFilename = fileHandler.uploadImage(multipartFile, "uploads/category");

        Category category = Category.builder()
                .name(createReq.getName())
                .path(generatePath(createReq.getName()))
                .url(savedFilename)
                .build();

        categoryRepository.save(category);
    }

    /**
     * 카테고리 명으로 path 생성 (대문자 -> 소문자, 공백 -> 하이픈으로 변환)
     * @param name
     * @return
     */
    private String generatePath(String name) {
        return "/categories/" + name.toLowerCase().replace(" ", "-");
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
                        .path(category.getPath())
                        .url(category.getUrl())
                        .build())
                .collect(Collectors.toList());
    }

}
