package back.Quest.model.dto.quiz;

import back.Quest.model.Enum.ValidationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class QuizDto {

    public record QuizRequest(
            Long quizNo,
            @NotBlank
            String question,
            List<QuizDistractRequest> list
    ) {}
    public record QuizDistractRequest(
            Long distractNo,
            @NotNull
            ValidationStatus validation,
            @NotBlank
            String text
    ) {}


    //Controller Response
    public record QuizResponse(
            Long quizNo,
            Long memberNo,
            String question,
            List<DistractResponse> response
    ) {}
    public record DistractResponse(
            Long distractNo,
            Long quizNo,
            ValidationStatus validation,
            String text
    ) {}


    //내부용
    public record QuizFlatResponse(
            Long quizNo,
            Long memberNo,
            String question,
            Long distractNo,
            ValidationStatus validation,
            String text
    ) {}



    //문제풀기용
    public record SolveRequest(
            int userAnswer
    ) {}
    public record SolveResponse(
            boolean isCorrect,
            String message
    ) {}

}
