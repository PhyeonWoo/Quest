package back.Quest.model.dto.missing;

import back.Quest.model.Enum.EnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MissingStatus implements EnumClass {
    MISSING("M", "실종중"),
    FOUND("F", "찾음(발견)"),
    CLOSED("C", "종결");

    private final String code; // DB에 저장할 실제 값
    private final String description;
}
