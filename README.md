# Quest - 퀴즈 & 실종자 관리 플랫폼

[![Java](https://img.shields.io/badge/Java-21%2B-orange)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7-red)](https://redis.io/)
[![Firebase](https://img.shields.io/badge/Firebase-Storage-yellow)](https://firebase.google.com/)

이미지/텍스트 퀴즈 생성, 다이어리 작성, 실종자 정보 관리 기능을 제공하는 **REST API 서버**입니다.


## 🎯 프로젝트 개요

Quest는 고령화 시대를 겪고 있는 대한민국의 알츠하이머 환자가 늘어가면서,
그에 따라서 질병의 경과를 늦추는데 도움이 되도록 사용자가 직접 퀴즈를 만들고 풀 수 있는 학습 플랫폼입니다.
텍스트 퀴즈뿐만 아니라 Firebase 기반의 이미지 퀴즈를 지원하며,
개인 다이어리 작성과 실종자 정보 공유 기능을 함께 제공합니다.


## 주요 특징

✅ 텍스트 & 이미지 기반 퀴즈 생성/풀기\
✅ Firebase Cloud Storage 이미지 업로드\
✅ JWT 기반 인증 (Access + Refresh Token 이중 구조)\
✅ Redis 블랙리스트를 이용한 로그아웃 토큰 무효화\
✅ Redis 캐싱으로 퀴즈 목록 조회 성능 최적화\
✅ 날짜별 개인 다이어리 관리\
✅ 실종자 정보 등록/수정/검색 (지역코드 기반)
<br>
<br>

## 기술 스택

| Category | Technology |
| :--- | :--- |
| **Backend** | Java 21, Spring Boot |
| **Security** | Spring Security, JWT, BCrypt |
| **ORM** | MyBatis |
| **Database** | MySQL |
| **Cache** | Redis |
| **Storage** | Firebase Cloud Storage |
| **Docs** | SpringDoc OpenAPI (Swagger UI) |
| **Test** | JUnit 5, Mockito |
<br>
<br>

## 핵심 구현 기능

**1. 인증/인가 (JWT + Redis)**
- 회원가입 시 ID/이메일 중복 검증, 비밀번호 정규식 검증 (영문+숫자+특수문자 8자 이상)
- Access Token (30분) / Refresh Token (7일) 이중 토큰 구조
- Redis를 이용한 Refresh Token 저장 및 토큰 갱신
- 로그아웃 시 Access Token을 Redis 블랙리스트에 등록하여 재사용 차단

**2. 퀴즈 (Quiz)**
- 텍스트 기반 객관식 퀴즈 생성/삭제/조회
- 보기(Distractor) 포함 다지선다 구성 (CORRECT / INCORRECT 상태 관리)
- 사용자가 정답 번호 제출 시 즉시 채점 및 결과 반환
- Redis 캐싱으로 퀴즈 목록 조회 성능 최적화
- 소유자 검증 — 본인 퀴즈만 삭제 가능

**3. 이미지 퀴즈 (ImgQuiz)**
- multipart/form-data 방식 이미지 업로드 + 퀴즈 생성 동시 처리
- Firebase Cloud Storage 연동 이미지 저장 (UUID 기반 파일명)
- 텍스트 퀴즈와 동일한 채점 로직 적용
- Redis 캐싱 적용

**4. 다이어리 (Diary)**
- 개인 다이어리 CRUD
- 날짜(LocalDate) 기반 단건 조회 지원
- 제목(100자), 내용(500자) 유효성 검사
- JWT 기반 본인 소유 검증

**5. 실종자 관리 (Missing)**
- 실종자 등록/수정/소프트 삭제/전체 조회
- 키워드 및 지역코드(AreaCode) 기반 검색
- 인증 없이 공개 접근 가능
- OPEN / CLOSED 상태 관리 — 종결 사건 수정 차단
<br>

## 아키텍처 설계 포인트

**계층 구조**

Controller → Service (Interface/Impl) → Mapper (MyBatis) → DB

<br>
<br>

## API 구조 요약

| 도메인 | 엔드포인트 | 주요 기능 |
| :--- | :--- | :--- |
| **Auth** | `/api/v1/auth/**` | 회원가입, 로그인, 로그아웃, 토큰 재발급 |
| **Quiz** | `/api/v1/quiz/**` | 퀴즈 생성/삭제/조회, 정답 제출 |
| **ImgQuiz** | `/api/v1/imgQuiz/**` | 이미지 퀴즈 생성/삭제/조회, 정답 제출 |
| **Diary** | `/api/v1/diary/**` | 다이어리 CRUD, 날짜별 조회 |
| **Missing** | `/api/v1/missing/**` | 실종자 등록/수정/검색 (인증 불필요) |

Swagger UI: `http://localhost:8080/swagger-ui.html`
