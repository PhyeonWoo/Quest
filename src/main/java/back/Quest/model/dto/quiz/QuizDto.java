package back.Quest.model.dto.quiz;

import back.Quest.model.Enum.ValidationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class QuizDto {

    @Schema(description = "퀴즈 생성 요청")
    public record QuizRequest(
            Long quizNo,
            @Schema(description = "퀴즈 질문", example = "대한민국의 수도는?")
            @NotBlank
            String question,
            @Schema(description = "보기 목록 (정답 1개 + 오답 N개)")
            List<QuizDistractRequest> list
    ) {}

    @Schema(description = "보기 항목")
    public record QuizDistractRequest(
            Long distractNo,
            @Schema(description = "정답 여부 (CORRECT: 정답, INCORRECT: 오답)", example = "CORRECT")
            @NotNull
            ValidationStatus validation,
            @Schema(description = "보기 텍스트", example = "서울")
            @NotBlank
            String text
    ) {}

    @Schema(description = "퀴즈 응답")
    public record QuizResponse(
            @Schema(description = "퀴즈 번호")
            Long quizNo,
            @Schema(description = "작성자 회원 번호")
            Long memberNo,
            @Schema(description = "퀴즈 질문")
            String question,
            @Schema(description = "보기 목록")
            List<DistractResponse> response
    ) {}

    @Schema(description = "보기 응답 항목")
    public record DistractResponse(
            @Schema(description = "보기 번호")
            Long distractNo,
            @Schema(description = "퀴즈 번호")
            Long quizNo,
            @Schema(description = "정답 여부")
            ValidationStatus validation,
            @Schema(description = "보기 텍스트")
            String text
    ) {}

    public record QuizFlatResponse(
            Long quizNo,
            Long memberNo,
            String question,
            Long distractNo,
            ValidationStatus validation,
            String text
    ) {}

    @Schema(description = "정답 제출 요청")
    public record SolveRequest(
            @Schema(description = "선택한 보기 번호 (1번부터 시작)", example = "1")
            int userAnswer
    ) {}

    @Schema(description = "채점 결과")
    public record SolveResponse(
            @Schema(description = "정답 여부")
            boolean isCorrect,
            @Schema(description = "결과 메시지", example = "정답입니다")
            String message
    ) {}
}
