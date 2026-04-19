package back.Quest.controller.diary;

import back.Quest.config.common.ApiResponse;
import back.Quest.model.dto.diary.DiaryDto;
import back.Quest.security.JwtProvider;
import back.Quest.service.diary.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Diary", description = "다이어리 API - 개인 일기 CRUD 및 날짜별 조회")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diary")
public class DiaryController {
    private final DiaryService diaryService;
    private final JwtProvider jwtProvider;

    @Operation(
            summary = "다이어리 작성",
            description = "새로운 다이어리를 작성합니다.\n\n" +
                    "- 제목: 1~100자\n" +
                    "- 내용: 1~500자"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다이어리 작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ApiResponse<String> insertDiary(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken,
            @Valid @RequestBody DiaryDto.DiaryRequest request
    ) {
        log.info("Insert Diary Request");
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);
        diaryService.insertDiary(memberNo, request);
        log.info("Insert Diary Response Ready");
        return ApiResponse.ok("생성 완료");
    }

    @Operation(
            summary = "다이어리 수정",
            description = "본인이 작성한 다이어리를 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다이어리 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 다이어리")
    })
    @PutMapping("/{diaryNo}")
    public ApiResponse<String> updateDiary(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken,
            @Parameter(description = "수정할 다이어리 번호", required = true)
            @PathVariable Long diaryNo,
            @Valid @RequestBody DiaryDto.DiaryUpdateRequest request
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);
        diaryService.updateDiary(memberNo, diaryNo, request);
        return ApiResponse.ok("수정 완료");
    }

    @Operation(
            summary = "다이어리 삭제",
            description = "본인이 작성한 다이어리를 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다이어리 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 다이어리")
    })
    @DeleteMapping("/delete/{diaryNo}")
    public ApiResponse<String> deleteDiary(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken,
            @Parameter(description = "삭제할 다이어리 번호", required = true)
            @PathVariable Long diaryNo
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);
        diaryService.deleteDiary(memberNo, diaryNo);
        return ApiResponse.ok("삭제 완료");
    }

    @Operation(
            summary = "날짜별 다이어리 조회",
            description = "특정 날짜에 작성된 다이어리를 조회합니다.\n\n" +
                    "날짜 형식: `yyyy-MM-dd` (예: 2024-01-15)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다이어리 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 날짜에 다이어리 없음")
    })
    @GetMapping("/{date}")
    public ApiResponse<DiaryDto.DiaryResponse> dateDiary(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", example = "2024-01-15", required = true)
            @PathVariable String date
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);
        LocalDate localDate = LocalDate.parse(date);
        DiaryDto.DiaryResponse response = diaryService.dateDiary(memberNo, localDate);
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "내 다이어리 전체 조회",
            description = "로그인한 사용자가 작성한 모든 다이어리 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다이어리 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/all")
    public ApiResponse<List<DiaryDto.DiaryResponse>> myDiary(
            @Parameter(description = "Bearer {accessToken}", required = true)
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);
        List<DiaryDto.DiaryResponse> response = diaryService.myDiary(memberNo);
        return ApiResponse.ok(response);
    }
}
