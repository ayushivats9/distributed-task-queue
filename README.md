# ⚡ Distributed Task Queue System

A production-grade async task processing system built from scratch — similar to how Swiggy, Razorpay, and Zepto handle background jobs in production.

## 🚀 What it does

- Accepts tasks via REST API
- Queues them by priority (URGENT → NORMAL → LOW)
- Processes them asynchronously using worker threads
- Retries failed tasks with exponential backoff
- Live monitoring dashboard with real-time updates

## 🏗️ Architecture
```
REST API → PriorityBlockingQueue → Worker Threads (x3)
                                        ↓
                              Task Status Updates
                              (PENDING → RUNNING → COMPLETED/FAILED/RETRYING)
```

## ⚙️ Tech Stack

- **Backend:** Java 21, Spring Boot 3.5
- **Queue:** PriorityBlockingQueue (thread-safe)
- **Database:** PostgreSQL / H2
- **Frontend:** Vanilla JS dashboard with real-time polling
- **Concurrency:** ExecutorService with 3 worker threads

## 🔥 Key Features

|        Feature      | Implementation |

| Priority Processing | PriorityBlockingQueue — URGENT tasks first |
| Async Workers | ExecutorService with fixed thread pool |
| Retry Logic | Exponential backoff (2^n seconds) |
| Max Retries | 3 attempts before marking FAILED |
| Live Dashboard | Auto-refreshes every 2 seconds |
| REST APIs | Full CRUD — create, fetch, list tasks |

## 📡 API Endpoints
```
POST /tasks          — Submit a new task
GET  /tasks          — Get all tasks
GET  /tasks/{id}     — Get task by ID
GET  /tasks/test     — Health check
```

## 🧠 Why I built this

Every major tech company runs systems like this internally:
- Swiggy — order processing pipeline
- Razorpay — payment retry queue  
- Zomato — notification service
- Amazon — SQS

This project replicates that core infrastructure from scratch.

## 🏃 How to run
```bash
git clone https://github.com/ayushivats9/distributed-task-queue.git
cd distributed-task-queue
./mvnw spring-boot:run
```

Open `http://localhost:8082` for the dashboard.
<img width="1727" height="897" alt="image" src="https://github.com/user-attachments/assets/03bbdb2d-98b7-4ab6-ac53-cc3d9fab0037" />




