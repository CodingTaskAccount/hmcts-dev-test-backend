package uk.gov.hmcts.reform.dev.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private long caseNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Column(nullable = false)
    private LocalDateTime dueDateTime;
}
