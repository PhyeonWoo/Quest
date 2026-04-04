package back.Quest.service.quiz.impl;

import back.Quest.config.exception.CustomException;
import back.Quest.mapper.quiz.QuizMapper;
import back.Quest.model.dto.quiz.QuizDto;
import back.Quest.model.Enum.ValidationStatus;
import back.Quest.service.quiz.QuizService;
import back.Quest.service.quiz.assembler.QuizAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuizServiceImpl implements QuizService {
    private final QuizMapper quizMapper;

    @Override
    @CacheEvict(value = "quizList", key = "#memberNo")
    public void insertQuiz(Long memberNo, QuizDto.QuizRequest request) {
        log.info("Insert Quiz Request");

        quizMapper.insertQuiz(memberNo, request);
        Long quizNo = quizMapper.selectLastInsertId();

        int distractCount = quizMapper.insertDistract(quizNo, request.list());
        if(quizNo == 0 || distractCount == 0) {
            log.error("Insert Fail");
            throw new CustomException.InvalidRequestException("생성 안됨");
        }

        log.info("Insert Quiz Success");
    }



    @Override
    @CacheEvict(value = "quizList", key = "#memberNo")
    public void deleteQuiz(Long memberNo, Long quizNo) {
        log.info("Delete Quiz Request");
        int deleteCount = quizMapper.deleteQuiz(memberNo, quizNo);
        if(deleteCount == 0) {
            log.warn("Delete Fail");
            throw new CustomException.InvalidRequestException("삭제 실패");
        }

        log.info("Delete Quiz Success");
    }


    @Override
    @Cacheable(value = "quizList", key = "#memberNo")
    public List<QuizDto.QuizResponse> myQuiz(Long memberNo) {
        log.info("MyQuiz find Request");

        List<QuizDto.QuizFlatResponse> response = quizMapper.myQuiz(memberNo);

        if (response.isEmpty()) {
            log.warn("Not Found My Quiz");
            return Collections.emptyList();
        }

        log.info("MyQuiz find Response Success");
        return QuizAssembler.toGroup(response);
    }

    @Override
    public QuizDto.QuizResponse findById(Long quizNo) {
        log.info("Single find Quiz Request");

        List<QuizDto.QuizFlatResponse> response = quizMapper.findById(quizNo);
        if (response == null || response.isEmpty()) {
            throw new CustomException.NotFoundException("존재하지 않음");
        }

        log.info("Single find Quiz Response Success");
        return QuizAssembler.toGroup(response).getFirst();
    }


    @Override
    public QuizDto.SolveResponse solveQuiz(Long memberNo, Long quizNo, QuizDto.SolveRequest request) {
        QuizDto.QuizResponse quiz = findById(quizNo);

        List<QuizDto.DistractResponse> distract = quiz.response();
        if (distract == null || distract.isEmpty()) {
            log.warn("Distract Not Found");
            throw new CustomException.NotFoundException("보기가 존재하지 않습니다.");
        }

        int answer = request.userAnswer();
        if (answer < 1 || answer > distract.size()) {
            throw new CustomException.InvalidRequestException("유효하지 않은 정답 번호입니다.");
        }

        boolean isCorrect =  distract.get(answer - 1).validation() == ValidationStatus.CORRECT;
        return new QuizDto.SolveResponse(
                isCorrect, isCorrect ? "정답입니다" : "오답입니다.");
    }
}
