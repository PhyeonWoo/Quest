---
name: Bug report
about: 버그 및 취약점 리포트 (BUG-001)
title: ''
labels: ''
assignees: ''

---

Title: ROOM_MEMBER 테이블 존재하지 않는 deletedDt 컬럼 참조로 채팅 전 기능 500 에러 발생                                      
                                                                         
  ---                                    
  Describe the bug

  채팅방 참여, 나가기, 내 채팅방 목록 조회 등 채팅 관련 전
  기능에서 500 Internal Server Error 발생. ROOM_MEMBER 테이블에 존재하지
  않는 deletedDt 컬럼을 SQL에서 참조하는 것이 원인.

  ---
  To Reproduce

  1. 로그인 후 accessToken 획득
  2. POST /api/v1/chat/join/{roomId} 호출
  3. 500 에러 응답 확인

  ---
  Expected behavior

  200 OK 응답과 함께 채팅방 참여 성공

  ---
  Screenshots

  SQLSyntaxErrorException: Unknown column 'deletedDt' in 'where clause'
  SQL: SELECT COUNT(*) > 0 FROM ROOM_MEMBER
       WHERE roomId = ? AND member_no = ? AND deletedDt IS NULL

  ---
  Desktop
  - OS: macOS
  - Server: Spring Boot 4.x, MySQL 8.x

  ---
  Additional context

  - 영향 범위: existRoomMember, myChatRoom, updateRead 쿼리 전부 해당
  - 원인 파일: src/main/resources/mapper/ChatMapper.xml
  - CHAT_ROOM 테이블엔 deletedDt 존재하나 ROOM_MEMBER 테이블엔 없음
  - 수정: AND deletedDt IS NULL 조건 3곳 제거
