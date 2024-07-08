package com.dutchpay.howmuch.common.file;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileHandler {

    /**
     * 업로드 경로
     * @param filename
     * @param dirname
     * @return
     */
    public Path getUploadPath(String filename, String dirname) {
        // 파일을 저장할 경로를 반환 (dirname/filename)
        return Paths.get(dirname, filename);
    }

    /**
     * 이미지 여러 장 업로드
     * @param multipartFiles
     * @param dirname
     * @return
     * @throws IOException
     */
    public List<String> uploadImages(List<MultipartFile> multipartFiles, String dirname) throws IOException {
        List<String> saveFilenames = new ArrayList<>();

        for(MultipartFile multipartFile : multipartFiles) {
            saveFilenames.add(uploadImage(multipartFile, dirname));
        }

        return saveFilenames;
    }

    /**
     * 이미지 업로드
     * @param multipartFile
     * @param relativeDir: 상대 경로 사용
     * @return
     * @throws IOException
     */
    public String uploadImage(MultipartFile multipartFile, String relativeDir) throws IOException {
        // 파일이 없을 때 null 반환
        if(multipartFile.isEmpty()) {
            return null;
        }

        // 원본 파일명
        String originalFilename = multipartFile.getOriginalFilename();
        // 업로드할 파일명
        String saveFilename = convertFilename(originalFilename);

        // 디렉토리 경로 생성
        Path dirPath = Paths.get(System.getProperty("user.dir"), relativeDir);
        // 디렉토리가 없다면 생성
        if(!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 파일을 저장할 경로
        Path uploadPath = dirPath.resolve(saveFilename);
        // 파일을 해당 경로에 저장
        multipartFile.transferTo(uploadPath.toFile());

        return saveFilename;
    }

    /**
     * UUID 사용해서 파일명 중복을 피함 (UUID 랜덤값.확장자)
     * @param originalFilename
     * @return
     */
    private String convertFilename(String originalFilename) {
        // UUID 랜덤값
        String uuid = UUID.randomUUID().toString();
        // 추출한 확장자
        String ext = extractExt(originalFilename);

        return uuid + ext;
    }

    /**
     * 원본 파일명에서 확장자 추출
     * @param originalFilename
     * @return
     */
    private String extractExt(String originalFilename) {
        // 마지막 .의 인덱스
        int pointIdx = originalFilename.lastIndexOf(".");

        // 확장자 추출 (없을 경우 "" 반환)
        return (pointIdx == -1) ? "" : originalFilename.substring(pointIdx);
    }

    /**
     * 이미지 삭제
     * @param filename
     * @param dirname
     * @return
     * @throws IOException
     */
    public boolean deleteImage(String filename, String dirname) throws IOException {
        // 삭제할 파일 경로
        Path deletePath = getUploadPath(filename, dirname);

        // 파일이 경로에 존재하는지 확인
        if(Files.exists(deletePath)) {
            // 파일 삭제
            Files.deleteIfExists(deletePath);
            return true;
        }

        return false;
    }

}
