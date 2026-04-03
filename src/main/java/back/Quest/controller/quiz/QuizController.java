package back.Quest.controller.quiz;

import back.Quest.config.common.ApiResponse;
import back.Quest.model.dto.quiz.QuizDto;
import back.Quest.security.JwtProvider;
import back.Quest.service.quiz.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/quiz")
public class QuizController {
    private final QuizService quizService;
    private final JwtProvider jwtProvider;


    private Long getMemberNo(String bearerToken) {
        String token = jwtProvider.resolveToken(bearerToken);
        return jwtProvider.getMemberNo(token);
    }


    @Operation(
            summary = "퀴즈 추가",
            description = "요청을 통해 퀴즈를 추가합니다."
    )
    @PostMapping
    public ApiResponse<String> insertQuiz(
            @RequestHeader("Authorization") String bearerToken,
            @Valid @RequestBody QuizDto.QuizRequest request
    ) {
        log.info("Insert Quiz Request");

        quizService.insertQuiz(getMemberNo(bearerToken), request);

        log.info("Insert Quiz Response Ready");
        return ApiResponse.ok("생성 완료");
    }



    @Operation(
            summary = "퀴즈 삭제",
            description = "요청을 통해 퀴즈를 삭제합니다."
    )
    @DeleteMapping("/delete/{quizNo}")
    public ApiResponse<String> deleteQuiz(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long quizNo
    ) {
        quizService.deleteQuiz(getMemberNo(bearerToken), quizNo);
        return ApiResponse.ok("삭제 완료");
    }



    @Operation(
            summary = "퀴즈 불러오기",
            description = "내가 쓴 퀴즈를 불러옵니다"
    )
    @GetMapping("/me")
    public ApiResponse<List<QuizDto.QuizResponse>> myQuiz(
            @RequestHeader("Authorization") String bearerToken
    ) {

        List<QuizDto.QuizResponse> response = quizService.myQuiz(getMemberNo(bearerToken));
        return ApiResponse.ok(response);
    }




    @Operation(
            summary = "단건 ID로 퀴즈 조회",
            description = "퀴즈 고유 번호를 통해 단건 조회를 합니다."
    )
    @GetMapping("/{quizNo}")
    public ApiResponse<QuizDto.QuizResponse> findById(
            @PathVariable Long quizNo
    ) {
        QuizDto.QuizResponse response = quizService.findById(quizNo);
        return ApiResponse.ok(response);
    }



    @Operation(
            summary = "퀴즈 풀기 제출",
            description = "사용자가 선택한 정답 번호를 통해 정답 여부를 판별"
    )
    @PostMapping("/{quizNo}/solve")
    public ApiResponse<QuizDto.SolveResponse> solveQuiz(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long quizNo,
            @RequestBody QuizDto.SolveRequest request
    ) {

        QuizDto.SolveResponse response = quizService.solveQuiz(getMemberNo(bearerToken), quizNo, request);
        return ApiResponse.ok(response);
    }


}
