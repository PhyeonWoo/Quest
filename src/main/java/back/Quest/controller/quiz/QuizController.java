package back.Quest.controller.quiz;

import back.Quest.config.common.ApiResponse;
import back.Quest.model.dto.quiz.QuizDto;
import back.Quest.security.JwtProvider;
import back.Quest.service.quiz.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Quiz", description = "텍스트 퀴즈 API - 생성, 삭제, 조회, 정답 제출")
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
            summary = "퀴즈 생성",
            description = "객관식 퀴즈를 생성합니다.\n\n" +
                    "보기(list) 중 정답은 `validation: CORRECT`, 오답은 `validation: INCORRECT`로 설정하세요."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "퀴즈 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ApiResponse<String> insertQuiz(
            @Parameter(description = "Bearer {accessToken}", required = true)
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
            description = "본인이 생성한 퀴즈를 삭제합니다. 다른 사람의 퀴즈는 삭제할 수 없습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "퀴즈 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "권한 없음 또는 삭제 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 퀴즈")
    })
    @DeleteMapping("/delete/{quizNo}")
    public ApiResponse<String> deleteQuiz(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken,
            @Parameter(description = "삭제할 퀴즈 번호", required = true)
            @PathVariable Long quizNo
    ) {
        quizService.deleteQuiz(getMemberNo(bearerToken), quizNo);
        return ApiResponse.ok("삭제 완료");
    }

    @Operation(
            summary = "내 퀴즈 목록 조회",
            description = "로그인한 사용자가 생성한 모든 퀴즈 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "퀴즈 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/me")
    public ApiResponse<List<QuizDto.QuizResponse>> myQuiz(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken
    ) {
        List<QuizDto.QuizResponse> response = quizService.myQuiz(getMemberNo(bearerToken));
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "퀴즈 단건 조회",
            description = "퀴즈 번호로 특정 퀴즈를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "퀴즈 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 퀴즈")
    })
    @GetMapping("/{quizNo}")
    public ApiResponse<QuizDto.QuizResponse> findById(
            @Parameter(description = "조회할 퀴즈 번호", required = true)
            @PathVariable Long quizNo
    ) {
        QuizDto.QuizResponse response = quizService.findById(quizNo);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "퀴즈 정답 제출",
            description = "보기 번호(1번부터 시작)를 제출하면 즉시 정답 여부를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "채점 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 정답 번호"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 퀴즈")
    })
    @PostMapping("/{quizNo}/solve")
    public ApiResponse<QuizDto.SolveResponse> solveQuiz(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken,
            @Parameter(description = "풀 퀴즈 번호", required = true)
            @PathVariable Long quizNo,
            @RequestBody QuizDto.SolveRequest request
    ) {
        QuizDto.SolveResponse response = quizService.solveQuiz(getMemberNo(bearerToken), quizNo, request);
        return ApiResponse.ok(response);
    }
}
