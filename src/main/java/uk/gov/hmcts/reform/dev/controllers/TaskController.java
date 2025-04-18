package uk.gov.hmcts.reform.dev.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequestDTO;
import uk.gov.hmcts.reform.dev.dto.TaskResponseDTO;
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
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Long> createTask(String title, String description, String status, LocalDateTime dueDateTime) {

        CreateTaskRequestDTO createRequest = CreateTaskRequestDTO.builder()
            .title(title)
            .description(description)
            .status(status)
            .dueDateTime(dueDateTime).build();

        TaskResponseDTO response = taskService.createTask(createRequest);

        return ResponseEntity.ok(response.id);
    }

    @PatchMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> updateTaskStatus(@PathVariable long id, String status) {

        TaskResponseDTO response;

        try {
            response = taskService.updateTaskStatus(id, status);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response.getStatus());
    }

    @GetMapping("/retrieveTask/{id}")
    public ResponseEntity<TaskResponseDTO> retrieveTaskById(@PathVariable long id) {

        TaskResponseDTO response;

        try {
            response = taskService.retrieveTask(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/retrieveAllTasks")
    public ResponseEntity<List<TaskResponseDTO>> retrieveAllTasks() {

        List<TaskResponseDTO> response = taskService.retrieveAllTasks();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Long> deleteTask(@PathVariable long id) {

        try {
            taskService.deleteTask(id);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(id);
    }
}
