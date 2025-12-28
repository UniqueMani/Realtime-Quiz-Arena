
# English

# Realtime Quiz Arena

**Realtime Quiz Arena** is a full-stack, real-time interactive quiz platform designed for high-concurrency environments. It supports both **Multiplayer Hosted Rooms** (where a host controls the game flow) and a **Single-Player Speed Mode** (for rapid-fire solo challenges).

Built with **Spring Boot** and **Vue 3**, the system emphasizes engineering excellence through containerization, comprehensive testing, system observability, and standardized API documentation.

## Key Features

### 1.  Game Modes

* **Host Mode (Multiplayer):**
* Users can create a room as a **Host** and invite others to join via a Room Code.
* Host controls the flow (Start Game, Next Question, Show Leaderboard).
* Real-time scoreboard updates for all connected players.


* **Speed Mode (Solo Challenge) [New!]:**
* A fast-paced single-player mode.
* 10 questions per session, 15-second countdown per question.
* Auto-submission on timeout and seamless question transitions.
* Detailed performance report upon completion.



### 2. Real-time Interaction

* Powered by **WebSockets (STOMP)** to ensure low-latency communication between the server and clients.
* Instant feedback on answers and live leaderboard synchronization.

### 3. Engineering Highlights (Course Requirements)

This project demonstrates several advanced software engineering practices:

* **Containerization:** Fully Dockerized application (Frontend, Backend, Database, Monitoring) managed via `docker-compose`.
* **Observability:** Integrated **Prometheus** and **Grafana** for real-time system monitoring (CPU, Memory, JVM, Active Sessions).
* **API Documentation:** Comprehensive REST API documentation auto-generated using **Swagger / OpenAPI**.
* **Testing:** Robust Unit and Integration testing using **JUnit 5** and **Mockito**.

## Tech Stack

**Backend:**

* **Java 17** + **Spring Boot 3**
* **Spring Data JPA** + **MySQL**
* **Spring WebSocket**
* **Spring Boot Actuator** (Monitoring)

**Frontend:**

* **Vue.js 3** + **TypeScript**
* **Vite** + **Tailwind CSS**
* **StompJS** (WebSocket Client)

**DevOps & Infrastructure:**

* **Docker** & **Docker Compose**
* **Prometheus** (Metrics Collection)
* **Grafana** (Visualization Dashboard)

## Quick Start (Docker)

The easiest way to run the application is using Docker Compose.

### Prerequisites

* Docker & Docker Compose installed.
* Java JDK 17+ & Maven (for building the JAR).

### Steps

1. **Build the Backend JAR:**
```bash
cd backend
./mvnw clean package -DskipTests

```


2. **Start Services:**
   Return to the root directory and run:
```bash
cd ..
docker-compose up -d --build

```


3. **Access the Application:**
* **Frontend (Game UI):** [http://localhost:5173](https://www.google.com/search?q=http://localhost:5173) (or configured port)
* **Backend API:** [http://localhost:8080](https://www.google.com/search?q=http://localhost:8080)



## Documentation & Monitoring

Once the services are running, you can access the following tools:

* **Swagger API Docs:**
  [http://localhost:8080/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui/index.html)
  *Test API endpoints and view data models.*
* **Grafana Dashboard:**
  [http://localhost:3000](https://www.google.com/search?q=http://localhost:3000)
  *(Default credentials: admin / admin)*
  *View system health, CPU usage, and business metrics.*
* **Prometheus Targets:**
  [http://localhost:9090/targets](https://www.google.com/search?q=http://localhost:9090/targets)

## Project Structure

```bash
realtime-quiz-arena/
├── backend/            # Spring Boot Application
│   ├── src/main/java   # Source code (Controllers, Services, WebSocket)
│   ├── src/test/java   # Unit Tests (JUnit/Mockito)
│   └── Dockerfile      # Backend container config
├── frontend/           # Vue 3 Application
│   ├── src/views       # Game UI (Home, Host, Player, SpeedGame)
│   └── Dockerfile      # Frontend container config
├── docker-compose.yml  # Orchestration for App, DB, and Monitoring
└── prometheus.yml      # Monitoring configuration

```

---

# 中文版 (Chinese)

# Realtime Quiz Arena (实时问答竞技场)

**Realtime Quiz Arena** 是一个基于现代 Web 技术栈构建的实时互动问答平台。系统专为高并发场景设计，支持**多人在线竞技**（由主持人控场）以及**单人快问快打**（Speed Mode）两种模式。

该项目采用了 **Spring Boot** 和 **Vue 3** 前后端分离架构，并重点展示了包括**容器化部署**、**自动化测试**、**系统可观测性**以及**规范化 API 文档**在内的多项工程化亮点。

## 核心功能

### 1. 🎮 游戏模式

* **主持人模式 (多人竞技):**
* 用户创建房间成为**主持人 (Host)**，生成房间码邀请好友加入。
* 主持人控制游戏进度（开始游戏、切换下一题、公布榜单）。
* 所有玩家端实时同步题目与分数排行榜。


* **快问快打模式 (单人挑战) [新增!]:**
* 无需等待，单人即可开始的极速挑战模式。
* 包含 10 道随机题目，每题 15 秒倒计时。
* 支持超时自动提交与无缝切题，挑战结束后生成详细战报。



### 2.实时互动

* 基于 **WebSockets (STOMP)** 协议，实现毫秒级的服务端消息推送。
* 保证所有客户端的状态（倒计时、分数、排名）严格同步。

### 3.工程亮点 (Engineering Highlights)

本项目严格遵循企业级开发规范，包含以下亮点：

* **容器化 (Dockerization):** 前端、后端、数据库及监控组件全链路 Docker 化，通过 `docker-compose` 一键编排。
* **可观测性 (Observability):** 集成 **Prometheus** 与 **Grafana**，实时监控 JVM 状态、CPU/内存使用率及业务指标。
* **API 文档:** 使用 **Swagger / OpenAPI 3** 自动生成交互式接口文档。
* **软件测试:** 包含完整的 **JUnit 5** 单元测试与 **Mockito** 模拟测试，确保核心业务逻辑的健壮性。

## 技术栈

**后端 (Backend):**

* Java 17, Spring Boot 3
* Spring Data JPA, MySQL
* Spring WebSocket
* Spring Boot Actuator (监控)

**前端 (Frontend):**

* Vue.js 3, TypeScript
* Vite, Tailwind CSS
* StompJS (WebSocket 客户端)

**运维与基础设施:**

* Docker, Docker Compose
* Prometheus (指标采集)
* Grafana (可视化看板)

##快速开始

推荐使用 Docker Compose 运行本项目。

### 前置条件

* 已安装 Docker 和 Docker Compose。
* 本地环境有 Java JDK 17+ 和 Maven (用于打包后端)。

### 启动步骤

1. **打包后端代码:**
```bash
cd backend
./mvnw clean package -DskipTests

```


2. **启动服务:**
   回到项目根目录执行：
```bash
cd ..
docker-compose up -d --build

```


3. **访问应用:**
* **游戏前端:** [http://localhost:5173](https://www.google.com/search?q=http://localhost:5173) (具体端口视配置而定)
* **后端接口:** [http://localhost:8080](https://www.google.com/search?q=http://localhost:8080)



## 文档与监控平台

服务启动后，您可以通过以下地址访问配套工具：

* **Swagger API 文档:**
  [http://localhost:8080/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui/index.html)
  *用于查看和测试 RESTful 接口。*
* **Grafana 监控看板:**
  [http://localhost:3000](https://www.google.com/search?q=http://localhost:3000)
  *(默认账号/密码: admin / admin)*
  *查看系统性能图表。*
* **Prometheus 指标:**
  [http://localhost:9090/targets](https://www.google.com/search?q=http://localhost:9090/targets)

## 项目结构

```bash
realtime-quiz-arena/
├── backend/            # Spring Boot 后端项目
│   ├── src/main/java   # 核心代码 (Controllers, Services, WebSocket)
│   ├── src/test/java   # 单元测试代码
│   └── Dockerfile      # 后端镜像配置
├── frontend/           # Vue 3 前端项目
│   ├── src/views       # 页面组件 (Home, Host, Player, SpeedGame)
│   └── Dockerfile      # 前端镜像配置
├── docker-compose.yml  # 容器编排文件
└── prometheus.yml      # 监控配置文件

```