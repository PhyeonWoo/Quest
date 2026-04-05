package back.Quest.service.diary.impl;

import back.Quest.config.exception.CustomException;
import back.Quest.mapper.diary.DiaryMapper;
import back.Quest.model.dto.diary.DiaryDto;
import back.Quest.service.diary.DiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DiaryServiceImpl implements DiaryService {
    private final DiaryMapper diaryMapper;


    @Override
    @Transactional
    @CacheEvict(value = "diaryList", key = "#memberNo")
    public void insertDiary(Long memberNo, DiaryDto.DiaryRequest request) {
        log.info("Insert Diary Request");
        int diaryCount = diaryMapper.insertDiary(memberNo, request);

        if (diaryCount == 0) {
            log.error("Insert Diary Request Error");
            throw new CustomException.InvalidRequestException("Insert Request Error");
        }

        log.info("Insert Diary Success");
    }

    @Override
    @Transactional
    @CacheEvict(value = "diaryList", key = "#memberNo")
    public void updateDiary(Long memberNo, Long diaryNo, DiaryDto.DiaryUpdateRequest request) {
        log.info("Update Diary Request");
        int updateDiary = diaryMapper.updateDiary(memberNo, diaryNo, request);

        if(updateDiary == 0) {
            log.error("Update Diary Request Error");
            throw new CustomException.InvalidRequestException("Update Request Error");
        }

        log.info("Update Diary Success");
    }

    @Override
    @Transactional
    @CacheEvict(value = "diaryList",key = "#memberNo")
    public void deleteDiary(Long memberNo, Long diaryNo) {
        log.info("Delete Diary Request");

        validDelete(memberNo,diaryNo);

        int deleteDiary = diaryMapper.deleteDiary(memberNo, diaryNo);
        if(deleteDiary == 0) {
            log.error("Not delete Request");
            throw new CustomException.InvalidRequestException("Delete Request Error");
        }
        log.info("Delete Diary Success");
    }

    @Override
    public DiaryDto.DiaryResponse dateDiary(Long memberNo, LocalDate date) {
        log.info("DateDiary Request");
        DiaryDto.DiaryResponse response = diaryMapper.dateDiary(memberNo, date);

        if (response == null) {
            log.warn("Not Response");
            throw new CustomException.NotFoundException("해당 날짜에 다이어리가 존재하지 않습니다.");
        }

        log.info("DateDiary Response Success");
        return response;
    }

    @Override
    @Cacheable(value = "diaryList", key = "#memberNo")
    public List<DiaryDto.DiaryResponse> myDiary(Long memberNo) {
        log.info("MyDiary find Request");
        List<DiaryDto.DiaryResponse> response = diaryMapper.myDiary(memberNo);

        if (response.isEmpty()) {
            log.warn("Not Found");
            return Collections.emptyList();
        }

        log.info("MyDiary Response Success : {}",response.size());
        return response;
    }





    private void validDelete(Long memberToken, Long diaryNo) {
        if (diaryNo == null) {
            throw new CustomException.InvalidRequestException("빈칸입니다.");
        }
        validDiaryOwner(memberToken, diaryNo);
    }


    private void validDiaryOwner(Long memberToken, Long diary_no) {
        DiaryDto.DiaryResponse response = diaryMapper.findByIdDiary(diary_no);

        if (response == null) {
            throw new CustomException.NotFoundException("존재하지 않습니다.");
        }
        if (!response.memberNo().equals(memberToken)) {
            throw new CustomException.InvalidRequestException("일치하지 않습니다.");
        }
    }
}
