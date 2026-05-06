package back.Quest.service.imgQuiz.impl;

import back.Quest.config.exception.CustomException;
import back.Quest.mapper.imgQuiz.ImgQuizMapper;
import back.Quest.model.Enum.ValidationStatus;
import back.Quest.model.dto.imgQuiz.ImgQuizDto;
import back.Quest.service.imgQuiz.ImgQuizService;
import back.Quest.service.imgQuiz.assembler.ImgQuizAssembler;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImgQuizServiceImpl implements ImgQuizService {
    private final ImgQuizMapper imgQuizMapper;

    @Value("${firebase.bucket}")
    private String firebaseBucket;


    @Override
    public void insertImgQuiz(Long memberNo, MultipartFile image, ImgQuizDto.ImgQuizRequest request) {
        log.info("Insert ImgQuiz Request");

        if (memberNo == null) {
            throw new CustomException.InvalidRequestException("빈칸 오류");
        }

        String filePath = "imgQuiz/" + UUID.randomUUID() + "_" + image.getOriginalFilename();
        String imgUrl = uploadImg(filePath, image);

        try {
            imgQuizMapper.insertImgQuiz(memberNo, imgUrl, request);
            Long imgQuizNo = imgQuizMapper.selectLastId();

            int imgDistractNo = imgQuizMapper.insertImgDistract(imgQuizNo, request.list());
            if (imgQuizNo == 0 || imgDistractNo == 0) {
                log.error("Insert Fail");
                throw new CustomException.InvalidRequestException("생성 오류");
            }
        } catch (Exception e) {
            // DB 저장 실패 시 삭제
            deleteImg(filePath);
            throw e;
        }

        log.info("Insert ImgQuiz Success");
    }

    @Override
    @CacheEvict(value = "imgQuizList", key = "#imgQuizNo")
    public void deleteImgQuiz(Long memberNo, Long imgQuizNo) {
        List<ImgQuizDto.ImgQuizFlatResponse> list = imgQuizMapper.findById(imgQuizNo);
        if (list == null || list.isEmpty()) {
            throw new CustomException.NotFoundException("존재하지 않는 퀴즈입니다.");
        }
        if (!list.get(0).memberNo().equals(memberNo)) {
            throw new CustomException.InvalidRequestException("권한 없음");
        }

        int deleteImgCount = imgQuizMapper.deleteImgQuiz(memberNo, imgQuizNo);
        if (deleteImgCount == 0) {
            throw new CustomException.InvalidRequestException("삭제 실패");
        }
        log.info("Delete ImgQuiz Success");
    }

    @Override
    public List<ImgQuizDto.ImgQuizResponse> myQuiz(Long memberNo) {
        List<ImgQuizDto.ImgQuizFlatResponse> response = imgQuizMapper.myQuiz(memberNo);
        if (response == null || response.isEmpty()) {
            return Collections.emptyList();
        }

        log.info("MyImgQuiz find Response Success");
        return ImgQuizAssembler.toGroup(response);
    }


    @Override
    @Cacheable(value = "imgQuizList", key = "#imgQuizNo")
    public ImgQuizDto.ImgQuizResponse findById(Long imgQuizNo) {
        List<ImgQuizDto.ImgQuizFlatResponse> response = imgQuizMapper.findById(imgQuizNo);
        if (response == null || response.isEmpty()) {
            throw new CustomException.NotFoundException("퀴즈 없음");
        }

        ImgQuizDto.ImgQuizResponse imgResponse = ImgQuizAssembler.toGroup(response).getFirst();
        log.info("Success");
        return imgResponse;
    }

    @Override
    public ImgQuizDto.ImgSolveResponse solveImgQuiz(Long memberNo, Long imgQuizNo, ImgQuizDto.ImgSolveRequest request) {
        ImgQuizDto.ImgQuizResponse imgQuiz = findById(imgQuizNo);

        List<ImgQuizDto.ImgQuizDistractResponse> distract = imgQuiz.list();
        if (distract == null || distract.isEmpty()) {
            log.warn("Distract Not Found");
            throw new CustomException.NotFoundException("존재하지 않음");
        }

        int answer = request.userAnswer();
        if (answer < 1 || answer > distract.size()) {
            throw new CustomException.InvalidRequestException("유효하지 않은 정답입니다.");
        }
        boolean isCorrect = distract.get(answer - 1).validation() == ValidationStatus.CORRECT;
        return new ImgQuizDto.ImgSolveResponse(
                isCorrect, isCorrect ? "정답입니다." : "오답입니다."
        );
    }


    private String uploadImg(String filePath, MultipartFile image) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            bucket.create(filePath, image.getBytes(), image.getContentType());

            String encodedPath = URLEncoder.encode(filePath, StandardCharsets.UTF_8);
            return "https://firebasestorage.googleapis.com/v0/b/"
                    + firebaseBucket
                    + "/o/" + encodedPath
                    + "?alt=media";

        } catch (IOException e) {
            log.error("Firebase 저장 실패 : {}", e.getMessage());
            throw new CustomException.InvalidRequestException("이미지 저장 실패");
        }
    }

    private void deleteImg(String filePath) {
        try {
            Blob blob = StorageClient.getInstance().bucket().get(filePath);
            if (blob != null) {
                blob.delete();
                log.info("Firebase 파일 롤백 완료: {}", filePath);
            }
        } catch (Exception e) {
            log.error("Firebase 파일 롤백 실패 (수동 삭제 필요): {}", filePath);
        }
    }
}
