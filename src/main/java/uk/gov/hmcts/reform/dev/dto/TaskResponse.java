package uk.gov.hmcts.reform.dev.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponse {

    public long id;

    public long caseNumber;

    @NonNull
    public String title;

    public String description;

    @NonNull
    public String status;

    @NonNull
    public LocalDate createdDate;

    @NonNull
    public LocalDateTime dueDateTime;
}
