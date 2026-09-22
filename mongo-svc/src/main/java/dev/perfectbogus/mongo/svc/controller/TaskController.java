package dev.perfectbogus.mongo.svc.controller;

import dev.perfectbogus.mongo.svc.entity.Task;
import dev.perfectbogus.mongo.svc.repository.TaskRepository;
import dev.perfectbogus.mongo.svc.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {
    private final TaskService service;

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return service.save(task);
    }

    @GetMapping
    public List<Task> getAllTasks(@RequestParam(required = false) String status) {
        if (status != null) {
            return service.findByStatus(status);
        }
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable String id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}/status")
    public Task updateStatus(@PathVariable String id, @RequestParam String status) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        service.deleteTask(id);
    }
}
