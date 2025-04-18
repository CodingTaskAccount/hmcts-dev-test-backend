package uk.gov.hmcts.reform.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class TaskResponseDTO {

    public long id;

    @NonNull
    public String title;

    public String description;

    @NonNull
    public String status;

    @NonNull
    public LocalDateTime dueDateTime;
}
