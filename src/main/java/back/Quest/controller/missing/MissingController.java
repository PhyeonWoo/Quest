package back.Quest.controller.missing;

import back.Quest.config.common.ApiResponse;
import back.Quest.model.dto.missing.MissingDto;
import back.Quest.service.missing.MissingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/missing")
public class MissingController {
    private final MissingService missingService;


    @Operation(
            summary = "실종자 목록 추가",
            description = "요청을 통해 실종자를 추가합니다"
    )
    @PostMapping
    public ApiResponse<String> insertMissing(
            @Valid @RequestBody MissingDto.MissingRequest request
    ) {
        log.info("Insert Missing Request Start");
        missingService.insertMissing(request);
        return ApiResponse.ok("실종자 추가");
    }


    @Operation(
            summary = "실종자 목록 수정",
            description = "요청을 통해 실종자를 수정합니다."
    )
    @PutMapping("/{missingNo}")
    public ApiResponse<String> updateMissing(
            @Valid @RequestBody MissingDto.MissingRequest request,
            @PathVariable Long missingNo
    ) {
        log.info("Update Missing Request Start");
        missingService.updateMissing(missingNo, request);
        return ApiResponse.ok("수정 완료");
    }


    @Operation(
            summary = "실종자 목록 삭제",
            description = "요청을 통해 실종자 목록을 삭제합니다"
    )
    @PutMapping("/delete/{missingNo}")
    public ApiResponse<String> deleteMissing(
            @PathVariable Long missingNo
    ) {
        log.info("Delete Missing Request Start");
        missingService.deleteMissing(missingNo);
        return ApiResponse.ok("삭제 완료");
    }


    @Operation(
            summary = "실종자 목록 불러오기",
            description = "요청을 통해 실종자 목록 불러오기"
    )
    @GetMapping("/all")
    public ApiResponse<List<MissingDto.MissingResponse>> findAll() {
        log.info("Find MissingList Request Start");
        List<MissingDto.MissingResponse> response = missingService.findAll();
        return ApiResponse.ok(response);
    }


    @Operation(
            summary = "실종자 검색",
            description = "검색으로 특정 실종자 지역 및 이름 검색"
    )
    @GetMapping("/search")
    public ApiResponse<List<MissingDto.MissingResponse>> findBySearch(
            @ModelAttribute MissingDto.MissingSearchRequest request
    ) {
        log.info("Search find MissingList Request Start - searchWord : {}",request.keywords());
        List<MissingDto.MissingResponse> response = missingService.findByKeyword(request);

        log.info("Search find MissingList Response Ready");
        return ApiResponse.ok(response);
    }

}
