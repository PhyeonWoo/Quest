# Quest

퀴즈, 이미지 퀴즈, 다이어리, 인증 기능을 포함한 Spring Boot 기반 백엔드 프로젝트입니다.  
JWT + Spring Security 기반 인증, Redis 기반 인증 보조 저장소, MyBatis + MySQL 기반 데이터 처리 구조로 구성되어 있습니다.

---

## 📌 프로젝트 소개

**Quest**는 여러 도메인 기능을 모듈 단위로 분리해 구성한 Java/Spring 백엔드 프로젝트입니다.  
현재 저장소 구조를 기준으로 다음과 같은 기능 영역이 확인됩니다.

- 인증(Auth)
- 다이어리(Diary)
- 텍스트 퀴즈(Quiz)
- 이미지 퀴즈(ImgQuiz)
- Missing 기능
- 시도/응답 처리 관련 로직(Attempt)

각 기능은 `controller`, `service`, `mapper`, `model` 계층으로 분리되어 있으며, 유지보수와 확장성을 고려한 구조를 따릅니다.

---

## 🛠 Tech Stack

### Backend
- Java 21
- Spring Boot 4.0.5
- Spring Web
- Spring Validation
- Spring Security
- JWT (`jjwt`)

### Database / Persistence
- MySQL
- MyBatis
- JDBC
- Redis

### Documentation / View / Batch
- Swagger


✨ 주요 특징
1. 인증 및 보안
Spring Security 기반 인증 구조
JWT 기반 로그인 인증 처리
Redis를 활용한 인증 관련 보조 저장소 구성
회원가입 / 로그인 / 로그아웃 / 재발급 흐름을 확장 가능한 구조로 분리
2. 도메인별 계층 분리
기능별로 아래 계층이 분리되어 있어 역할이 명확합니다.

controller : API 요청/응답 처리
service : 비즈니스 로직 처리
mapper : MyBatis 기반 DB 접근
model : DTO / Enum / 데이터 모델 관리
3. 퀴즈 및 기록 기능
텍스트 퀴즈 기능
이미지 퀴즈 기능
다이어리 기능
Missing 관련 기능 분리 구성
4. 문서화 및 테스트
Swagger(OpenAPI) 기반 API 문서화 가능
Auth / Diary 중심의 테스트 코드 포함
Spring Boot Test, Mockito, Spring Security Test 활용
