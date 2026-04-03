package back.Quest.service.quiz;

import back.Quest.model.dto.quiz.QuizDto;

import java.util.List;

public interface QuizService {
    void insertQuiz(Long memberNo, QuizDto.QuizRequest request);

    void deleteQuiz(Long memberNo, Long quizNo);

    List<QuizDto.QuizResponse> myQuiz(Long memberNo);

    QuizDto.QuizResponse findById(Long quizNo);

    QuizDto.SolveResponse solveQuiz(Long memberNo, Long quizNo, QuizDto.SolveRequest request);
}