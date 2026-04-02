package com.taskqueue.service;

import com.taskqueue.taskqueue.Task;
import com.taskqueue.repository.TaskRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class TaskService {

    private final TaskRepository taskRepository;

    // Thread pool (3 workers)
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    // Priority Queue
    private final PriorityBlockingQueue<Task> taskQueue =
            new PriorityBlockingQueue<>(10, (t1, t2) -> {

                // Step 1: compare by execution time
                int timeCompare = Long.compare(t1.getExecuteAt(), t2.getExecuteAt());

                if (timeCompare != 0) {
                    return timeCompare;
                }

                // Step 2: if same time → compare priority
                return t1.getPriority().ordinal() - t2.getPriority().ordinal();
            });
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Add task
    public Task createTask(Task task) {
        task.setCreatedAt(System.currentTimeMillis());
        task.setExecuteAt(System.currentTimeMillis());
        task.setStatus(Task.TaskStatus.PENDING);
        task.setRetryCount(0);

        Task savedTask = taskRepository.save(task);
        taskQueue.add(savedTask);

        return savedTask;
    }
    public Task getTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    // Start multiple workers
    @PostConstruct
    public void startWorkers() {
        for (int i = 0; i < 3; i++) {
            executorService.submit(() -> {
                while (true) {
                    try {

                        // 👇 THIS PART (ADD HERE)
                        Task task = taskQueue.take();

                        long now = System.currentTimeMillis();

                        if (task.getExecuteAt() > now) {
                            taskQueue.add(task);
                            Thread.sleep(500);
                            continue;
                        }

                        processTask(task);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // Processing logic
    private void processTask(Task task) {
        try {
            task.setStatus(Task.TaskStatus.RUNNING);
            taskRepository.save(task);

            System.out.println(Thread.currentThread().getName() +
                    " Processing task: " + task);

            // Simulate failure randomly (for testing)
            if (Math.random() < 0.5) {
                throw new RuntimeException("Simulated failure");
            }

            Thread.sleep(2000);

            task.setStatus(Task.TaskStatus.COMPLETED);
            taskRepository.save(task);

        } catch (Exception e) {

        	int retries = task.getRetryCount();

        	if (retries < 3) {
        	    task.setRetryCount(retries + 1);
        	    task.setStatus(Task.TaskStatus.RETRYING);

        	    // 🔥 Exponential backoff
        	    long delay = (long) Math.pow(2, retries) * 1000;

        	    task.setExecuteAt(System.currentTimeMillis() + delay);

        	    taskRepository.save(task);

        	    System.out.println("Retrying after " + delay + " ms: " + task);

        	    taskQueue.add(task);

        	} else {
        	    task.setStatus(Task.TaskStatus.FAILED);
        	    taskRepository.save(task);

        	    System.out.println("Task failed permanently: " + task);
        	}
    }}}