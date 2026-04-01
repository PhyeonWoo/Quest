package back.Quest.model.dto.missing;

import jakarta.validation.constraints.*;
import org.apache.ibatis.type.Alias;

public class MissingDto {

    public record MissingRequest(
            Long missingNo,

            @NotBlank
            String name,

            @NotNull
            @Min(value = 1)
            @Max(value = 110)
            Integer age,

            @NotNull
            AreaCode area,

            @NotBlank
            @Size(min = 2, max = 2, message = "남자/여자")
            String gender,

            String img,

            @NotNull
            MissingStatus status,

            @NotBlank
            String date // 실종 날짜 yyyyMMdd 기준
    ) {}


    public record MissingSearchRequest(
            @NotBlank
            String keywords
    ) {}

    @Alias("MissingResponse")
    public record MissingResponse (
            Long missingNo,
            String name,
            Integer age,
            AreaCode area,
            String gender,
            String img,
            MissingStatus status,
            String date // 실종 날짜 yyyyMMdd 기준
    ) {}
}
