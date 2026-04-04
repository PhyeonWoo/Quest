package back.Quest.controller.diary;

import back.Quest.config.common.ApiResponse;
import back.Quest.model.dto.diary.DiaryDto;
import back.Quest.security.JwtProvider;
import back.Quest.service.diary.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diary")
public class DiaryController {
    private final DiaryService diaryService;
    private final JwtProvider jwtProvider;

    @Operation(
            summary = "다이어리 추가",
            description = "요청을 통해 다이어리를 추가합니다."
    )
    @PostMapping
    public ApiResponse<String> insertDiary(
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
            description = "요청을 통해 다이어리를 수정합니다."
    )
    @PutMapping("/{diaryNo}")
    public ApiResponse<String> updateDiary(
            @RequestHeader("Authorization") String bearerToken,
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
            description = "요청을 통해 다이어리를 삭제"
    )
    @DeleteMapping("/delete/{diaryNo}")
    public ApiResponse<String> deleteDiary(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long diaryNo
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        diaryService.deleteDiary(memberNo, diaryNo);
        return ApiResponse.ok("삭제 완료");
    }


    @Operation(
            summary = "다이어리 날짜별 불러오기",
            description = "날짜별 단일 다이어리 불러오기"
    )
    @GetMapping("/{date}")
    public ApiResponse<DiaryDto.DiaryResponse> dateDiary(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable String date
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        LocalDate localDate = LocalDate.parse(date);

        DiaryDto.DiaryResponse response = diaryService.dateDiary(memberNo, localDate);
        return ApiResponse.ok(response);
    }



    @Operation(
            summary = "다이어리 전체 조회",
            description = "내가 쓴 다이어리 전체 조회"
    )
    @GetMapping("/all")
    public ApiResponse<List<DiaryDto.DiaryResponse>> myDiary(
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = jwtProvider.resolveToken(bearerToken);
        Long memberNo = jwtProvider.getMemberNo(token);

        List<DiaryDto.DiaryResponse> response = diaryService.myDiary(memberNo);
        return ApiResponse.ok(response);
    }

}
