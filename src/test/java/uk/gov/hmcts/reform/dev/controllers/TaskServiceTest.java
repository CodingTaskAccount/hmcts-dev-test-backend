package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchTaskExecutor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.excpetions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskRepository;
import uk.gov.hmcts.reform.dev.services.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class TaskServiceTest {

    @Autowired
    private TaskService underTest;

    @MockitoBean
    private TaskRepository taskRepository;

    private long id;
    private long caseNumber;
    private String title;
    private String description;
    private String status;
    private LocalDate createdDate;
    private LocalDateTime dueDateTime;

    private TaskResponse testResponse;

    @BeforeEach
    void setUp() {
        id = 1;
        caseNumber = 100;
        title = "test title";
        description = "test description";
        status = "test status";
        createdDate = LocalDate.now();
        dueDateTime = LocalDateTime.now();

        testResponse = TaskResponse.builder()
            .id(id)
            .caseNumber(caseNumber)
            .title(title)
            .description(description)
            .status(status)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();
    }

    @Test
    void createTask_shouldReturnValidResponse() throws Exception {

        CreateTaskRequest createTaskRequest = CreateTaskRequest.builder()
            .title(title)
            .caseNumber(caseNumber)
            .description(description)
            .status(status)
            .dueDateTime(dueDateTime)
            .build();

        Task createdTask = Task.builder()
            .id(id)
            .caseNumber(caseNumber)
            .title(createTaskRequest.getTitle())
            .description(createTaskRequest.getDescription())
            .status(createTaskRequest.getStatus())
            .createdDate(createdDate)
            .dueDateTime(createTaskRequest.getDueDateTime())
            .build();

        when(taskRepository.save(argThat(
                task -> task.getCaseNumber() == caseNumber &&
                    task.getTitle().equals(title) &&
                task.getDescription().equals(description) &&
                task.getStatus() == status &&
                    task.getCreatedDate().equals(createdDate) &&
                task.getDueDateTime().equals(dueDateTime)
        ))).thenReturn(createdTask);

        TaskResponse response = underTest.createTask(createTaskRequest);

        assertEquals(testResponse, response);
        verify(taskRepository).save(argThat(
            task -> task.getCaseNumber() == caseNumber &&
                task.getTitle().equals(title) &&
            task.getDescription().equals(description) &&
            task.getStatus() == status &&
                task.getCreatedDate().equals(createdDate) &&
            task.getDueDateTime().equals(dueDateTime)
        ));
    }

    @Test
    void retrieveTask_shouldReturnValidResponse() throws Exception {

        Task task = Task.builder()
            .id(id)
            .caseNumber(caseNumber)
            .title(title)
            .description(description)
            .status(status)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        TaskResponse response = underTest.retrieveTask(id);
        assertEquals(testResponse, response);
        verify(taskRepository).findById(id);
    }

    @Test
    void updateTask_shouldReturnValidResponse() throws Exception {

        Task task = Task.builder()
            .id(id)
            .caseNumber(caseNumber)
            .title(title)
            .description(description)
            .status(status)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);

        TaskResponse response = underTest.updateTaskStatus(id, status);
        assertEquals(testResponse, response);
        verify(taskRepository).findById(id);
        verify(taskRepository).save(task);
    }

    @Test
    void retrieveAllTasks_shouldReturnListOfValidResponses() throws Exception {

        Task task = Task.builder()
            .id(id)
            .caseNumber(caseNumber)
            .title(title)
            .description(description)
            .status(status)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();

        List<Task> exampleResponses = new ArrayList<>();
        exampleResponses.add(task);

        when(taskRepository.findAll()).thenReturn(exampleResponses);

        List<TaskResponse> response = underTest.retrieveAllTasks();
        assertEquals(testResponse, response.getFirst());
        verify(taskRepository).findAll();
    }

    @Test
    void deleteTask_ShouldCallRepositoryDelete() {

        Task task = Task.builder()
            .id(id)
            .caseNumber(caseNumber)
            .title(title)
            .description(description)
            .status(status)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        underTest.deleteTask(id);
        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_InvalidIdShouldThrowTaskNotFoundException() throws Exception {

        when(taskRepository.findById(id)).thenThrow(new TaskNotFoundException("Task Not Found"));

        assertThrows(TaskNotFoundException.class, () -> {
            underTest.deleteTask(id);
        });

    }

}
