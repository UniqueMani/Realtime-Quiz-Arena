# Realtime Quiz Arena (Demo)
A demo "real-time quiz arena" full-stack project:
- Frontend: Vue 3 (Vite)
- Backend: Spring Boot 3 (REST + WebSocket/STOMP + OpenAPI)
- Infra: Docker Compose (Postgres + RabbitMQ + backend + frontend)

## Quick start (Docker)
Prereqs: Docker + Docker Compose

```bash
cd realtime-quiz-arena
docker compose up -d --build
```

Open:
- Frontend: http://localhost:5173
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Backend health: http://localhost:8080/api/health

## Local dev (without Docker)
### Backend
Prereqs: Java 17+, Maven 3.9+
```bash
cd backend
./mvnw spring-boot:run
```
Backend uses H2 in-memory DB by default.

### Frontend
Prereqs: Node 18+
```bash
cd frontend
npm install
npm run dev
```

## Demo flow
1. Open frontend.
2. Click "Create Room" as Host. Copy room code.
3. In another browser/window, join with the room code and a nickname.
4. Host clicks "Start Game" to open a question.
5. Players choose an option and submit.
6. Leaderboard updates in real-time.

## Notes
- This is a demo scaffold designed for coursework. Extend it with:
  - Persisting rooms/sessions
  - Multiple questions & quiz selection
  - Auth (JWT) + RBAC
  - MQ-driven scoring/analytics (RabbitMQ queues already included)
  - Comprehensive tests (unit/mock/integration + Testcontainers)

## If Docker build fails with npm/maven network errors (ECONNRESET / ETIMEDOUT)
This usually means Docker build cannot reach the npm/maven registries due to ISP/campus restrictions or proxy requirements.

### Option A (recommended): pass your proxy to Docker build
In PowerShell (example: Clash on Windows):
```powershell
$env:HTTP_PROXY="http://host.docker.internal:7890"
$env:HTTPS_PROXY="http://host.docker.internal:7890"
$env:NO_PROXY="localhost,127.0.0.1,db,rabbitmq,backend,frontend"
$env:NPM_REGISTRY="https://registry.npmmirror.com"
docker compose build --no-cache
docker compose up -d
```
Make sure your proxy software allows LAN access.

### Option B: build frontend locally, run only backend+db with Docker
```powershell
docker compose up -d db rabbitmq backend
cd frontend
npm install
npm run dev
```
