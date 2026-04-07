//package back.Quest.service.attemp.impl;
//
//import back.Quest.mapper.attemp.AttemptMapper;
//import back.Quest.model.dto.attemp.AttemptDto;
//import back.Quest.model.dto.quiz.QuizDto;
//import back.Quest.service.attemp.AttemptService;
//import back.Quest.service.quiz.QuizService;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@Transactional
//public class AttemptServiceImpl implements AttemptService {
//
//    private final AttemptMapper attemptMapper;
//    private final QuizService quizService;
//    private final RedisTemplate<String, String> redisTemplate;
//
//    private static final String ATTEMPT_LOCK_KEY_PREFIX = "quiz:attempt:lock:";
//    private static final long LOCK_TIMEOUT_SECONDS = 30;
//
//    /**
//     * 퀴즈 풀이 시작
//     */
//    @Override
//    public AttemptDto.StartAttemptResponse startQuizAttempt(Long memberNo, Long quizNo) {
//        log.info("Starting quiz attempt - memberNo: {}, quizNo: {}", memberNo, quizNo);
//
//        // 1. 동시성 제어 (Redis 분산 락)
//        String lockKey = ATTEMPT_LOCK_KEY_PREFIX + memberNo + ":" + quizNo;
//        Boolean acquired = redisTemplate.opsForValue()
//                .setIfAbsent(lockKey, "1", java.time.Duration.ofSeconds(LOCK_TIMEOUT_SECONDS));
//
//        if (!acquired) {
//            throw new IllegalStateException("이미 진행 중인 풀이가 있습니다. 먼저 완료해주세요.");
//        }
//
//        try {
//            // 2. 풀이 기록 생성
//            AttemptDto.QuizAttempt attempt = new AttemptDto.QuizAttempt(
//                    null,
//                    memberNo,
//                    quizNo,
//                    LocalDateTime.now(),
//                    null,
//                    null,
//                    null,
//                    LocalDateTime.now()
//            );
//
//            attemptMapper.insertQuizAttempt(attempt);
//            Long attemptId = attempt.attemptId();
//
//            // 3. 퀴즈 및 문제 정보 조회
//            // QuizService에서 문제 정보 조회 (기존 구조 활용)
//            List<QuizDto.QuizFlatResponse> quizzes = quizService.getQuizzesByQuizNo(quizNo);
//
//            // 4. QuizFlatResponse를 StartAttemptResponse 형식으로 변환
//            Map<Long, List<AttemptDto.StartAttemptResponse.QuestionDetail.DistractDetail>> groupedByQuestion =
//                    quizzes.stream()
//                            .collect(Collectors.groupingBy(
//                                    q -> q.quizNo(),
//                                    Collectors.mapping(
//                                            q -> new AttemptDto.StartAttemptResponse.QuestionDetail.DistractDetail(
//                                                    q.distractNo(),
//                                                    q.quizNo(),
//                                                    q.validation(),
//                                                    q.text()
//                                            ),
//                                            Collectors.toList()
//                                    )
//                            ));
//
//            List<AttemptDto.StartAttemptResponse.QuestionDetail> questionDetails =
//                    quizzes.stream()
//                            .map(q -> new AttemptDto.StartAttemptResponse.QuestionDetail(
//                                    q.quizNo(),
//                                    q.question(),
//                                    groupedByQuestion.getOrDefault(q.quizNo(), List.of())
//                            ))
//                            .distinct()
//                            .collect(Collectors.toList());
//
//            AttemptDto.StartAttemptResponse response = new AttemptDto.StartAttemptResponse(
//                    attemptId,
//                    quizNo,
//                    quizzes.isEmpty() ? null : quizzes.get(0).question(),
//                    questionDetails.size(),
//                    LocalDateTime.now(),
//                    questionDetails
//            );
//
//            log.info("Quiz attempt started: memberNo={}, quizNo={}, attemptId={}",
//                    memberNo, quizNo, attemptId);
//
//            return response;
//
//        } catch (Exception e) {
//            redisTemplate.delete(lockKey);
//            throw e;
//        }
//    }
//
//    /**
//     * 퀴즈 풀이 제출 및 채점
//     *
//     * userAnswer: 선택지 번호 (1, 2, 3, ...)
//     */
//    @Override
//    public AttemptDto.SubmitAttemptResponse submitQuizAttempt(
//            Long attemptId,
//            List<AttemptDto.SubmitAnswerRequest> answers) {
//
//        log.info("Submitting quiz attempt - attemptId: {}, answers count: {}",
//                attemptId, answers.size());
//
//        // 1. 풀이 정보 조회
//        AttemptDto.QuizAttempt attempt = attemptMapper.selectAttemptById(attemptId);
//        if (attempt == null) {
//            throw new IllegalArgumentException("존재하지 않는 풀이입니다.");
//        }
//
//        Long memberNo = attempt.memberNo();
//        Long quizNo = attempt.quizNo();
//        LocalDateTime startedAt = attempt.startedAt();
//
//        // 2. 답변 검증 및 저장 (배치)
//        List<AttemptDto.QuestionAnswer> answerList = answers.stream()
//                .map(answer -> {
//                    // userAnswer로 정답 여부 판단
//                    // QuizService에서 userAnswer를 검증
//                    boolean isCorrect = quizService.validateAnswer(
//                            answer.questionId(),
//                            answer.userAnswer()
//                    );
//
//                    return new AttemptDto.QuestionAnswer(
//                            null,
//                            attemptId,
//                            answer.questionId(),
//                            answer.userAnswer(),
//                            isCorrect,
//                            answer.answerText(),
//                            LocalDateTime.now()
//                    );
//                })
//                .collect(Collectors.toList());
//
//        attemptMapper.insertQuestionAnswerBatch(answerList);
//
//        // 3. 점수 계산
//        long correctCount = answerList.stream()
//                .filter(AttemptDto.QuestionAnswer::isCorrect)
//                .count();
//
//        int score = (int) ((correctCount * 100) / answers.size());
//        LocalDateTime completedAt = LocalDateTime.now();
//        long durationMs = ChronoUnit.MILLIS.between(startedAt, completedAt);
//
//        // 4. 풀이 기록 업데이트
//        attemptMapper.completeAttempt(attemptId, score, completedAt, durationMs);
//
//        // 5. 사용자 학습 현황 갱신
//        updateMemberQuizProgress(memberNo, quizNo, score);
//
//        // 6. 통계 캐시 무효화
//        String cacheKey = "quiz:stat:" + quizNo;
//        redisTemplate.delete(cacheKey);
//
//        // 7. 락 해제
//        String lockKey = ATTEMPT_LOCK_KEY_PREFIX + memberNo + ":" + quizNo;
//        redisTemplate.delete(lockKey);
//
//        // 8. Response 생성
//        List<AttemptDto.SubmitAttemptResponse.AnswerResult> answerResults =
//                answerList.stream()
//                        .map(a -> new AttemptDto.SubmitAttemptResponse.AnswerResult(
//                                a.questionId(),
//                                null,  // question은 별도 조회 필요
//                                a.userAnswer(),
//                                a.isCorrect(),
//                                a.answerText(),
//                                null,  // correctAnswerText는 별도 조회 필요
//                                a.isCorrect() ? "정답입니다!" : "오답입니다."
//                        ))
//                        .collect(Collectors.toList());
//
//        AttemptDto.SubmitAttemptResponse response = new AttemptDto.SubmitAttemptResponse(
//                attemptId,
//                score,
//                answers.size(),
//                (int) correctCount,
//                (double) correctCount / answers.size() * 100,
//                durationMs,
//                completedAt,
//                answerResults
//        );
//
//        log.info("Quiz attempt completed: memberNo={}, quizNo={}, attemptId={}, score={}",
//                memberNo, quizNo, attemptId, score);
//
//        return response;
//    }
//
//    /**
//     * 사용자의 풀이 히스토리 조회
//     */
//    @Override
//    public AttemptDto.UserAttemptHistoryResponse getUserAttemptHistory(Long memberNo, Integer limit) {
//        log.info("Fetching attempt history - memberNo: {}, limit: {}", memberNo, limit);
//
//        List<AttemptDto.QuizAttempt> attempts = attemptMapper
//                .selectMemberAttemptHistory(memberNo, limit != null ? limit : 20);
//
//        AttemptDto.UserAttemptHistoryResponse response = new AttemptDto.UserAttemptHistoryResponse();
//
//        if (attempts.isEmpty()) {
//            response.memberNo = memberNo;
//            response.totalAttempts = 0;
//            response.avgScore = 0.0;
//            response.attempts = new ArrayList<>();
//            return response;
//        }
//
//        response.memberNo = memberNo;
//        response.totalAttempts = attempts.size();
//        response.avgScore = attempts.stream()
//                .mapToInt(AttemptDto.QuizAttempt::score)
//                .average()
//                .orElse(0.0);
//
//        response.attempts = attempts.stream()
//                .map(attempt -> new AttemptDto.UserAttemptHistoryResponse.AttemptSummary(
//                        attempt.attemptId(),
//                        attempt.quizNo(),
//                        null,  // quizTitle은 별도 JOIN 필요
//                        attempt.score(),
//                        (double) (attempt.score() != null ? attempt.score() : 0) / 100.0,
//                        calculateDurationMinutes(attempt.startedAt(), attempt.completedAt()),
//                        attempt.completedAt()
//                ))
//                .collect(Collectors.toList());
//
//        return response;
//    }
//
//    /**
//     * 사용자의 퀴즈 진행 현황
//     */
//    @Override
//    public AttemptDto.UserQuizProgressResponse getUserQuizProgress(Long memberNo) {
//        log.info("Fetching quiz progress - memberNo: {}", memberNo);
//
//        List<AttemptDto.MemberQuizProgress> progresses = attemptMapper
//                .selectMemberAllProgress(memberNo);
//
//        AttemptDto.UserQuizProgressResponse response = new AttemptDto.UserQuizProgressResponse();
//        response.memberNo = memberNo;
//        response.progressItems = progresses.stream()
//                .map(p -> {
//                    AttemptDto.UserQuizProgressResponse.QuizProgressItem item =
//                            new AttemptDto.UserQuizProgressResponse.QuizProgressItem();
//                    item.quizNo = p.quizNo();
//                    item.status = p.status();
//                    item.attemptCount = p.attemptCount();
//                    item.bestScore = p.bestScore();
//                    item.lastAttemptedAt = p.lastAttemptedAt();
//                    item.isCompleted = "COMPLETED".equals(p.status());
//                    return item;
//                })
//                .collect(Collectors.toList());
//
//        return response;
//    }
//
//    /**
//     * 특정 풀이 상세 조회
//     */
//    @Override
//    public AttemptDto.SubmitAttemptResponse getAttemptDetail(Long attemptId) {
//        log.info("Fetching attempt detail - attemptId: {}", attemptId);
//
//        AttemptDto.QuizAttempt attempt = attemptMapper.selectAttemptById(attemptId);
//        if (attempt == null) {
//            throw new IllegalArgumentException("존재하지 않는 풀이입니다.");
//        }
//
//        List<AttemptDto.QuestionAnswer> answers = attemptMapper
//                .selectAnswersByAttempt(attemptId);
//
//        long correctCount = answers.stream()
//                .filter(AttemptDto.QuestionAnswer::isCorrect)
//                .count();
//
//        long durationMs = ChronoUnit.MILLIS.between(
//                attempt.startedAt(),
//                attempt.completedAt()
//        );
//
//        List<AttemptDto.SubmitAttemptResponse.AnswerResult> answerResults =
//                answers.stream()
//                        .map(a -> new AttemptDto.SubmitAttemptResponse.AnswerResult(
//                                a.questionId(),
//                                null,
//                                a.userAnswer(),
//                                a.isCorrect(),
//                                a.answerText(),
//                                null,
//                                a.isCorrect() ? "정답입니다!" : "오답입니다."
//                        ))
//                        .collect(Collectors.toList());
//
//        return new AttemptDto.SubmitAttemptResponse(
//                attemptId,
//                attempt.score(),
//                answers.size(),
//                (int) correctCount,
//                (double) correctCount / answers.size() * 100,
//                durationMs,
//                attempt.completedAt(),
//                answerResults
//        );
//    }
//
//    /**
//     * 퀴즈 통계 조회
//     */
////    @Override
////    public AttemptDto.QuizStatisticsResponse getQuizStatistics(Long quizNo) {
////        log.info("Fetching quiz statistics - quizNo: {}", quizNo);
////
////        AttemptDto.QuizStatistic stat = attemptMapper.selectQuizStatByQuizNo(quizNo);
////
////        if (stat == null) {
////            return new AttemptDto.QuizStatisticsResponse(quizNo, null, 0, 0.0, 0.0);
////        }
////
////        return new AttemptDto.QuizStatisticsResponse(
////                quizNo,
////                null,
////                stat.totalAttempts(),
////                stat.avgScore(),
////                stat.correctRate()
////        );
////    }
//
//    /**
//     * 기존 메서드 (호환성)
//     */
//    @Override
//    public AttemptDto.AttemptResponse insertAttempt(Long memberNo, Long quizNo,
//                                                    QuizDto.SolveRequest request,
//                                                    QuizDto.SolveResponse response) {
//        AttemptDto.QuizAttempt attempt = new AttemptDto.QuizAttempt(
//                null,
//                memberNo,
//                quizNo,
//                LocalDateTime.now(),
//                null,
//                null,
//                null,
//                LocalDateTime.now()
//        );
//
//        attemptMapper.insertQuizAttempt(attempt);
//
//        return new AttemptDto.AttemptResponse(
//                attempt.attemptId(),
//                quizNo,
//                memberNo,
//                response.isCorrect(),
//                response.message()
//        );
//    }
//
//    // ============================================
//    // Private Methods
//    // ============================================
//
//    /**
//     * 사용자 학습 현황 갱신
//     */
//    private void updateMemberQuizProgress(Long memberNo, Long quizNo, int score) {
//        AttemptDto.MemberQuizProgress existing = attemptMapper
//                .selectMemberQuizProgress(memberNo, quizNo);
//
//        Integer newAttemptCount = existing != null ? existing.attemptCount() + 1 : 1;
//        Integer bestScore = existing != null
//                ? Math.max(existing.bestScore(), score)
//                : score;
//
//        String status = score >= 80 ? "COMPLETED" : "IN_PROGRESS";
//
//        attemptMapper.upsertMemberQuizProgress(memberNo, quizNo, status, newAttemptCount, bestScore);
//
//        log.debug("Member quiz progress updated - memberNo: {}, quizNo: {}, status: {}",
//                memberNo, quizNo, status);
//    }
//
//    /**
//     * 소요 시간 계산 (분 단위)
//     */
//    private Integer calculateDurationMinutes(LocalDateTime startedAt, LocalDateTime completedAt) {
//        if (startedAt == null || completedAt == null) return 0;
//        return (int) ChronoUnit.MINUTES.between(startedAt, completedAt);
//    }
//}