package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    private TaskService taskService;

    @Test
    void update_shouldReturnOkWithUpdatedStatus() throws Exception {
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

        when(taskService.updateTaskStatus(id, newStatus)).thenReturn(testResponse);

        this.mockMvc.perform(patch("/update/{id}", id)
                                 .param("status", newStatus))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(testResponse)));
    }

    @Test
    void retrieveTask_shouldReturnOkWithTaskResponse() throws Exception {

    }
}
