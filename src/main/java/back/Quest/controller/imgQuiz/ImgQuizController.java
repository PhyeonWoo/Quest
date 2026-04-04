package back.Quest.controller.imgQuiz;

import back.Quest.config.common.ApiResponse;
import back.Quest.model.dto.imgQuiz.ImgQuizDto;
import back.Quest.security.JwtProvider;
import back.Quest.service.imgQuiz.ImgQuizService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/imgQuiz")
@Slf4j
public class ImgQuizController {
    private final ImgQuizService imgQuizService;
    private final JwtProvider jwtProvider;

    private Long getMemberNo(String bearerToken) {
        String token = jwtProvider.resolveToken(bearerToken);
        return jwtProvider.getMemberNo(token);
    }

    @Operation(
            summary = "이미지 퀴즈 추가",
            description = "요청을 통해 이미지 퀴즈를 추가합니다."
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> insertImgQuiz(
            @RequestHeader("Authorization") String bearerToken,
            @RequestPart("image") MultipartFile image,
            @Valid @RequestPart ImgQuizDto.ImgQuizRequest request
    ) {
        imgQuizService.insertImgQuiz(getMemberNo(bearerToken), image, request);
        return ApiResponse.ok("생성 완료");
    }


    @Operation(
            summary = "이미지 퀴즈 삭제",
            description = "요청을 통해 이미지 퀴즈를 삭제합니다."
    )
    @DeleteMapping("/delete/{imgQuizNo}")
    public ApiResponse<String> deleteImgQuiz(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long imgQuizNo
    ) {
        imgQuizService.deleteImgQuiz(getMemberNo(bearerToken), imgQuizNo);
        return ApiResponse.ok("삭제 완료");
    }



    @Operation(
            summary = "이미지 퀴즈 불러오기",
            description = "내가 쓴 이미지퀴즈를 불러옵니다."
    )
    @GetMapping("/me")
    public ApiResponse<List<ImgQuizDto.ImgQuizResponse>> myQuiz(
            @RequestHeader("Authorization") String bearerToken
    ) {
        List<ImgQuizDto.ImgQuizResponse> result = imgQuizService.myQuiz(getMemberNo(bearerToken));
        return ApiResponse.ok(result);
    }



    @Operation(
            summary = "ID로 단건 이미지 퀴즈 조회",
            description = "이미지 퀴즈 고유 번호를 통해 단건 조회를 합니다."
    )
    @GetMapping("/{imgQuizNo}")
    public ApiResponse<ImgQuizDto.ImgQuizResponse> findById(
            @PathVariable Long imgQuizNo
    ) {
        ImgQuizDto.ImgQuizResponse response = imgQuizService.findById(imgQuizNo);
        return ApiResponse.ok(response);
    }




    @Operation(
            summary = "이미지 퀴즈 풀기 제출",
            description = "사용자가 선택한 정답 번호를 통해 정답 여부를 판별"
    )
    @PostMapping("/{imgQuizNo}/solve")
    public ApiResponse<ImgQuizDto.ImgSolveResponse> solveImgQuiz(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long imgQuizNo,
            @RequestBody ImgQuizDto.ImgSolveRequest request
    ) {
        ImgQuizDto.ImgSolveResponse result = imgQuizService.solveImgQuiz(getMemberNo(bearerToken), imgQuizNo, request);
        return ApiResponse.ok(result);
    }


}
