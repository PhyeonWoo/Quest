//package back.Quest.service.attemp;
//
//import back.Quest.model.dto.attemp.AttemptDto;
//import back.Quest.model.dto.quiz.QuizDto;
//
//import java.util.List;
//
///**
// * 퀴즈 풀이 Service 인터페이스
// *
// * 책임:
// * 1. 퀴즈 풀이 시작/완료 관리
// * 2. 자동 채점
// * 3. 사용자 학습 현황 추적
// */
//public interface AttemptService {
//
//    // ============================================
//    // 기존 메서드 (호환성 유지)
//    // ============================================
//
//    /**
//     * 퀴즈 풀이 기록 저장 (기존 방식)
//     *
//     * @param memberNo 사용자 번호
//     * @param quizNo 퀴즈 번호
//     * @param request 풀이 요청 (SolveRequest)
//     * @param response 채점 결과 (SolveResponse)
//     * @return 풀이 응답
//     */
//    AttemptDto.AttemptResponse insertAttempt(Long memberNo,
//                                             Long quizNo,
//                                             QuizDto.SolveRequest request,
//                                             QuizDto.SolveResponse response);
//
//    // ============================================
//    // 새로운 메서드 (확장된 기능)
//    // ============================================
//
//    /**
//     * 퀴즈 풀이 시작
//     *
//     * 1. 동시성 제어 (Redis 분산 락)
//     * 2. RECALL_QUIZ_ATTEMPT 레코드 생성
//     * 3. 퀴즈 및 문제 정보 조회
//     *
//     * @param memberNo 사용자 번호
//     * @param quizNo 퀴즈 번호
//     * @return 풀이 시작 응답 (문제 정보 포함)
//     * @throws IllegalStateException 이미 진행 중인 풀이가 있을 때
//     */
//    AttemptDto.StartAttemptResponse startQuizAttempt(Long memberNo, Long quizNo);
//
//    /**
//     * 퀴즈 풀이 제출 및 자동 채점
//     *
//     * 1. 각 답변에 대해 정답 여부 판단
//     * 2. RECALL_QUESTION_ANSWER에 배치 저장
//     * 3. 점수 계산
//     * 4. RECALL_QUIZ_ATTEMPT 업데이트
//     * 5. RECALL_MEMBER_QUIZ_PROGRESS 갱신
//     * 6. 통계 캐시 무효화
//     * 7. 락 해제
//     *
//     * @param attemptId 풀이 ID
//     * @param answers 사용자 답변 목록
//     * @return 채점 결과 응답
//     * @throws IllegalArgumentException 존재하지 않는 풀이
//     */
//    AttemptDto.SubmitAttemptResponse submitQuizAttempt(
//            Long attemptId,
//            List<AttemptDto.SubmitAnswerRequest> answers
//    );
//
//    /**
//     * 사용자의 풀이 히스토리 조회
//     *
//     * 최근 풀이부터 오래된 풀이 순서로 반환
//     * (completedAt DESC)
//     *
//     * @param memberNo 사용자 번호
//     * @param limit 조회 개수 (기본값: 20)
//     * @return 사용자 풀이 히스토리
//     */
//    AttemptDto.UserAttemptHistoryResponse getUserAttemptHistory(
//            Long memberNo,
//            Integer limit
//    );
//
//    /**
//     * 사용자의 퀴즈별 진행 현황 조회
//     *
//     * 각 퀴즈의 상태를 추적
//     * - COMPLETED: 점수 80점 이상
//     * - IN_PROGRESS: 점수 80점 미만
//     * - NOT_STARTED: 아직 풀이하지 않음
//     *
//     * @param memberNo 사용자 번호
//     * @return 퀴즈별 진행 현황
//     */
//    AttemptDto.UserQuizProgressResponse getUserQuizProgress(Long memberNo);
//
//    /**
//     * 특정 풀이의 상세 정보 조회
//     *
//     * 각 문제별 정답 여부, 사용자 답변, 정답 포함
//     *
//     * @param attemptId 풀이 ID
//     * @return 풀이 상세 정보
//     * @throws IllegalArgumentException 존재하지 않는 풀이
//     */
//    AttemptDto.SubmitAttemptResponse getAttemptDetail(Long attemptId);
//
//    /**
//     * 퀴즈 통계 조회
//     *
//     * 해당 퀴즈의 평균 점수, 정답률 등
//     * (캐시된 데이터 사용)
//     *
//     * @param quizNo 퀴즈 번호
//     * @return 퀴즈 통계
//     */
////    AttemptDto.QuizStatisticsResponse getQuizStatistics(Long quizNo);
//}