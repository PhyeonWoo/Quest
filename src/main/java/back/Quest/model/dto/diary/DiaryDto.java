package back.Quest.model.dto.diary;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class DiaryDto {

    // INSERT 시 사용
    public record DiaryRequest(
            Long memberNo,
            @Size(min = 1, max = 100, message = "1자 이상 100자 이하여야 합니다.")
            String title,
            @Size(min = 1, max = 500, message = "500자 이하로만 작성 가능합니다.")
            String memo
    ) {}

    // UPDATE 시 사용
    public record DiaryUpdateRequest(
            Long memberNo,
            Long diaryNo,
            String title,
            String memo
    ) {}

    // 응답
    public record DiaryResponse(
            Long diaryNo,
            Long memberNo,
            String title,
            String memo,
            LocalDate createdDt,
            LocalDate updatedDt
    ) {}
}