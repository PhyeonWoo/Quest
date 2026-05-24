# BUG-001
**Title: ROOM_MEMBER 테이블 존재하지 않는 deletedDt 컬럼 참조로 채팅 전 기능 500 에러 발생**                                     
                                                                         
  ---                                    
  **버그 설명**

  채팅방 참여, 나가기, 내 채팅방 목록 조회 등 채팅 관련 전 기능에서 500 Internal Server Error 발생.  
  ROOM_MEMBER 테이블에 존재하지 않는 deletedDt 컬럼을 SQL에서 참조하는 것이 원인.

  ---
  **재현 절차**

  1. 로그인 후 accessToken 획득
  2. POST /api/v1/chat/join/{roomId} 호출
  3. 500 에러 응답 확인

  ---
  **기대 동작**

  200 OK 응답과 함께 채팅방 참여 성공

  ---
  **스크린샷 / 코드**

  SQLSyntaxErrorException: Unknown column 'deletedDt' in 'where clause'
  SQL: SELECT COUNT(*) > 0 FROM ROOM_MEMBER
       WHERE roomId = ? AND member_no = ? AND deletedDt IS NULL

  ---
   **추가 정보**

  - 영향 범위: existRoomMember, myChatRoom, updateRead 쿼리 전부 해당
  - 원인 파일: src/main/resources/mapper/ChatMapper.xml
  - CHAT_ROOM 테이블엔 deletedDt 존재하나 ROOM_MEMBER 테이블엔 없음
  - 수정: AND deletedDt IS NULL 조건 3곳 제거

  ---    
# BUG-002
**Title: leaveChat 쿼리 컬럼명 오류로 채팅방 나가기 시 DB 미삭제**                                                                 
                                                                       
  ---                                                                    
  **버그 설명**                                                     
                                                                         
  채팅방 나가기 실행 시 200 응답은 반환되나 ROOM_MEMBER 테이블에서 실제 row가 삭제되지 않음. 
  SQL 쿼리에서 컬럼명 member_no 대신 memberNo 사용 원인.                                                                 

  ---
  **재현 절차**

  1. 로그인 후 채팅방 참여
  2. WebSocket /pub/chat/leave 전송 또는 나가기 요청
  3. DB ROOM_MEMBER 테이블 조회
  4. 해당 row가 삭제되지 않은 것 확인

  ---
  **기대 동작**

  나가기 요청 후 ROOM_MEMBER 테이블에서 해당 사용자의 row 삭제

  ---
  **스크린샷 / 코드**

  -- 실제 실행 쿼리
  DELETE FROM ROOM_MEMBER
  WHERE roomId = ?
  AND memberNo = ?   ->  존재하지 않는 컬럼명

  -- 올바른 쿼리
  DELETE FROM ROOM_MEMBER
  WHERE roomId = ?
  AND member_no = ?

  ---
  **추가 정보**

  - 원인 파일: src/main/resources/mapper/ChatMapper.xml leaveChat 쿼리
  - 전체 테이블에서 컬럼명은 member_no 로 통일되어 있으나 해당 쿼리만 memberNo 사용
  - 0 rows affected로 실행되어 에러 없이 통과되는 버그 발생
  - 수정: memberNo → member_no

  ---
  
# BUG-003
**Title: leaveChat 서비스 파라미터 순서 역전으로 항상 NotFoundException 발생**
  ---                                                                    
  **버그 설명**       
  
  채팅방 나가기 시 "존재하지 않습니다." 예외가 항상 발생
  ChatServiceImpl.leaveChat() 에서 existRoomMember() 호출 시
  파라미터 순서가 역전되어 roomId값이 memberNo 파라미터로, memberNo값이 roomId 파라미터로 바인딩되는 것이 원인.
  
  ---
  **재현 절차**

  1. 로그인 후 채팅방 참여
  2. WebSocket `/pub/chat/leave` 전송
  3. "존재하지 않습니다." 예외 응답 확인

  ---
  **기대 동작**

  정상 퇴장 처리 및 ROOM_MEMBER row 삭제

  ---
  **스크린샷 / 코드**

 // 매퍼 시그니처
  boolean existRoomMember(@Param("memberNo") Long memberNo,
  @Param("roomId") Long roomId);

  // 잘못된 호출 (ChatServiceImpl.java:118)
  chatMapper.existRoomMember(roomId, memberNo); // 순서 역전

  // 올바른 호출
  chatMapper.existRoomMember(memberNo, roomId);


  ---
  **추가 정보**
  
  - 원인 파일:
  src/main/java/back/Quest/service/chat/impl/ChatServiceImpl.java:118
  - 매퍼 파라미터 순서: (memberNo, roomId) 인데 (roomId, memberNo) 로 호출
  - 항상 false 반환 → 항상 예외 발생하는 구조
  - 수정: 파라미터 순서 (roomId, memberNo) → (memberNo, roomId)

  ---
  # BUG-004
**Title: SecurityConfig 규칙 순서 오류로 logout 엔드포인트 인증 없이 접근 가능**
  ---                                                                    
  **버그 설명**       
  
 Spring Security filterChain에서        
  `/api/v1/auth/logout` authenticated() 규칙이                           
  `/api/v1/auth/**` permitAll() 규칙보다 뒤에 선언되어                   
  logout 엔드포인트에 JWT 없이 접근 가능.
  
  ---
  **재현 절차**

  1. 로그인 없이 DELETE /api/v1/auth/logout 요청
  2. Authorization 헤더 없음
  3. 기대: 401 Unauthorized
  4. 실제: 200


  ---
  **기대 동작**

  Authorization 헤더 없는 logout 요청 시 401 Unauthorized 반환.
  유효한 JWT 토큰 보유 사용자만 logout 가능해야 함

  ---
  **스크린샷 / 코드**

 // 잘못된 순서 (SecurityConfig.java)
  .requestMatchers("/api/v1/auth/**").permitAll()        
  .requestMatchers("/api/v1/auth/logout").authenticated()  // ← 도달 안됨

  // 올바른 순서
  .requestMatchers("/api/v1/auth/logout").authenticated()  // ← logout 먼저 선언
  .requestMatchers("/api/v1/auth/**").permitAll()

  ---
  **추가 정보**
  
  - 원인 파일: src/main/java/back/Quest/config/SecurityConfig.java
  - Spring Security 규칙은 first-match → 구체적 경로를 상위에 선언해야 함
  - 영향: 인증되지 않은 사용자가 logout API 호출 가능, Redis 블랙리스트 우회 가능
  - 수정: authenticated() 규칙을 permitAll() 규칙보다 앞으로 이동
  ---
  # BUG-005
**Title: 마지막 멤버 퇴장 시 채팅방 자동 삭제 안됨**
  ---                                                                    
  **버그 설명**       
  
  채팅방에서 모든 멤버가 퇴장해도 CHAT_ROOM이 소프트 삭제(deletedDt)되지 않음.
  빈 채팅방이 전체 목록에 계속 노출됨.
  
  ---
  **재현 절차**

 1. 채팅방 생성 (멤버 1명)
  2. WebSocket /chat/leave 메시지 전송
  3. GET /api/v1/chat/all 호출
  4. 기대: 해당 채팅방 목록에서 제거
  5. 실제: deletedDt NULL인 채로 목록에 계속 노출

  ---
  **기대 동작**

  마지막 멤버 퇴장 시 CHAT_ROOM.deletedDt = NOW() 로 업데이트.
  전체 채팅방 목록 조회 시 해당 방 노출 안 됨.

  ---
  **스크린샷 / 코드**

 // 현재 코드 (ChatServiceImpl.java) - 잔여 멤버 체크 없음
  public void leaveChat(Long roomId, Long memberNo) {
      boolean exists = chatMapper.existRoomMember(memberNo, roomId);
      if (!exists) {
          throw new CustomException.NotFoundException("존재하지않습니다.");
      }
      chatMapper.leaveChat(roomId, memberNo);
      // ← 여기서 잔여 멤버 수 확인 및 채팅방 삭제 로직 없음
  }

  // 수정 방향
  chatMapper.leaveChat(roomId, memberNo);
  int remaining = chatMapper.countRoomMembers(roomId);
  if (remaining == 0) {
      chatMapper.deleteRoomById(roomId); // deletedDt = NOW()
  }


  ---
  **추가 정보**
  
  - 원인 파일:src/main/java/back/Quest/service/chat/impl/ChatServiceImpl.java
  - 관련 파일: src/main/resources/mapper/ChatMapper.xml
  - 영향: 빈 채팅방 DB 누적, 사용자에게 입장 불가한 빈 방 목록 노출
  - 수정: leaveChat() 이후 ROOM_MEMBER 잔여 수 조회 → 0이면 채팅방 소프트
   삭제
  ---

  
