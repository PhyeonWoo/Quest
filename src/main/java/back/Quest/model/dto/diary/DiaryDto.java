package back.Quest.model.dto.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.apache.ibatis.type.Alias;

import java.time.LocalDate;

public class DiaryDto {

    @Schema(description = "다이어리 작성 요청")
    public record DiaryRequest(
            Long memberNo,
            @Schema(description = "다이어리 제목 (1~100자)", example = "오늘의 일기")
            @Size(min = 1, max = 100, message = "1자 이상 100자 이하여야 합니다.")
            String title,
            @Schema(description = "다이어리 내용 (1~500자)", example = "오늘은 공부를 열심히 했다.")
            @Size(min = 1, max = 500, message = "500자 이하로만 작성 가능합니다.")
            String memo
    ) {}

    @Schema(description = "다이어리 수정 요청")
    public record DiaryUpdateRequest(
            Long memberNo,
            Long diaryNo,
            @Schema(description = "수정할 제목 (1~100자)", example = "수정된 제목")
            @Size(min = 1, max = 100, message = "1자 이상 100자 이하여야 합니다.")
            String title,
            @Schema(description = "수정할 내용 (1~500자)", example = "수정된 내용입니다.")
            @Size(min = 1, max = 500, message = "500자 이하로만 작성 가능합니다.")
            String memo
    ) {}

    @Schema(description = "다이어리 응답")
    @Alias("DiaryResponse")
    public record DiaryResponse(
            @Schema(description = "다이어리 번호")
            Long diaryNo,
            @Schema(description = "작성자 회원 번호")
            Long memberNo,
            @Schema(description = "제목")
            String title,
            @Schema(description = "내용")
            String memo,
            @Schema(description = "작성일", example = "2024-01-15")
            LocalDate createdDt,
            @Schema(description = "수정일", example = "2024-01-15")
            LocalDate updatedDt
    ) {}
}
