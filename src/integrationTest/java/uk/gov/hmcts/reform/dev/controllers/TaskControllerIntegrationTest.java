package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskRepository;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @Test
    void create_validShouldReturnOk() throws Exception {
        long id = 1;
        String title = "test title";
        long caseNumber = 100;
        String description = "test description";
        String status = "status";
        LocalDateTime dueDateTime = LocalDateTime.now();
        LocalDate createdDate = LocalDate.now();

        CreateTaskRequest createTaskRequest = CreateTaskRequest.builder()
            .title(title)
            .caseNumber(caseNumber)
            .description(description)
            .status(status)
            .dueDateTime(dueDateTime)
            .build();

        TaskResponse testResponse = TaskResponse.builder()
            .id(id)
            .title(title)
            .caseNumber(caseNumber)
            .description(description)
            .status(status)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();

        Task task = Task.builder()
            .id(testResponse.getId())
            .title(title)
            .caseNumber(caseNumber)
            .description(description)
            .status(status)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();

        when(taskRepository.save(argThat(
            testTask -> task.getCaseNumber() == caseNumber &&
                task.getTitle().equals(title) &&
                task.getDescription().equals(description) &&
                task.getStatus().equals(status) &&
                task.getCreatedDate().equals(createdDate) &&
                task.getDueDateTime().equals(dueDateTime)
        ))).thenReturn(task);

        this.mockMvc.perform(post("/create")
                                 .contentType("application/json")
                                 .content(objectMapper.writeValueAsString(createTaskRequest)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(testResponse)));

        verify(taskRepository).save(argThat(
            testTask -> task.getCaseNumber() == caseNumber &&
                task.getTitle().equals(title) &&
                task.getDescription().equals(description) &&
                task.getStatus().equals(status) &&
                task.getCreatedDate().equals(createdDate) &&
                task.getDueDateTime().equals(dueDateTime)
        ));
    }

    @Test
    void retrieve_validShouldReturnOk() throws Exception {
        long id = 1;
        String title = "test title";
        long caseNumber = 100;
        String description = "test description";
        String status = "status";
        LocalDateTime dueDateTime = LocalDateTime.now();
        LocalDate createdDate = LocalDate.now();

        TaskResponse testResponse = TaskResponse.builder()
            .id(id)
            .title(title)
            .caseNumber(caseNumber)
            .description(description)
            .status(status)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();

        Task task = Task.builder()
            .id(testResponse.getId())
            .title(title)
            .caseNumber(caseNumber)
            .description(description)
            .status(status)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        this.mockMvc.perform(get("/retrieveTask/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(testResponse)));

        verify(taskRepository).findById(id);
    }

    @Test
    void update_validShouldReturnOk() throws Exception {
        long id = 1;
        String title = "test title";
        long caseNumber = 100;
        String description = "test description";
        String status = "status";
        String newStatus = "new status";
        LocalDateTime dueDateTime = LocalDateTime.now();
        LocalDate createdDate = LocalDate.now();

        TaskResponse testResponse = TaskResponse.builder()
            .id(id)
            .title(title)
            .caseNumber(caseNumber)
            .description(description)
            .status(newStatus)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();

        Task task = Task.builder()
            .id(testResponse.getId())
            .title(title)
            .caseNumber(caseNumber)
            .description(description)
            .status(status)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(argThat(
            testTask -> task.getCaseNumber() == caseNumber &&
                task.getTitle().equals(title) &&
                task.getDescription().equals(description) &&
                task.getStatus().equals(newStatus) &&
                task.getCreatedDate().equals(createdDate) &&
                task.getDueDateTime().equals(dueDateTime)
        ))).thenReturn(task);

        this.mockMvc.perform(patch("/update/{id}?status={newStatus}", id, newStatus))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(testResponse)));

        verify(taskRepository).findById(id);
        verify(taskRepository).save(argThat(
            testTask -> task.getCaseNumber() == caseNumber &&
                task.getTitle().equals(title) &&
                task.getDescription().equals(description) &&
                task.getStatus().equals(newStatus) &&
                task.getCreatedDate().equals(createdDate) &&
                task.getDueDateTime().equals(dueDateTime)
        ));
    }

    @Test
    void retrieveAll_validShouldReturnOk() throws Exception {
        long id = 1;
        String title = "test title";
        long caseNumber = 100;
        String description = "test description";
        String status = "status";
        String newStatus = "new status";
        LocalDateTime dueDateTime = LocalDateTime.now();
        LocalDate createdDate = LocalDate.now();

        TaskResponse testResponse = TaskResponse.builder()
            .id(id)
            .title(title)
            .caseNumber(caseNumber)
            .description(description)
            .status(newStatus)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();

        Task task = Task.builder()
            .id(testResponse.getId())
            .title(title)
            .caseNumber(caseNumber)
            .description(description)
            .status(status)
            .createdDate(createdDate)
            .dueDateTime(dueDateTime)
            .build();

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(task);

        when(taskRepository.findAll()).thenReturn(tasks);

        this.mockMvc.perform(get("/retrieveAllTasks"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(tasks)));

        verify(taskRepository).findAll();
    }
}
