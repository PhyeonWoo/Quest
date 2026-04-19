package back.Quest.model.dto.missing;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.apache.ibatis.type.Alias;

public class MissingDto {

    @Schema(description = "실종자 등록/수정 요청")
    public record MissingRequest(
            Long missingNo,

            @Schema(description = "실종자 이름", example = "홍길동")
            @NotBlank
            String name,

            @Schema(description = "실종자 나이 (1~110)", example = "25")
            @NotNull
            @Min(value = 1)
            @Max(value = 110)
            Integer age,

            @Schema(description = "실종 지역 코드 (예: SEOUL, BUSAN)", example = "SEOUL")
            @NotNull
            AreaCode area,

            @Schema(description = "성별 (남자 또는 여자)", example = "남자")
            @NotBlank
            @Size(min = 2, max = 2, message = "남자/여자")
            String gender,

            @Schema(description = "실종자 이미지 URL (선택)", example = "https://example.com/image.jpg")
            String img,

            @Schema(description = "실종 상태 (MISSING: 수색중, FOUND: 찾음(발견), CLOSED: 종결)", example = "MISSING")
            @NotNull
            MissingStatus status,

            @Schema(description = "실종 날짜 (yyyyMMdd 형식)", example = "20240115")
            @NotBlank
            String date
    ) {}

    @Schema(description = "실종자 검색 요청")
    public record MissingSearchRequest(
            @Schema(description = "검색 키워드 (이름 또는 지역)", example = "홍길동")
            @NotBlank
            String keywords
    ) {}

    @Schema(description = "실종자 응답")
    @Alias("MissingResponse")
    public record MissingResponse(
            @Schema(description = "실종자 번호")
            Long missingNo,
            @Schema(description = "이름")
            String name,
            @Schema(description = "나이")
            Integer age,
            @Schema(description = "지역 코드")
            AreaCode area,
            @Schema(description = "성별")
            String gender,
            @Schema(description = "이미지 URL")
            String img,
            @Schema(description = "실종 상태")
            MissingStatus status,
            @Schema(description = "실종 날짜 (yyyyMMdd)")
            String date
    ) {}
}
