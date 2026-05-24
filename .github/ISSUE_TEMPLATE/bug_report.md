---
name: Bug report
about: 버그 및 취약점 리포트 (BUG-002)
title: ''
labels: ''
assignees: PhyeonWoo

---

Title: leaveChat 쿼리 컬럼명 오류로 채팅방 나가기 시 DB 미삭제                                                                 
                                                                       
  ---                                                                    
  Describe the bug                                                       
                                                                         
  채팅방 나가기 실행 시 200 응답은 반환되나 ROOM_MEMBER 테이블에서 실제 row가 삭제되지 않음. 
  SQL 쿼리에서 컬럼명 member_no 대신 memberNo 사용 원인.                                                                 

  ---
  To Reproduce

  1. 로그인 후 채팅방 참여
  2. WebSocket /pub/chat/leave 전송 또는 나가기 요청
  3. DB ROOM_MEMBER 테이블 조회
  4. 해당 row가 삭제되지 않은 것 확인

  ---
  Expected behavior

  나가기 요청 후 ROOM_MEMBER 테이블에서 해당 사용자의 row 삭제

  ---
  Screenshots

  -- 실제 실행된 쿼리 (잘못됨)
  DELETE FROM ROOM_MEMBER
  WHERE roomId = ?
  AND memberNo = ?   ->  존재하지 않는 컬럼명

  -- 올바른 쿼리
  DELETE FROM ROOM_MEMBER
  WHERE roomId = ?
  AND member_no = ?

  ---
  Desktop
  - OS: macOS
  - Server: Spring Boot 4.x, MySQL 8.x

  ---
  Additional context

  - 원인 파일: src/main/resources/mapper/ChatMapper.xml leaveChat 쿼리
  - 전체 테이블에서 컬럼명은 member_no 로 통일되어 있으나 해당 쿼리만 memberNo 사용
  - 0 rows affected로 실행되어 에러 없이 통과되는 버그 발생
  - 수정: memberNo → member_no
