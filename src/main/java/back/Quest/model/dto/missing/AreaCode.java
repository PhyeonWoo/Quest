package back.Quest.model.dto.missing;

import back.Quest.model.Enum.EnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AreaCode implements EnumClass {
    SEOUL("11","서울"),
    BUSAN("26","부산"),
    DAEGU("27", "대구"),
    INCHEON("28", "인천"),
    GWANGJU("29", "광주"),
    DAEJEON("30", "대전"),
    ULSAN("31", "울산"),
    GANGWON("32", "강원"),
    CHUNGBUK("33", "충북"),
    CHUNGNAM("34", "충남"),
    JEONBUK("35", "전북"),
    SEJONG("36", "세종"),
    GYEONGBUK("37", "경북"),
    GYEONGNAM("38", "경남"),
    JEJU("39", "제주"),
    GYEONGGI("41", "경기"),
    JEONNAM("46", "전남");

    private final String code;
    private final String description;
}
