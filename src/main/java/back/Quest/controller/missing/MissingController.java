package back.Quest.controller.missing;

import back.Quest.config.common.ApiResponse;
import back.Quest.model.dto.missing.MissingDto;
import back.Quest.service.missing.MissingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Missing", description = "실종자 관리 API - 인증 불필요. 실종자 등록, 수정, 삭제, 조회, 검색")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/missing")
public class MissingController {
    private final MissingService missingService;

    @Operation(
            summary = "실종자 등록",
            description = "실종자 정보를 등록합니다.\n\n" +
                    "- 나이: 1~110\n" +
                    "- 성별: `남자` 또는 `여자`\n" +
                    "- 상태: `OPEN`(수색중) / `CLOSED`(종결)\n" +
                    "- 실종 날짜: `yyyyMMdd` 형식 (예: 20240115)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "실종자 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패")
    })
    @PostMapping
    public ApiResponse<String> insertMissing(
            @Valid @RequestBody MissingDto.MissingRequest request
    ) {
        log.info("Insert Missing Request Start");
        missingService.insertMissing(request);
        return ApiResponse.ok("실종자 추가");
    }

    @Operation(
            summary = "실종자 정보 수정",
            description = "등록된 실종자 정보를 수정합니다. 이미 종결(CLOSED)된 사건은 수정할 수 없습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "실종자 정보 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "종결된 사건 또는 ID 불일치"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 실종자")
    })
    @PutMapping("/{missingNo}")
    public ApiResponse<String> updateMissing(
            @Valid @RequestBody MissingDto.MissingRequest request,
            @Parameter(description = "수정할 실종자 번호", required = true)
            @PathVariable Long missingNo
    ) {
        log.info("Update Missing Request Start");
        missingService.updateMissing(missingNo, request);
        return ApiResponse.ok("수정 완료");
    }

    @Operation(
            summary = "실종자 삭제",
            description = "실종자 정보를 삭제합니다. 이미 종결(CLOSED)된 사건은 삭제할 수 없습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "실종자 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "종결된 사건은 삭제 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 실종자")
    })
    @DeleteMapping("/delete/{missingNo}")
    public ApiResponse<String> deleteMissing(
            @Parameter(description = "삭제할 실종자 번호", required = true)
            @PathVariable Long missingNo
    ) {
        log.info("Delete Missing Request Start");
        missingService.deleteMissing(missingNo);
        return ApiResponse.ok("삭제 완료");
    }

    @Operation(
            summary = "실종자 전체 목록 조회",
            description = "등록된 모든 실종자 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "실종자 목록 조회 성공")
    })
    @GetMapping("/all")
    public ApiResponse<List<MissingDto.MissingResponse>> findAll() {
        log.info("Find MissingList Request Start");
        List<MissingDto.MissingResponse> response = missingService.findAll();
        return ApiResponse.ok(response);
    }

    @Operation(
            summary = "실종자 검색",
            description = "키워드로 실종자를 검색합니다. 이름 또는 지역으로 검색할 수 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "검색 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "검색어 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "검색 결과 없음")
    })
    @GetMapping("/search")
    public ApiResponse<List<MissingDto.MissingResponse>> findBySearch(
            @Parameter(description = "검색 키워드 (이름 또는 지역)", example = "홍길동", required = true)
            @ModelAttribute MissingDto.MissingSearchRequest request
    ) {
        log.info("Search find MissingList Request Start - searchWord : {}", request.keywords());
        List<MissingDto.MissingResponse> response = missingService.findByKeyword(request);
        log.info("Search find MissingList Response Ready");
        return ApiResponse.ok(response);
    }
}
