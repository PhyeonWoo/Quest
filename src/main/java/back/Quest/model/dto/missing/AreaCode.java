package back.Quest.model.dto.missing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AreaCode {
    SEOUL("11","서울"),
    GYEONGGI("31","경기"),
    BUSAN("26","부산"),
    INCHEON("28", "인천"),
    DAEGU("27", "대구"),
    GWANGJU("29", "광주"),
    DAEJEON("30", "대전"),
    ULSAN("31", "울산"),
    SEJONG("36", "세종"),
    GANGWON("32", "강원"),
    CHUNGBUK("33", "충북"),
    CHUNGNAM("34", "충남"),
    JEONBUK("35", "전북"),
    JEONNAM("36", "전남"),
    GYEONGBUK("37", "경북"),
    GYEONGNAM("38", "경남"),
    JEJU("39", "제주");

    private final String code;
    private final String description;
}
