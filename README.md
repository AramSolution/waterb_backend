# waterb_backend

김제시 하수도 관리 시스템 백엔드 API 서버입니다. 전자정부 표준 프레임워크(Spring Boot 3) 기반이며, 관리자 웹(`waterb_frontend`)과 JWT 인증으로 연동합니다.

## 기술 스택

- Java 25, Gradle, Spring Boot 3.5
- Spring Security + JWT
- MyBatis, MySQL
- Swagger (SpringDoc OpenAPI)

## 실행

```bash
# Windows
gradlew.bat bootRun

# 빌드 검증
gradlew.bat compileJava
```

- Context path: `/water` (`application.properties`)
- Swagger UI: `{baseUrl}/water/swagger-ui.html`
- API Docs: `{baseUrl}/water/v3/api-docs`

## 주요 API (현행)

| 구분 | Base path | 설명 |
|------|-----------|------|
| 인증 | `POST /auth/login-jwt`, `GET /auth/logout` | 관리자 JWT 로그인·로그아웃 |
| 관리자 회원 | `/api/admin/member` | 관리자 회원 CRUD |
| 오수 원인자부담금 | `/api/admin/support/fee-payer` | 부과·수납 관리 |
| 배수설비 | `/api/admin/support/drainage-equip` | 배수설비 관리 |
| 건축물용도 | `/api/admin/armbuild` | 건축물용도(ARMBULD) |
| 공통코드 | `/api/cont/code` | 코드·상세코드 |
| 파일 | `/api/v1/files` | 업로드·다운로드 |
| 본인인증 | `/api/cert/siren` | Siren 본인인증 (내부 연동) |
| 배너 | `/api/admin/banner` | 관리자 배너 (선택) |
| 대시보드 | `/api/admin/dashboard` | 대시보드 (선택) |

레거시 ieum/gunsan 모듈(userWeb, NEIS, GPKI, 지원사업 art*, OAuth 소셜 로그인 등)은 제거되었습니다.

## 인증 예시

```http
POST /water/auth/login-jwt
Content-Type: application/json

{
  "id": "admin_id",
  "password": "password"
}
```

응답의 `accessToken`을 이후 요청의 `Authorization` 헤더에 포함합니다 (`Bearer` 접두사 선택).

## 설정

| 항목 | 파일 |
|------|------|
| 프로파일 | `application.properties` → `spring.profiles.active` |
| DB·도메인·CORS | `application-dev.properties`, `application-prod.properties` |
| JWT Secret | 프로파일별 `Globals.jwt.secret` (운영 시 반드시 변경) |

DB 스키마: `water` (MySQL)

## 프로젝트 구조 (요약)

```
src/main/java/
├── arami/
│   ├── adminWeb/          # 하수도 업무 API (support, armbuild, banner, …)
│   ├── common/            # 인증, 파일, 공통코드, 관리자 회원, 본인인증
│   └── shared/armuser/    # ARMUSER 공통 (본인인증 DI 중복 체크 등 내부용)
└── egovframework/         # eGov 프레임워크·Security·JWT
```

## 프론트엔드

관리자 UI: [waterb_frontend](../waterb_frontend) — Next.js 14, `/adminWeb/*`

---

Last updated: 2026-03-02
