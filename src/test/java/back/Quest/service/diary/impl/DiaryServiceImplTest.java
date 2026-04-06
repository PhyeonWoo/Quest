package back.Quest.service.diary.impl;

import back.Quest.config.exception.CustomException;
import back.Quest.mapper.diary.DiaryMapper;
import back.Quest.model.dto.diary.DiaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("다이어리 서비스 테스트")
public class DiaryServiceImplTest {

    @Mock
    private DiaryMapper diaryMapper;

    @InjectMocks
    private DiaryServiceImpl diaryService;

    private Long memberNo = 1L;
    private Long diaryNo = 1L;
    private LocalDate testDate = LocalDate.of(2024, 12, 25);

    // 테스트 데이터
    private DiaryDto.DiaryResponse mockDiaryResponse;

    @BeforeEach
    void setUp() {
        mockDiaryResponse = new DiaryDto.DiaryResponse(
                1L,
                1L,
                "Test Title",
                "Test Content",
                LocalDate.now(),
                LocalDate.now()
        );
    }

    @Test
    @DisplayName("일기 생성 성공")
    void testDiaryInsertSuccess() {
        //given
        Long memberNo = 1L;
        DiaryDto.DiaryRequest request = new DiaryDto.DiaryRequest(
                memberNo,
                "titleTest",
                "memoTest"
        );
        // mapper가 1행 삽입했다고 가정
        when(diaryMapper.insertDiary(eq(memberNo), any(DiaryDto.DiaryRequest.class))).thenReturn(1);

        assertDoesNotThrow(() -> diaryService.insertDiary(memberNo, request));

        //검증
        verify(diaryMapper, times(1)).insertDiary(memberNo, request);
    }

    @Test
    @DisplayName("일기 수정 성공")
    void testDiaryUpdateSuccess() {
        Long memberNo = 1L;
        Long diaryNo = 1L;
        DiaryDto.DiaryUpdateRequest request = new DiaryDto.DiaryUpdateRequest(
                memberNo,
                diaryNo,
                "UpdateTest",
                "memoUpdate"
        );

        when(diaryMapper.updateDiary(eq(memberNo), eq(diaryNo), any(DiaryDto.DiaryUpdateRequest.class))).thenReturn(1);

        assertDoesNotThrow(() -> diaryService.updateDiary(memberNo, diaryNo, request));

        verify(diaryMapper, times(1)).updateDiary(memberNo, diaryNo, request);
    }


    @Test
    @DisplayName("일기 삭제")
    void testDiaryDeleteSuccess() {
        when(diaryMapper.findByIdDiary(diaryNo)).thenReturn(mockDiaryResponse);
        when(diaryMapper.deleteDiary(memberNo, diaryNo)).thenReturn(1);

        // 동작 검증
        assertDoesNotThrow(() -> diaryService.deleteDiary(memberNo, diaryNo));


        verify(diaryMapper, times(1)).findByIdDiary(diaryNo);
        verify(diaryMapper, times(1)).deleteDiary(memberNo, diaryNo);
    }


    @Test
    @DisplayName("일기가 존재하지 않음")
    void testDiaryDeleteNotFound() {
        when(diaryMapper.findByIdDiary(diaryNo)).thenReturn(null);

        CustomException.NotFoundException exception = assertThrows(
                CustomException.NotFoundException.class,
                () -> diaryService.deleteDiary(memberNo,diaryNo)
        );

        assertEquals("존재하지 않음",exception.getMessage());

        verify(diaryMapper, times(1)).findByIdDiary(diaryNo);
        verify(diaryMapper, never()).deleteDiary(anyLong(), anyLong());
    }


    @Test
    @DisplayName("일기번호 null")
    void testDiaryDeleteNullNo() {
        Long nullDiary = null;

        CustomException.InvalidRequestException exception = assertThrows(
                CustomException.InvalidRequestException.class,
                () -> diaryService.deleteDiary(memberNo, nullDiary)
        );

        assertEquals("존재하지 않습니다.",exception.getMessage());

        verify(diaryMapper, never()).findByIdDiary(anyLong());
    }

    @Test
    @DisplayName("일기 memberNo 불일치")
    void testDiaryDeleteMemberNoMiss() {
        Long missMemberNo = 1000L;
        when(diaryMapper.findByIdDiary(diaryNo)).thenReturn(mockDiaryResponse);

        CustomException.InvalidRequestException exception = assertThrows(
                CustomException.InvalidRequestException.class,
                () -> diaryService.deleteDiary(missMemberNo, diaryNo)
        );

        assertEquals("일치하지 않음",exception.getMessage());

        verify(diaryMapper, times(1)).findByIdDiary(diaryNo);
        verify(diaryMapper, never()).deleteDiary(missMemberNo, diaryNo);
    }


    @Test
    @DisplayName("특정 날짜 일기 조회 성공")
    void testDateDiarySuccess() {
        when(diaryMapper.dateDiary(memberNo,testDate)).thenReturn(mockDiaryResponse);

        DiaryDto.DiaryResponse response = diaryMapper.dateDiary(memberNo, testDate);

        assertNotNull(response);
        assertEquals(1L, response.memberNo());
        assertEquals("Test Title", response.title());

        verify(diaryMapper, times(1)).dateDiary(memberNo, testDate);
    }


    @Test
    @DisplayName("내 일기 목록 조회 성공")
    void testMyDiarySuccess() {
        // given
        DiaryDto.DiaryResponse response1 = new DiaryDto.DiaryResponse(
                1L, 1L, "Title 1", "Content 1", LocalDate.now(), LocalDate.now()
        );
        DiaryDto.DiaryResponse response2 = new DiaryDto.DiaryResponse(
                2L, 1L, "Title 2", "Content 2", LocalDate.now(), LocalDate.now()
        );
        List<DiaryDto.DiaryResponse> mockList = Arrays.asList(response1, response2);

        when(diaryMapper.myDiary(memberNo)).thenReturn(mockList);

        // when
        List<DiaryDto.DiaryResponse> result = diaryService.myDiary(memberNo);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).title());
        assertEquals("Title 2", result.get(1).title());

        verify(diaryMapper, times(1)).myDiary(memberNo);
    }
}
