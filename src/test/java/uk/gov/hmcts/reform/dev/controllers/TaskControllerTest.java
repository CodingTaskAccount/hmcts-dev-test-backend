package uk.gov.hmcts.reform.dev.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.coyote.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.excpetions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.services.TaskService;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TaskControllerTest
{
    @Autowired
    private TaskController underTest;

    long id;
    String title;
    String description;
    String status;
    LocalDateTime dueDateTime;

    private TaskResponse testResponse;

    @MockitoBean
    private TaskService taskService;

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
    void create_shouldReturnOkWithTaskResponse() throws Exception {

        CreateTaskRequest testRequest = CreateTaskRequest.builder()
            .title(title)
            .description(description)
            .status(status)
            .dueDateTime(dueDateTime)
            .build();

        when(taskService.createTask(testRequest)).thenReturn(testResponse);

        ResponseEntity<TaskResponse> response = underTest.createTask(testRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testResponse, response.getBody());
    }

    @Test
    void update_shouldReturnOkWithNewStatus() throws Exception {

        when(taskService.updateTaskStatus(id, status)).thenReturn(testResponse);

        ResponseEntity<TaskResponse> response = underTest.updateTaskStatus(id, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testResponse, response.getBody());
    }

    @Test
    void retrieveTask_shouldReturnOkWithTaskResponse() throws Exception {

        when(taskService.retrieveTask(id)).thenReturn(testResponse);

        ResponseEntity<TaskResponse> response = underTest.retrieveTaskById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testResponse, response.getBody());
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
    }

    @Test
    void delete_shouldReturnOkWithId() throws Exception {

        ResponseEntity<Long> response = underTest.deleteTask(id);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(id, response.getBody());
    }

    @Test
    void update_invalidId_ShouldReturnNotFound() throws Exception {

        when(taskService.updateTaskStatus(id, status)).thenThrow(new TaskNotFoundException("Task Not Found"));

        ResponseEntity<TaskResponse> response = underTest.updateTaskStatus(id, status);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void retrieve_invalidId_ShouldReturnNotFound() throws Exception {

        when(taskService.retrieveTask(id)).thenThrow(new TaskNotFoundException("Task Not Found"));

        ResponseEntity<TaskResponse> response = underTest.retrieveTaskById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void delete_invalidId_ShouldReturnNotFound() throws Exception {

        doThrow(new TaskNotFoundException("Task Not Found"))
            .when(taskService)
            .deleteTask(id);

        ResponseEntity<Long> response = underTest.deleteTask(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
