# 프로젝트 구조

아래는 현재 레포지토리의 디렉터리 구조와 주요 역할을 요약한 문서이다.

```text
/
├─ app/
│  ├─ backend/
│  │  ├─ build.gradle                 # Gradle 빌드 설정
│  │  ├─ gradlew / gradlew.bat         # Gradle 실행 스크립트
│  │  └─ src/
│  │     └─ main/
│  │        ├─ java/
│  │        │  └─ com/example/backend/
│  │        │     ├─ auth/             # 인증/인가 API
│  │        │     │  ├─ dto/           # 요청/응답 DTO
│  │        │     │  ├─ AuthController.java
│  │        │     │  └─ AuthService.java
│  │        │     ├─ config/           # 보안/JWT 설정
│  │        │     ├─ data/             # 시드 데이터 초기화
│  │        │     ├─ post/             # 게시글 도메인
│  │        │     │  ├─ dto/           # 게시글 DTO
│  │        │     │  ├─ PostController.java
│  │        │     │  ├─ PostService.java
│  │        │     │  └─ PostRepository.java
│  │        │     ├─ security/         # JWT 필터/토큰 처리
│  │        │     ├─ user/             # 사용자 도메인
│  │        │     └─ BackendApplication.java
│  │        └─ resources/              # 설정 파일/리소스
│  │
│  ├─ frontend/
│  │  ├─ package.json                  # 프론트 의존성/스크립트
│  │  ├─ index.html
│  │  └─ src/
│  │     ├─ App.jsx
│  │     ├─ App.css
│  │     ├─ index.css
│  │     └─ main.jsx
│  │
│  └─ docker-compose.yml               # 로컬 통합 실행
│
├─ docs/
│  ├─ feature/                         # 기능 단위 문서
│  ├─ spec.md                          # 요구사항
│  ├─ plan.md                          # 작업 계획
│  ├─ progress.md                      # 진행 현황
│  ├─ next.md                          # 다음 작업
│  ├─ decisions.md                     # 기술 결정 기록
│  ├─ troubleshooting.md               # 이슈/해결 기록
│  └─ structure.md                     # 구조 문서
│
├─ AGENTS.md
└─ README.md
```

## 주요 디렉터리/파일 설명

- `app/`: 실제 애플리케이션 소스 루트
- `app/backend/`: 백엔드(Spring Boot) 프로젝트
- `app/frontend/`: 프론트엔드(Vite/React) 프로젝트
- `docs/`: 문서 모음
- `docs/feature/`: 기능 단위 상세 문서
- `docs/spec.md`: 현재 요구사항
- `docs/plan.md`: 작업 계획
- `docs/progress.md`: 진행 현황
- `docs/next.md`: 다음 작업
- `docs/decisions.md`: 기술적 결정 기록
- `docs/troubleshooting.md`: 이슈 및 해결 기록
- `AGENTS.md`: 에이전트 작업 규칙
- `README.md`: 프로젝트 개요 및 실행 가이드

## 백엔드 상세

- `app/backend/build.gradle`: Gradle 빌드 설정
- `app/backend/src/main/java/com/example/backend/`: 백엔드 패키지 루트
- `app/backend/src/main/java/com/example/backend/BackendApplication.java`: Spring Boot 진입점
- `app/backend/src/main/java/com/example/backend/auth/`: 인증/인가 API 및 서비스
- `app/backend/src/main/java/com/example/backend/auth/dto/`: 로그인/회원가입/토큰 요청·응답 DTO
- `app/backend/src/main/java/com/example/backend/config/`: 보안 및 JWT 설정
- `app/backend/src/main/java/com/example/backend/data/`: 시드 데이터 초기화
- `app/backend/src/main/java/com/example/backend/post/`: 게시글 도메인(엔티티, 서비스, API, 리포지토리)
- `app/backend/src/main/java/com/example/backend/post/dto/`: 게시글 요청·응답 DTO
- `app/backend/src/main/java/com/example/backend/security/`: JWT 필터 및 토큰 처리
- `app/backend/src/main/java/com/example/backend/user/`: 사용자/권한 엔티티 및 리포지토리
- `app/backend/src/main/resources/`: 설정 파일 및 리소스

## 프론트엔드 상세

- `app/frontend/package.json`: 프론트엔드 의존성 및 스크립트
- `app/frontend/index.html`: Vite 엔트리 HTML
- `app/frontend/src/`: 프론트엔드 소스 루트
- `app/frontend/src/main.jsx`: 앱 부트스트랩
- `app/frontend/src/App.jsx`: 주요 화면/라우팅
- `app/frontend/src/App.css`: 앱 전역 스타일
- `app/frontend/src/index.css`: 기본 스타일 리셋 및 공통 스타일
