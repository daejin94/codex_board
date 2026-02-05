# 로그인 흐름

이 문서는 백엔드와 프론트엔드에 적용된 로그인 흐름을 설명합니다.

## 구성 요소
- 클라이언트: `http://localhost:5173`의 React 프론트엔드
- 백엔드 API: `http://localhost:8080`의 Spring Boot 서비스
- Redis: 리프레시 토큰 세션 저장소
- Postgres: `user`, `user_grant` 테이블 저장소

## 계정 정보
데이터베이스가 비어 있으면 데모 사용자가 자동으로 생성됩니다.
- 이메일: `user@example.com`
- 비밀번호: `password123`

## 엔드포인트
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`

## 흐름
1. 클라이언트가 `POST /api/auth/login`에 이메일과 비밀번호를 전송합니다.
2. 백엔드가 비밀번호를 검증하고 `user_grant`에서 권한을 조회합니다.
3. 백엔드가 다음 토큰을 발급합니다.
   - 액세스 토큰 (단기, 15분)
   - 리프레시 토큰 (장기, 7일)
4. 리프레시 토큰의 JTI가 Redis에 저장되며 TTL은 토큰 만료와 동일하게 설정됩니다.
5. 클라이언트는 두 토큰을 저장합니다 (데모 UI는 localStorage 사용).
6. 보호된 API 호출 시 `Authorization: Bearer <accessToken>` 헤더를 전송합니다.
7. 액세스 토큰이 만료되면 `POST /api/auth/refresh`에 리프레시 토큰을 전송합니다.
8. 백엔드는 리프레시 토큰과 Redis를 확인하고 토큰을 로테이션한 뒤 새 토큰을 반환합니다.
9. 로그아웃 시 `POST /api/auth/logout`에 리프레시 토큰을 전송하여 Redis에서 세션을 제거합니다.

## 토큰 클레임
- `sub`: 사용자 ID
- `email`: 사용자 이메일
- `auth`: 권한 목록(콤마 구분, 액세스 토큰 전용)
- `typ`: `access` 또는 `refresh`
- `jti`: 토큰별 고유 ID

## 세션 무효화
리프레시 토큰은 JTI 기준으로 Redis에 저장됩니다. 해당 JTI를 삭제하면 세션이 즉시 무효화됩니다.
