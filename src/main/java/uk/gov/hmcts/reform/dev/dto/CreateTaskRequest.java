package uk.gov.hmcts.reform.dev.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskRequest {

    @NotNull
    public String title;

    public String description;

    @NotNull
    public String status;

    @NotNull
    public LocalDateTime dueDateTime;
}
