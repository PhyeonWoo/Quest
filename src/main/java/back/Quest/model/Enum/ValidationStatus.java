package back.Quest.model.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ValidationStatus implements EnumClass {
    CORRECT("Y", "정답"),
    INCORRECT("N","오답");

    private final String code;
    private final String description;
}
