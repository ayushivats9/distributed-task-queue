package com.taskqueue.taskqueue;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TaskType type;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private int retryCount;
    private static final int MAX_RETRIES = 3;
    private long executeAt;

    private long createdAt;

    // Enums
    public enum TaskType {
        SEND_OTP, PROCESS_PAYMENT, SEND_EMAIL,
        UPDATE_INVENTORY, NOTIFY_USER
    }

    public enum TaskPriority {
        URGENT, NORMAL, LOW
    }

    public enum TaskStatus {
        PENDING, RUNNING, COMPLETED, FAILED, RETRYING
    }

    // Constructor
    public Task() {}

    public Task(String name, TaskType type, TaskPriority priority) {
        this.name = name;
        this.type = type;
        this.priority = priority;
        this.status = TaskStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = System.currentTimeMillis();
        this.executeAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public TaskType getType() { return type; }
    public void setType(TaskType type) { this.type = type; }
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getExecuteAt() { return executeAt; }
    public void setExecuteAt(long executeAt) { this.executeAt = executeAt; }

    @Override
    public String toString() {
        return "Task{id='" + id + "', name='" + name + 
               "', priority=" + priority + ", status=" + status + "}";
    }
}