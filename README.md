# codex_board
- Desc: Codex를 이용한 게시판 만들기 Test

## 실행 방법

### 0) 사전 준비
- Node.js LTS 설치
- Java 21 설치
- Docker Desktop 설치 (PostgreSQL/Redis 사용 시)

### 1) 인프라 실행 (PostgreSQL/Redis)
```powershell
cd app
docker compose up -d
```

### 2) 백엔드 실행
```powershell
cd app/backend
.\gradlew.bat bootRun
```

### 3) 프론트 실행
```powershell
cd app/frontend
npm install
npm run dev
```

### 4) 접속
- 프론트: http://localhost:5173
- 백엔드: http://localhost:8080

### 5) 환경설정
- DB/Redis/JWT 설정: app/backend/src/main/resources/application.yml
- 프론트 API 주소: VITE_API_BASE (기본값 http://localhost:8080)
```powershell
$env:VITE_API_BASE="http://localhost:8080"
npm run dev
```

### 6) 로컬 DB 직접 구성 시
- PostgreSQL: DB `board`, USER `board`, PASSWORD `board`, PORT `5432`
- Redis: PORT `6379`

### 7) 트러블슈팅
- docs/troubleshooting.md 참고

## Commit Guide

### Commit Message Rule
|타입|설명|
|---|---|
|Feat|새로운 기능 추가|
|Fix|버그 수정|
|Design|CSS 등 UI 디자인 변경|
|Style|코드 포맷/세미콜론 등 기능 변경 없는 스타일 수정|
|Refactor|코드 리팩토링|
|Comment|필요 주석 추가 및 변경|
|Docs|문서 수정|
|Rename|파일/폴더명 변경 혹은 경로 수정|
|Remove|파일 삭제|
|Build|빌드/버전/의존성 관련 변경|
|Etc|기타 변경|

### Commit Message Structure

#### <제목>

[타입] 제목
- 필수 입력
- 50자 제한
- 명령형, 현재 시제 사용
- 무엇을/왜 했는지 요약

#### <본문>

1. 본문 내용
- 선택 사항
- 70자 제한
- 명령형, 현재 시제 사용

**예시**
<<제목>>
[Feat] 사용자 회원가입 기능 구현

<<본문>>
1. 이메일/비밀번호 입력 추가
2. 회원가입 완료 시 메일 발송

**주의사항**
- 여러 커밋에 걸릴 변경을 한 커밋에 몰지 말 것
- 예) [Feat] 회원가입 기능, 이메일 인증 기능 구현 (X)