package uk.gov.hmcts.reform.dev.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.excpetions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class TaskController {

    TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid CreateTaskRequest createTaskRequest) {

        TaskResponse response = taskService.createTask(createTaskRequest);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable long id, String status) {

        TaskResponse response;

        try {
            response = taskService.updateTaskStatus(id, status);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/retrieveTask/{id}")
    public ResponseEntity<TaskResponse> retrieveTaskById(@PathVariable long id) {

        TaskResponse response;

        try {
            response = taskService.retrieveTask(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/retrieveAllTasks")
    public ResponseEntity<List<TaskResponse>> retrieveAllTasks() {

        List<TaskResponse> response = taskService.retrieveAllTasks();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> deleteTask(@PathVariable long id) {

        try {
            taskService.deleteTask(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(id);
    }
}
