package uk.gov.hmcts.reform.dev.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CreateTaskRequestDTO {

    @NonNull
    public String title;

    public String description;

    @NonNull
    public String status;

    @NonNull
    public LocalDateTime dueDateTime;
}
