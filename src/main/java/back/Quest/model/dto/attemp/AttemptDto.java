//package back.Quest.model.dto.attemp;
//
//import back.Quest.model.Enum.ValidationStatus;
//import java.time.LocalDateTime;
//import java.util.List;
//
//public class AttemptDto {
//
//    // ============================================
//    // 퀴즈 풀이 시작 요청
//    // ============================================
//    public record StartAttemptRequest(
//            Long memberNo,
//            Long quizNo
//    ) {}
//
//    // ============================================
//    // 퀴즈 풀이 시작 응답 (문제 정보 포함)
//    // ============================================
//    public record StartAttemptResponse(
//            Long attemptId,
//            Long quizNo,
//            String quizTitle,
//            Integer totalQuestions,
//            LocalDateTime startedAt,
//            List<QuestionDetail> questions
//    ) {
//        public record QuestionDetail(
//                Long questionId,
//                String question,
//                List<DistractDetail> distractList
//        ) {
//            public record DistractDetail(
//                    Long distractNo,
//                    Long questionId,
//                    ValidationStatus validation,
//                    String text
//            ) {}
//        }
//    }
//
//    // ============================================
//    // 사용자 답변 제출 요청
//    // ============================================
//    public record SubmitAnswerRequest(
//            Long questionId,
//            Integer userAnswer,  // 선택지 번호 (1, 2, 3, ...)
//            String answerText    // 주관식용
//    ) {}
//
//    // ============================================
//    // 퀴즈 풀이 제출 응답 (채점 결과)
//    // ============================================
//    public record SubmitAttemptResponse(
//            Long attemptId,
//            Integer score,
//            Integer totalQuestions,
//            Integer correctCount,
//            Double correctRate,
//            Long durationMs,
//            LocalDateTime completedAt,
//            List<AnswerResult> answers
//    ) {
//        public record AnswerResult(
//                Long questionId,
//                String question,
//                Integer userAnswer,
//                Boolean isCorrect,
//                String userAnswerText,
//                String correctAnswerText,
//                String message
//        ) {}
//    }
//
//    // ============================================
//    // 내부 DB DTO (Record 기반 - 평탄화)
//    // ============================================
//
//    // 퀴즈 풀이 기록
//    public record QuizAttempt(
//            Long attemptId,
//            Long memberNo,
//            Long quizNo,
//            LocalDateTime startedAt,
//            LocalDateTime completedAt,
//            Integer score,
//            Long durationMs,
//            LocalDateTime createdDt
//    ) {}
//
//    // 문제 답변 기록
//    public record QuestionAnswer(
//            Long answerId,
//            Long attemptId,
//            Long questionId,
//            Integer userAnswer,
//            Boolean isCorrect,
//            String answerText,
//            LocalDateTime createdDt
//    ) {}
//
//    // 퀴즈 통계
//    public record QuizStatistic(
//            Long statId,
//            Long quizNo,
//            Integer totalAttempts,
//            Double avgScore,
//            Double correctRate,
//            Long avgDurationMs,
//            LocalDateTime statComputedAt
//    ) {}
//
//    // 사용자 퀴즈 진행 현황
//    public record MemberQuizProgress(
//            Long progressId,
//            Long memberNo,
//            Long quizNo,
//            LocalDateTime lastAttemptedAt,
//            Integer bestScore,
//            Integer attemptCount,
//            String status  // NOT_STARTED, IN_PROGRESS, COMPLETED
//    ) {}
//
//    // ============================================
//    // 응답 DTO (Mutable - 표현 계층용)
//    // ============================================
//
//    // 사용자 풀이 히스토리
//    public static class UserAttemptHistoryResponse {
//        public Long memberNo;
//        public Integer totalAttempts;
//        public Double avgScore;
//        public List<AttemptSummary> attempts;
//
//        public UserAttemptHistoryResponse() {
//        }
//
//        public static class AttemptSummary {
//            public Long attemptId;
//            public Long quizNo;
//            public String quizTitle;
//            public Integer score;
//            public Double correctRate;
//            public Integer durationMinutes;
//            public LocalDateTime completedAt;
//
//            public AttemptSummary() {
//            }
//
//            public AttemptSummary(Long attemptId, Long quizNo, String quizTitle,
//                                  Integer score, Double correctRate, Integer durationMinutes,
//                                  LocalDateTime completedAt) {
//                this.attemptId = attemptId;
//                this.quizNo = quizNo;
//                this.quizTitle = quizTitle;
//                this.score = score;
//                this.correctRate = correctRate;
//                this.durationMinutes = durationMinutes;
//                this.completedAt = completedAt;
//            }
//        }
//    }
//
//    // 사용자 퀴즈 진행 현황
//    public static class UserQuizProgressResponse {
//        public Long memberNo;
//        public List<QuizProgressItem> progressItems;
//
//        public UserQuizProgressResponse() {
//        }
//
//        public static class QuizProgressItem {
//            public Long quizNo;
//            public String quizTitle;
//            public String status;
//            public Integer attemptCount;
//            public Integer bestScore;
//            public LocalDateTime lastAttemptedAt;
//            public Boolean isCompleted;
//
//            public QuizProgressItem() {
//            }
//        }
//    }
//
//    // 퀴즈 통계 응답
//    public static class QuizStatisticsResponse {
//        public Long quizNo;
//        public String quizTitle;
//        public Integer totalAttempts;
//        public Double avgScore;
//        public Double correctRate;
//        public String difficultyLevel;  // EASY, MEDIUM, HARD
//        public LocalDateTime lastComputedAt;
//
//        public QuizStatisticsResponse() {
//        }
//
//        public QuizStatisticsResponse(Long quizNo, String quizTitle, Integer totalAttempts,
//                                      Double avgScore, Double correctRate) {
//            this.quizNo = quizNo;
//            this.quizTitle = quizTitle;
//            this.totalAttempts = totalAttempts;
//            this.avgScore = avgScore;
//            this.correctRate = correctRate;
//        }
//    }
//
//    // ============================================
//    // 기존 호환성 유지
//    // ============================================
//
//    // 기존 SolveRequest / SolveResponse 호환
//    public record SolveRequest(
//            int userAnswer
//    ) {}
//
//    public record SolveResponse(
//            boolean isCorrect,
//            String message
//    ) {}
//
//    // 기존 응답 유지
//    public record AttemptResponse(
//            Long attemptId,
//            Long quizNo,
//            Long memberNo,
//            Boolean isCorrect,
//            String message
//    ) {}
//}