package back.Quest.service.diary;

import back.Quest.model.dto.diary.DiaryDto;

import java.time.LocalDate;
import java.util.List;

public interface DiaryService {
    //추가
    void insertDiary(Long memberNo, DiaryDto.DiaryRequest request);
    //수정
    void updateDiary(Long memberNo, Long diaryNo, DiaryDto.DiaryUpdateRequest request);
    //삭제
    void deleteDiary(Long memberNo, Long diaryNo);

    //날짜별 조회
    DiaryDto.DiaryResponse dateDiary(Long memberNo, LocalDate date);

    //전체 조회
    List<DiaryDto.DiaryResponse> myDiary(Long memberNo);
}
