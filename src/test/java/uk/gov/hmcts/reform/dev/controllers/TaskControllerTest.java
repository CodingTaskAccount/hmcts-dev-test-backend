package uk.gov.hmcts.reform.dev.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.excpetions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TaskControllerTest
{
    @Autowired
    private TaskController underTest;

    private long id;
    private long caseNumber;
    private String title;
    private String description;
    private String status;
    private LocalDate createdDate;
    private LocalDateTime dueDateTime;

    private TaskResponse testResponse;

    @MockitoBean
    private TaskService taskService;

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
    void create_shouldReturnOkWithTaskResponse() throws Exception {

        CreateTaskRequest testRequest = CreateTaskRequest.builder()
            .title(title)
            .caseNumber(caseNumber)
            .description(description)
            .status(status)
            .dueDateTime(dueDateTime)
            .build();

        when(taskService.createTask(testRequest)).thenReturn(testResponse);

        ResponseEntity<TaskResponse> response = underTest.createTask(testRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testResponse, response.getBody());
        verify(taskService).createTask(testRequest);
    }

    @Test
    void update_shouldReturnOkWithNewStatus() throws Exception {

        when(taskService.updateTaskStatus(id, status)).thenReturn(testResponse);

        ResponseEntity<TaskResponse> response = underTest.updateTaskStatus(id, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testResponse, response.getBody());
        verify(taskService).updateTaskStatus(id, status);
    }

    @Test
    void retrieveTask_shouldReturnOkWithTaskResponse() throws Exception {

        when(taskService.retrieveTask(id)).thenReturn(testResponse);

        ResponseEntity<TaskResponse> response = underTest.retrieveTaskById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testResponse, response.getBody());
        verify(taskService).retrieveTask(id);
    }

    @Test
    void retrieveAllTasks_shouldReturnOkWithListOfTaskResponses() throws Exception {

        List<TaskResponse> exampleResponses = new ArrayList<>();
        exampleResponses.add(testResponse);
        exampleResponses.add(testResponse);

        when(taskService.retrieveAllTasks()).thenReturn(exampleResponses);

        ResponseEntity<List<TaskResponse>> response = underTest.retrieveAllTasks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(exampleResponses, response.getBody());
        verify(taskService).retrieveAllTasks();
    }

    @Test
    void delete_shouldReturnOkWithId() throws Exception {

        ResponseEntity<Long> response = underTest.deleteTask(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody());
        verify(taskService).deleteTask(id);
    }

    @Test
    void update_invalidId_ShouldReturnNotFound() throws Exception {

        when(taskService.updateTaskStatus(id, status)).thenThrow(new TaskNotFoundException("Task Not Found"));

        ResponseEntity<TaskResponse> response = underTest.updateTaskStatus(id, status);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskService).updateTaskStatus(id, status);
    }

    @Test
    void retrieve_invalidId_ShouldReturnNotFound() throws Exception {

        when(taskService.retrieveTask(id)).thenThrow(new TaskNotFoundException("Task Not Found"));

        ResponseEntity<TaskResponse> response = underTest.retrieveTaskById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskService).retrieveTask(id);
    }

    @Test
    void delete_invalidId_ShouldReturnNotFound() throws Exception {

        doThrow(new TaskNotFoundException("Task Not Found"))
            .when(taskService)
            .deleteTask(id);

        ResponseEntity<Long> response = underTest.deleteTask(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskService).deleteTask(id);
    }

}
