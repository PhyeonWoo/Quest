package back.Quest.service.diary.impl;

import back.Quest.mapper.diary.DiaryMapper;
import back.Quest.model.dto.diary.DiaryDto;
import back.Quest.service.diary.DiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;



@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryServiceImpl implements DiaryService {
    private final DiaryMapper diaryMapper;


    @Override
    public void insertDiary(Long memberNo, DiaryDto.DiaryRequest request) {
        log.info("Insert Diary Request");
        int diaryCount = diaryMapper.insertDiary(memberNo, request);

        if (diaryCount == 0) {
            throw new IllegalArgumentException("오류");
        }
        log.info("Insert Diary Success");
    }

    @Override
    public void updateDiary(Long memberNo, Long diaryNo, DiaryDto.DiaryUpdateRequest request) {
        int updateDiary = diaryMapper.updateDiary(memberNo, diaryNo, request);

        if(updateDiary == 0) {
            throw new IllegalArgumentException("오류");
        }
        log.info("Update Diary Success");
    }

    @Override
    public void deleteDiary(Long memberNo, Long diaryNo) {
        log.info("Delete Diary Request");

        validDelete(memberNo,diaryNo);

        int deleteDiary = diaryMapper.deleteDiary(memberNo, diaryNo);
        if(deleteDiary == 0) {
            throw new IllegalArgumentException("오류");
        }
        log.info("Delete Diary Success");
    }

    @Override
    public DiaryDto.DiaryResponse dateDiary(Long memberNo, LocalDate date) {
        DiaryDto.DiaryResponse response = diaryMapper.dateDiary(memberNo, date);

        if (response == null) {
            throw new IllegalArgumentException("공백");
        }

        return response;
    }

    @Override
    public List<DiaryDto.DiaryResponse> myDiary(Long memberNo) {
        List<DiaryDto.DiaryResponse> response = diaryMapper.myDiary(memberNo);

        if (response.isEmpty()) {
            log.warn("Not Fount");
            return Collections.emptyList();
        }

        log.info("MyDiary Response Success : {}",response.size());
        return response;
    }






//    private void validInsert(Long memberToken, DiaryDto.DiaryRequest request) {
//        validPermission(memberToken, request.memberNo());
//    }

    private void validPermission(Long memberToken, Long requestMemberNo) {
        if(!memberToken.equals(requestMemberNo)) {
            throw new IllegalArgumentException("오류");
        }
    }

    private void validUpdate(Long memberToken, Long diaryNo, DiaryDto.DiaryUpdateRequest request) {
        validPermission(memberToken, request.memberNo());
        validDiary(diaryNo, request.diaryNo());
    }


    private void validDiary(Long diaryNo, Long requestDiaryNo) {
        if (diaryNo == null || requestDiaryNo == null) {
            throw new IllegalArgumentException("공백이면 안됩니다.");
        }
        if(!diaryNo.equals(requestDiaryNo)) {
            throw new IllegalArgumentException("일치하지 않습니다.");
        }
    }


    private void validDelete(Long memberToken, Long diaryNo) {
        if (diaryNo == null) {
            throw new IllegalArgumentException("빈칸이면 안됩니다.");
        }
        validDiaryOwner(memberToken, diaryNo);
    }


    private void validDiaryOwner(Long memberToken, Long diary_no) {
        DiaryDto.DiaryResponse response = diaryMapper.findByIdDiary(diary_no);

        if (response == null) {
            throw new IllegalArgumentException("다이어리를 찾을 수 없습니다.");
        }
        if (!response.memberNo().equals(memberToken)) {
            throw new IllegalArgumentException("일치하지 않습니다.");
        }
    }
}
