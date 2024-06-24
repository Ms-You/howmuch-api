package com.dutchpay.howmuch.service;

import com.dutchpay.howmuch.common.exception.ErrorCode;
import com.dutchpay.howmuch.common.exception.GlobalException;
import com.dutchpay.howmuch.common.file.FileHandler;
import com.dutchpay.howmuch.domain.Category;
import com.dutchpay.howmuch.domain.dto.CategoryDTO;
import com.dutchpay.howmuch.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final FileHandler fileHandler;
    private static final String CATEGORY_IMAGE_PATH = "uploads/category";
    private static final Pattern KOREAN_PATTERN = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]+");

    /**
     * 카테고리 등록
     * @param multipartFile
     * @param createReq
     */
    public void enrollNewCategory(MultipartFile multipartFile, CategoryDTO.CreateReq createReq) throws IOException {
        if(categoryRepository.findByName(createReq.getName()).isPresent()) {
            throw new GlobalException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        String savedFilename = fileHandler.uploadImage(multipartFile, CATEGORY_IMAGE_PATH);

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
                        .path(encodePath(category.getPath()))
                        .url(CATEGORY_IMAGE_PATH + "/" + category.getUrl())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 경로가 한글이면 인코딩해서 응답
     * @param path
     * @return
     */
    private String encodePath(String path) {
        try {
            // path 에 한글이 포함되어 있는지 확인
            if(KOREAN_PATTERN.matcher(path).find()) {   // 한글이 있으면 인코딩 후 전달
                return URLEncoder.encode(path, StandardCharsets.UTF_8.toString());
            } else {
                return path;
            }
        } catch (UnsupportedEncodingException e) {
            log.error("카테고리 path 인코딩 실패", e);
            throw new GlobalException(ErrorCode.URL_ENCODING_FAILED);
        }
    }

}
