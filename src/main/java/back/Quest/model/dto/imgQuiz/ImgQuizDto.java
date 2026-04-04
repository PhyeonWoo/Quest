package back.Quest.model.dto.imgQuiz;

import back.Quest.model.Enum.ValidationStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public class ImgQuizDto {

    // 이미지 퀴즈 추가용
    public record ImgQuizRequest(
            Long imgQuizNo,
            String question,
            List<ImgQuizDistractRequest> list
    ) {}
    public record ImgQuizDistractRequest(
            Long imgDistractNo,
            ValidationStatus validation,
            String text
    ) {}


    //Controller Response
    @JsonDeserialize
    public record ImgQuizResponse(
            Long imgQuizNo,
            Long memberNo,
            String imgUrl,
            String question,
            List<ImgQuizDistractResponse> list
    ) {}
    @JsonDeserialize
    public record ImgQuizDistractResponse (
            Long imgDistractNo,
            Long imgQuizNo,
            ValidationStatus validation,
            String text
    ) {}

    // 내부용 DTO
    public record ImgQuizFlatResponse(
            Long imgQuizNo,
            Long memberNo,
            String imgUrl,
            String question,
            Long imgDistractNo,
            ValidationStatus validation,
            String text
    ) {}


    //문제풀기용
    public record ImgSolveRequest(
            int userAnswer
    ) {}
    public record ImgSolveResponse(
            boolean isCorrect,
            String message
    ) {}

}

