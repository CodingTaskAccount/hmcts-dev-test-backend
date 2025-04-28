package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    private TaskService taskService;

    @Test
    @Transactional
    void create_validShouldReturnOk() throws Exception {
        long id = 1;
        String title = "test title";
        String description = "test description";
        String newStatus = "new status";
        LocalDateTime dueDateTime = LocalDateTime.now();

        TaskResponse testResponse = TaskResponse.builder()
            .id(id)
            .title(title)
            .description(description)
            .status(newStatus)
            .dueDateTime(dueDateTime)
            .build();

        this.mockMvc.perform(post("/create"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(testResponse)));

        verify()
    }
}
