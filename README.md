# ARAMI v3

전자정부 프레임워크 기반의 Spring Boot 웹 애플리케이션입니다. JWT 인증을 사용하는 RESTful API 서버입니다.

## 📋 목차

- [프로젝트 개요](#-프로젝트-개요)
- [기술 스택](#-기술-스택)
- [주요 기능](#-주요-기능)
- [프로젝트 구조](#-프로젝트-구조)
- [시작하기](#-시작하기)
- [API 문서](#-api-문서)
- [보안 설정](#-보안-설정)
- [개발 가이드](#-개발-가이드)

## 🎯 프로젝트 개요

**ARAMI v3**는 전자정부 표준 프레임워크를 기반으로 구축된 엔터프라이즈급 웹 애플리케이션입니다.

- **버전**: 1.0.0
- **그룹**: co.kr.uaram
- **Java 버전**: 25
- **빌드 도구**: Gradle
- **서버 포트**: 8080

## 🛠 기술 스택

### Backend
- **Spring Boot 3.5.6** - 메인 프레임워크
- **Spring Security** - 인증/인가 처리
- **JWT (JSON Web Token)** - 토큰 기반 인증
- **전자정부 표준프레임워크** - 공공기관 표준 개발 프레임워크
- **Lombok** - 코드 간소화
- **Logback** - 로깅

### Database
- **MySQL 8.x** - 메인 데이터베이스
- **HSQLDB** - 내장 데이터베이스 (테스트용)
- **DBCP2** - 커넥션 풀

### API Documentation
- **Swagger/OpenAPI 3** - API 문서 자동화
- **SpringDoc** - Spring Boot 3 호환 Swagger

### Additional Libraries
- **JJWT** - JWT 토큰 생성 및 검증
- **Commons FileUpload** - 파일 업로드 처리
- **Hibernate Validator** - 데이터 검증
- **Log4JDBC** - SQL 로깅

### Testing
- **JUnit** - 단위 테스트
- **Selenium** - E2E 테스트
- **Spring Boot Test** - 통합 테스트

## 🚀 주요 기능

### 1. JWT 기반 인증 시스템
- 토큰 기반 Stateless 인증
- Access Token 유효시간: 24시간
- Bearer 토큰 및 일반 토큰 모두 지원
- 자동 토큰 검증 및 사용자 인증

### 2. Spring Security 통합
- Role 기반 접근 제어 (ROLE_ADMIN, ROLE_USER)
- CORS 설정 (http://localhost:3000)
- CSRF 보호 비활성화 (Stateless API)
- 커스텀 AccessDeniedHandler 및 AuthenticationEntryPoint

### 3. 파일 관리
- 멀티파트 파일 업로드 (최대 100MB)
- 파일 확장자 화이트리스트 검증
- 파일 저장 경로: `./files`

### 4. API 문서화
- Swagger UI 자동 생성
- API 엔드포인트 자동 문서화
- 접근 URL: http://localhost:8080/swagger-ui.html

### 5. 로깅
- 롤링 파일 로그 (최대 10MB, 30일 보관)
- 로그 저장 경로: `./logs`
- SQL 쿼리 로깅 지원

## 📁 프로젝트 구조

```
aramiv3/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── arami/                    # 비즈니스 로직
│   │   │   │   ├── common/              # 공통 모듈
│   │   │   │   │   ├── auth/           # 권한 관리
│   │   │   │   │   └── CommonService.java
│   │   │   │   ├── member/             # 회원 관리
│   │   │   │   │   ├── service/       # 비즈니스 로직
│   │   │   │   │   └── web/           # 컨트롤러
│   │   │   │   ├── config/            # 설정 클래스
│   │   │   │   └── logs/              # 로깅 모듈
│   │   │   └── egovframework/          # 전자정부 프레임워크
│   │   │       ├── EgovBootApplication.java  # 메인 클래스
│   │   │       ├── com/
│   │   │       │   ├── cmm/           # 공통 컴포넌트
│   │   │       │   ├── config/        # 설정
│   │   │       │   ├── jwt/           # JWT 인증
│   │   │       │   │   ├── EgovJwtTokenUtil.java
│   │   │       │   │   ├── JwtAuthenticationFilter.java
│   │   │       │   │   └── JwtAuthenticationEntryPoint.java
│   │   │       │   ├── security/      # Spring Security
│   │   │       │   │   └── SecurityConfig.java
│   │   │       │   └── sns/           # SNS 로그인
│   │   │       └── let/               # 표준 컴포넌트
│   │   │           └── uat/uia/       # 로그인/로그아웃
│   │   └── resources/
│   │       ├── application.properties  # 설정 파일
│   │       └── application.yml
│   └── test/                          # 테스트 코드
├── build.gradle                       # Gradle 빌드 설정
├── settings.gradle
└── README.md                          # 프로젝트 문서
```

## 🏃 시작하기

### 필수 요구사항

- **Java 25** 이상
- **Gradle 7.x** 이상
- **MySQL 8.x** (운영 환경)
- **Node.js** (프론트엔드 개발 시)

### 데이터베이스 설정

1. MySQL 서버에 데이터베이스 생성:
```sql
CREATE DATABASE water CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. `application-dev.properties`(또는 프로파일별 properties / 운영 시 `GLOBALS_MYSQL_URL` 등)에서 데이터베이스 정보 수정:
```properties
Globals.mysql.Url = jdbc:mysql://YOUR_HOST:3306/water?autoReconnect=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul&allowMultiQueries=true
Globals.mysql.UserName = YOUR_USERNAME
Globals.mysql.Password = YOUR_PASSWORD
```

### JWT Secret Key 변경 (필수)

보안을 위해 JWT Secret Key를 반드시 변경하세요:

```properties
# application.properties
Globals.jwt.secret = YOUR_NEW_SECRET_KEY_HERE
```

Secret Key 생성 방법:
```bash
# Base64로 인코딩된 256비트 키 생성
openssl rand -base64 32
```

### 애플리케이션 실행

#### 방법 1: Gradle 사용
```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

#### 방법 2: IDE 실행
1. IntelliJ IDEA 또는 Eclipse에서 프로젝트 열기
2. `EgovBootApplication.java` 파일 찾기
3. 메인 메서드 실행

#### 방법 3: JAR 빌드 후 실행
```bash
# JAR 파일 생성
./gradlew clean build

# JAR 파일 실행
java -jar build/libs/aramiV3-1.0.0.jar
```

### 애플리케이션 접속

- **메인 애플리케이션**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 📚 API 문서

### Swagger UI 접속

애플리케이션 실행 후 브라우저에서 다음 URL로 접속:
```
http://localhost:8080/swagger-ui.html
```

### 주요 API 엔드포인트

#### 인증 API

**로그인 (JWT)**
```http
POST /auth/login-jwt
Content-Type: application/json

{
  "id": "user_id",
  "password": "user_password"
}
```

**응답**
```json
{
  "resultCode": "200",
  "resultMessage": "성공 !!!",
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "resultVO": {
    "id": "user_id",
    "name": "사용자명",
    "userSe": "USR",
    "groupNm": "ROLE_USER"
  }
}
```

**로그아웃**
```http
GET /auth/logout
Authorization: {JWT_TOKEN}
```

#### 회원 API

**회원 목록 조회**
```http
GET /api/memberList
Authorization: {JWT_TOKEN}
```

**응답**
```json
{
  "resultCode": "200",
  "resultMessage": "정상적으로 처리되었습니다.",
  "userId": "user_id",
  "userName": "사용자명"
}
```

## 🔒 보안 설정

### JWT 인증 방식

프론트엔드에서 API 호출 시 다음 두 가지 방식 모두 지원:

#### 방식 1: 토큰만 전송
```javascript
fetch('http://localhost:8080/api/memberList', {
  method: 'GET',
  headers: {
    'Authorization': token,
    'Content-Type': 'application/json'
  }
});
```

#### 방식 2: Bearer 포함 (표준)
```javascript
fetch('http://localhost:8080/api/memberList', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});
```

### Axios 인터셉터 설정 예제

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

// 요청 인터셉터 - 자동으로 토큰 추가
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers['Authorization'] = token;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터 - 401/403 에러 처리
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem('accessToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### 권한 레벨

- **ROLE_ADMIN**: 관리자 권한
  - `/admin/**` 접근 가능
  - `/members/**` 접근 가능

- **ROLE_USER**: 일반 사용자 권한
  - `/mypage/**` 접근 가능
  - `/inform/**` 접근 가능
  - `/api/**` 접근 가능

### CORS 설정

현재 허용된 Origin:
- `http://localhost:3000` (프론트엔드 개발 서버)

추가 Origin 허용 방법:
```java
// SecurityConfig.java
private static final String[] ORIGINS_WHITELIST = {
    "http://localhost:3000",
    "https://your-domain.com"  // 추가
};
```

## 💻 개발 가이드

### 로컬 개발 환경 설정

1. **프로젝트 클론**
```bash
git clone [repository-url]
cd aramiv3
```

2. **환경변수 설정**
```properties
# application.properties 파일 수정
spring.profiles.active=dev
```

3. **의존성 설치**
```bash
./gradlew build
```

4. **개발 서버 실행**
```bash
./gradlew bootRun
```

### 프론트엔드 연동

프론트엔드 개발 시 프록시 설정 예제 (React):

```javascript
// package.json
{
  "proxy": "http://localhost:8080"
}
```

또는 setupProxy.js:
```javascript
const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(
    '/api',
    createProxyMiddleware({
      target: 'http://localhost:8080',
      changeOrigin: true,
    })
  );
};
```

### 코드 스타일

- **Lombok 사용**: Getter/Setter 자동 생성
- **로깅**: `@Slf4j` 어노테이션 사용
- **REST API**: RESTful 설계 원칙 준수

### 빌드 및 배포

#### 개발 환경 빌드
```bash
./gradlew clean build
```

#### 프로덕션 빌드
```bash
./gradlew clean build -Pprofile=prod
```

#### Docker 배포 (선택사항)
```dockerfile
FROM openjdk:25-jdk-slim
COPY build/libs/aramiV3-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080
```

## 🔧 트러블슈팅

### 자주 발생하는 문제

#### 1. 401 Unauthorized 에러
**원인**: JWT 토큰이 전송되지 않았거나 만료됨

**해결방법**:
- 로그인하여 새 토큰 받기
- Authorization 헤더에 토큰이 포함되었는지 확인
- 토큰 유효기간 확인 (현재 24시간)

#### 2. 403 Forbidden 에러
**원인**: 권한이 부족함

**해결방법**:
- 사용자의 Role 확인 (ROLE_ADMIN, ROLE_USER)
- 해당 엔드포인트에 필요한 권한 확인

#### 3. "Compact JWT strings may not contain whitespace" 에러
**원인**: 토큰에 공백이 포함됨

**해결방법**:
```javascript
// 토큰 저장 시 trim() 사용
const token = response.data.accessToken.trim();
localStorage.setItem('accessToken', token);
```

#### 4. CORS 에러
**원인**: 허용되지 않은 Origin에서 요청

**해결방법**:
- `application.properties`에서 `Globals.Allow.Origin` 확인
- `SecurityConfig.java`의 `ORIGINS_WHITELIST`에 Origin 추가

#### 5. Database Connection 에러
**원인**: 데이터베이스 연결 실패

**해결방법**:
- MySQL 서버 실행 상태 확인
- `application.properties`의 DB 정보 확인
- 방화벽 설정 확인

### 로그 확인

로그 파일 위치:
```
./logs/arami-backend.log
```

디버그 로그 활성화:
```properties
# application.properties
logging.root.level=DEBUG
```

## 📝 라이선스

이 프로젝트는 전자정부 표준프레임워크 라이선스를 따릅니다.

## 👥 개발팀

- **Organization**: UARAM (co.kr.uaram)
- **Project**: ARAMI v3

## 📞 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.

---

**Last Updated**: 2025-12-24
