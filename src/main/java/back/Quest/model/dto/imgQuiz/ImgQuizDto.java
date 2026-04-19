package back.Quest.model.dto.imgQuiz;

import back.Quest.model.Enum.ValidationStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ImgQuizDto {

    @Schema(description = "이미지 퀴즈 생성 요청")
    public record ImgQuizRequest(
            Long imgQuizNo,
            @Schema(description = "퀴즈 질문", example = "이 건물은 어디인가?")
            String question,
            @Schema(description = "보기 목록 (정답 1개 + 오답 N개)")
            List<ImgQuizDistractRequest> list
    ) {}

    @Schema(description = "이미지 퀴즈 보기 항목")
    public record ImgQuizDistractRequest(
            Long imgDistractNo,
            @Schema(description = "정답 여부 (CORRECT: 정답, INCORRECT: 오답)", example = "CORRECT")
            ValidationStatus validation,
            @Schema(description = "보기 텍스트", example = "경복궁")
            String text
    ) {}

    @Schema(description = "이미지 퀴즈 응답")
    @JsonDeserialize
    public record ImgQuizResponse(
            @Schema(description = "이미지 퀴즈 번호")
            Long imgQuizNo,
            @Schema(description = "작성자 회원 번호")
            Long memberNo,
            @Schema(description = "Firebase에 저장된 이미지 URL")
            String imgUrl,
            @Schema(description = "퀴즈 질문")
            String question,
            @Schema(description = "보기 목록")
            List<ImgQuizDistractResponse> list
    ) {}

    @Schema(description = "이미지 퀴즈 보기 응답")
    @JsonDeserialize
    public record ImgQuizDistractResponse(
            @Schema(description = "보기 번호")
            Long imgDistractNo,
            @Schema(description = "이미지 퀴즈 번호")
            Long imgQuizNo,
            @Schema(description = "정답 여부")
            ValidationStatus validation,
            @Schema(description = "보기 텍스트")
            String text
    ) {}

    public record ImgQuizFlatResponse(
            Long imgQuizNo,
            Long memberNo,
            String imgUrl,
            String question,
            Long imgDistractNo,
            ValidationStatus validation,
            String text
    ) {}

    @Schema(description = "이미지 퀴즈 정답 제출 요청")
    public record ImgSolveRequest(
            @Schema(description = "선택한 보기 번호 (1번부터 시작)", example = "1")
            int userAnswer
    ) {}

    @Schema(description = "이미지 퀴즈 채점 결과")
    public record ImgSolveResponse(
            @Schema(description = "정답 여부")
            boolean isCorrect,
            @Schema(description = "결과 메시지", example = "정답입니다.")
            String message
    ) {}
}
