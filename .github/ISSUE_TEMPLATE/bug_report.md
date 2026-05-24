# BUG-001
**Title: ROOM_MEMBER 테이블 존재하지 않는 deletedDt 컬럼 참조로 채팅 전 기능 500 에러 발생**                                     
                                                                         
  ---                                    
  **Describe the bug**

  채팅방 참여, 나가기, 내 채팅방 목록 조회 등 채팅 관련 전 기능에서 500 Internal Server Error 발생.  
  ROOM_MEMBER 테이블에 존재하지 않는 deletedDt 컬럼을 SQL에서 참조하는 것이 원인.

  ---
  **To Reproduce**

  1. 로그인 후 accessToken 획득
  2. POST /api/v1/chat/join/{roomId} 호출
  3. 500 에러 응답 확인

  ---
  **Expected behavior**

  200 OK 응답과 함께 채팅방 참여 성공

  ---
  **Screenshots**

  SQLSyntaxErrorException: Unknown column 'deletedDt' in 'where clause'
  SQL: SELECT COUNT(*) > 0 FROM ROOM_MEMBER
       WHERE roomId = ? AND member_no = ? AND deletedDt IS NULL

  ---
  **Additional context**

  - 영향 범위: existRoomMember, myChatRoom, updateRead 쿼리 전부 해당
  - 원인 파일: src/main/resources/mapper/ChatMapper.xml
  - CHAT_ROOM 테이블엔 deletedDt 존재하나 ROOM_MEMBER 테이블엔 없음
  - 수정: AND deletedDt IS NULL 조건 3곳 제거

  ---    
# BUG-002
**Title: leaveChat 쿼리 컬럼명 오류로 채팅방 나가기 시 DB 미삭제**                                                                 
                                                                       
  ---                                                                    
  **Describe the bug**                                                       
                                                                         
  채팅방 나가기 실행 시 200 응답은 반환되나 ROOM_MEMBER 테이블에서 실제 row가 삭제되지 않음. 
  SQL 쿼리에서 컬럼명 member_no 대신 memberNo 사용 원인.                                                                 

  ---
  **To Reproduce**

  1. 로그인 후 채팅방 참여
  2. WebSocket /pub/chat/leave 전송 또는 나가기 요청
  3. DB ROOM_MEMBER 테이블 조회
  4. 해당 row가 삭제되지 않은 것 확인

  ---
  **Expected behavior**

  나가기 요청 후 ROOM_MEMBER 테이블에서 해당 사용자의 row 삭제

  ---
  **Screenshots**

  -- 실제 실행된 쿼리 (잘못됨)
  DELETE FROM ROOM_MEMBER
  WHERE roomId = ?
  AND memberNo = ?   ->  존재하지 않는 컬럼명

  -- 올바른 쿼리
  DELETE FROM ROOM_MEMBER
  WHERE roomId = ?
  AND member_no = ?

  ---
  **Additional context**

  - 원인 파일: src/main/resources/mapper/ChatMapper.xml leaveChat 쿼리
  - 전체 테이블에서 컬럼명은 member_no 로 통일되어 있으나 해당 쿼리만 memberNo 사용
  - 0 rows affected로 실행되어 에러 없이 통과되는 버그 발생
  - 수정: memberNo → member_no

  ---
# BUG-003
**Title: leaveChat 서비스 파라미터 순서 역전으로 항상 NotFoundException 발생**
  ---                                                                    
  **Describe the bug**
  
  채팅방 나가기 시 "존재하지 않습니다." 예외가 항상 발생
  ChatServiceImpl.leaveChat() 에서 existRoomMember() 호출 시
  파라미터 순서가 역전되어 roomId값이 memberNo 파라미터로, memberNo값이 roomId 파라미터로 바인딩되는 것이 원인.
  
  ---
  **To Reproduce**

  1. 로그인 후 채팅방 참여
  2. WebSocket `/pub/chat/leave` 전송
  3. "존재하지 않습니다." 예외 응답 확인

  ---
  **Expected behavior**

  정상 퇴장 처리 및 ROOM_MEMBER row 삭제

  ---
  **Screenshots**

 // 매퍼 시그니처
  boolean existRoomMember(@Param("memberNo") Long memberNo,
  @Param("roomId") Long roomId);

  // 잘못된 호출 (ChatServiceImpl.java:118)
  chatMapper.existRoomMember(roomId, memberNo); // 순서 역전

  // 올바른 호출
  chatMapper.existRoomMember(memberNo, roomId);


  ---
  **Additional context**
  
  - 원인 파일:
  src/main/java/back/Quest/service/chat/impl/ChatServiceImpl.java:118
  - 매퍼 파라미터 순서: (memberNo, roomId) 인데 (roomId, memberNo) 로 호출
  - 항상 false 반환 → 항상 예외 발생하는 구조
  - 수정: 파라미터 순서 (roomId, memberNo) → (memberNo, roomId)

  ---
  
