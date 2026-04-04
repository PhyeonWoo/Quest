package back.Quest.mapper.diary;

import back.Quest.model.dto.diary.DiaryDto;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DiaryMapper {

    // 다이어리 추가
    int insertDiary(@Param("memberNo") Long memberNo,
                    @Param("request") DiaryDto.DiaryRequest request);


    // 다이어리 수정
    int updateDiary(@Param("memberNo") Long memberNo,
                    @Param("diaryNo") Long diaryNo,
                    @Param("request") DiaryDto.DiaryUpdateRequest request
    );


    // 다이어리 삭제
    int deleteDiary(@Param("memberNo") Long memberNo,
                    @Param("diaryNo") Long diaryNo
    );


    // 날짜별 다이어리 조회
    DiaryDto.DiaryResponse dateDiary(@Param("memberNo") Long memberNo,
                                     @Param("date") LocalDate date
    );

    // 다이어리 ID로 단건조회
    DiaryDto.DiaryResponse findByIdDiary(Long diaryNo);

    // memberNo로 내가 쓴 전체 다이어리 조회
    List<DiaryDto.DiaryResponse> myDiary(Long memberNo);
}
