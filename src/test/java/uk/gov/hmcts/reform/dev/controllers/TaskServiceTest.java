package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskRepository;
import uk.gov.hmcts.reform.dev.services.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    long id;
    String title;
    String description;
    String status;
    LocalDateTime dueDateTime;

    private TaskResponse testResponse;

    @BeforeEach
    void setUp() {
        id = 1;
        title = "test title";
        description = "test description";
        status = "test status";
        dueDateTime = LocalDateTime.now();

        testResponse = TaskResponse.builder()
            .id(id)
            .title(title)
            .description(description)
            .status(status)
            .dueDateTime(dueDateTime)
            .build();
    }

    @Test
    void createTask_shouldReturnValidResponse() throws Exception {

        CreateTaskRequest createTaskRequest = CreateTaskRequest.builder()
            .title(title)
            .description(description)
            .status(status)
            .dueDateTime(dueDateTime)
            .build();

        Task task = Task.builder()
            .id(id)
            .title(createTaskRequest.getTitle())
            .description(createTaskRequest.getDescription())
            .status(createTaskRequest.getStatus())
            .dueDateTime(createTaskRequest.getDueDateTime())
            .build();

        when(taskRepository.save(any())).thenReturn(task);

        TaskResponse response = underTest.createTask(createTaskRequest);
        assertEquals(testResponse, response);
    }

    @Test
    void retrieveTask_shouldReturnValidResponse() throws Exception {

        Task task = Task.builder()
            .id(id)
            .title(title)
            .description(description)
            .status(status)
            .dueDateTime(dueDateTime)
            .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        TaskResponse response = underTest.retrieveTask(id);
        assertEquals(testResponse, response);
    }

    @Test
    void updateTask_shouldReturnValidResponse() throws Exception {

        Task task = Task.builder()
            .id(id)
            .title(title)
            .description(description)
            .status(status)
            .dueDateTime(dueDateTime)
            .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);

        TaskResponse response = underTest.updateTaskStatus(id, status);
        assertEquals(testResponse, response);
    }

    @Test
    void retrieveAllTasks_shouldReturnListOfValidResponses() throws Exception {

        Task task = Task.builder()
            .id(id)
            .title(title)
            .description(description)
            .status(status)
            .dueDateTime(dueDateTime)
            .build();

        List<Task> exampleResponses = new ArrayList<>();
        exampleResponses.add(task);

        when(taskRepository.findAll()).thenReturn(exampleResponses);

        List<TaskResponse> response = underTest.retrieveAllTasks();
        assertEquals(testResponse, response.getFirst());
    }

}
