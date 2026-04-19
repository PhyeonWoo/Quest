package back.Quest.controller.imgQuiz;

import back.Quest.config.common.ApiResponse;
import back.Quest.config.exception.CustomException;
import back.Quest.model.dto.imgQuiz.ImgQuizDto;
import back.Quest.security.JwtProvider;
import back.Quest.service.imgQuiz.ImgQuizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "ImgQuiz", description = "이미지 퀴즈 API - 이미지 업로드 기반 퀴즈 생성, 삭제, 조회, 정답 제출")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/imgQuiz")
@Slf4j
public class ImgQuizController {
    private final ImgQuizService imgQuizService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    private Long getMemberNo(String bearerToken) {
        String token = jwtProvider.resolveToken(bearerToken);
        return jwtProvider.getMemberNo(token);
    }

    @Operation(
            summary = "이미지 퀴즈 생성",
            description = "이미지 파일과 퀴즈 정보를 함께 전송하여 이미지 퀴즈를 생성합니다.\n\n" +
                    "`multipart/form-data` 형식으로 전송하세요.\n\n" +
                    "- `image`: 이미지 파일\n" +
                    "- `request`: 퀴즈 정보 JSON"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 퀴즈 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> insertImgQuiz(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken,
            @Parameter(description = "퀴즈에 사용할 이미지 파일", required = true)
            @RequestPart("image") MultipartFile image,
            @Parameter(description = "퀴즈 정보 JSON 문자열", required = true)
            @RequestPart("request") String requestJson
    ) {
        try {
            ImgQuizDto.ImgQuizRequest request = objectMapper.readValue(requestJson, ImgQuizDto.ImgQuizRequest.class);
            imgQuizService.insertImgQuiz(getMemberNo(bearerToken), image, request);
            return ApiResponse.ok("생성 완료");
        } catch (Exception e) {
            throw new CustomException.InvalidRequestException("요청 형식이 올바르지 않습니다.");
        }
    }

    @Operation(
            summary = "이미지 퀴즈 삭제",
            description = "본인이 생성한 이미지 퀴즈를 삭제합니다. 다른 사람의 퀴즈는 삭제할 수 없습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 퀴즈 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "권한 없음 또는 삭제 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 퀴즈")
    })
    @DeleteMapping("/delete/{imgQuizNo}")
    public ApiResponse<String> deleteImgQuiz(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken,
            @Parameter(description = "삭제할 이미지 퀴즈 번호", required = true)
            @PathVariable Long imgQuizNo
    ) {
        imgQuizService.deleteImgQuiz(getMemberNo(bearerToken), imgQuizNo);
        return ApiResponse.ok("삭제 완료");
    }

    @Operation(
            summary = "내 이미지 퀴즈 목록 조회",
            description = "로그인한 사용자가 생성한 모든 이미지 퀴즈 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 퀴즈 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/me")
    public ApiResponse<List<ImgQuizDto.ImgQuizResponse>> myQuiz(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken
    ) {
        List<ImgQuizDto.ImgQuizResponse> result = imgQuizService.myQuiz(getMemberNo(bearerToken));
        return ApiResponse.ok(result);
    }

    @Operation(
            summary = "이미지 퀴즈 단건 조회",
            description = "이미지 퀴즈 번호로 특정 이미지 퀴즈를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 퀴즈 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 퀴즈")
    })
    @GetMapping("/{imgQuizNo}")
    public ApiResponse<ImgQuizDto.ImgQuizResponse> findById(
            @Parameter(description = "조회할 이미지 퀴즈 번호", required = true)
            @PathVariable Long imgQuizNo
    ) {
        ImgQuizDto.ImgQuizResponse response = imgQuizService.findById(imgQuizNo);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "이미지 퀴즈 정답 제출",
            description = "보기 번호(1번부터 시작)를 제출하면 즉시 정답 여부를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "채점 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 정답 번호"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 퀴즈")
    })
    @PostMapping("/{imgQuizNo}/solve")
    public ApiResponse<ImgQuizDto.ImgSolveResponse> solveImgQuiz(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken,
            @Parameter(description = "풀 이미지 퀴즈 번호", required = true)
            @PathVariable Long imgQuizNo,
            @RequestBody ImgQuizDto.ImgSolveRequest request
    ) {
        ImgQuizDto.ImgSolveResponse result = imgQuizService.solveImgQuiz(getMemberNo(bearerToken), imgQuizNo, request);
        return ApiResponse.ok(result);
    }
}
