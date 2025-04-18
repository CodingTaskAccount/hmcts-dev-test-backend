package uk.gov.hmcts.reform.dev.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.reform.dev.dto.TaskResponseDTO;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> { }
