package back.Quest.service.imgQuiz.impl;

import back.Quest.config.exception.CustomException;
import back.Quest.mapper.imgQuiz.ImgQuizMapper;
import back.Quest.model.Enum.ValidationStatus;
import back.Quest.model.dto.imgQuiz.ImgQuizDto;
import back.Quest.service.imgQuiz.ImgQuizService;
import back.Quest.service.imgQuiz.assembler.ImgQuizAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImgQuizServiceImpl implements ImgQuizService {
    private final ImgQuizMapper imgQuizMapper;


    @Override
    public void insertImgQuiz(Long memberNo, MultipartFile image, ImgQuizDto.ImgQuizRequest request) {
        log.info("Insert ImgQuiz Request");

        if (memberNo == null) {
            throw new CustomException.InvalidRequestException("빈칸 오류");
        }

        String imgUrl = saveImg(image);
        imgQuizMapper.insertImgQuiz(memberNo, imgUrl, request);
        Long imgQuizNo = imgQuizMapper.selectLastId();

        int imgDistractNo = imgQuizMapper.insertImgDistract(imgQuizNo, request.list());
        if (imgQuizNo == 0 || imgDistractNo == 0) {
            log.error("Insert Fail");
            throw new CustomException.InvalidRequestException("생성 오류");
        }

        log.info("Insert ImgQuiz Success");
    }

    @Override
    public void deleteImgQuiz(Long memberNo, Long imgQuizNo) {
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

        log.info("");
        return ImgQuizAssembler.toGroup(response);
    }


    @Override
    public ImgQuizDto.ImgQuizResponse findById(Long imgQuizNo) {
        List<ImgQuizDto.ImgQuizFlatResponse> response = imgQuizMapper.findById(imgQuizNo);
        if (response == null || response.isEmpty()) {
            return null;
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


    private String saveImg(MultipartFile image) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/imgQuiz/";
            File dir = new File(uploadDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            String filePath = uploadDir + fileName;
            image.transferTo(new File(filePath));

            return "/imgQuiz/" + filePath;
        } catch (IOException e) {
            log.error("저장 실패 : {}",e.getMessage());
            throw new CustomException.InvalidRequestException("이미지 저장 실패");
        }
    }

}
