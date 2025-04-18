package uk.gov.hmcts.reform.dev.services;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequestDTO;
import uk.gov.hmcts.reform.dev.dto.TaskResponseDTO;
import uk.gov.hmcts.reform.dev.excpetions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository repository) {
        this.taskRepository = repository;
    }

    public TaskResponseDTO createTask(CreateTaskRequestDTO createTaskRequestDTO) {

        Task task = mapCreateTaskRequestToTask(createTaskRequestDTO);
        task = taskRepository.save(task);

        return mapTaskToResponseDTO(task);
    }

    public TaskResponseDTO retrieveTask(Long id) throws TaskNotFoundException {

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(String.format("No task found with ID: %d", id)));

        return mapTaskToResponseDTO(task);
    }

    public TaskResponseDTO updateTaskStatus(long id, String newStatus) throws TaskNotFoundException {

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(String.format("No task found with ID: %d", id)));

        task.setStatus(newStatus);
        task = taskRepository.save(task);

        return mapTaskToResponseDTO(task);
    }

    public List<TaskResponseDTO> retrieveAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        List<TaskResponseDTO> responses = new ArrayList<>();

        for (Task task : tasks) {
            TaskResponseDTO taskResponseDTO = mapTaskToResponseDTO(task);
            responses.add(taskResponseDTO);
        }

        return responses;
    }

    public void deleteTask(long id) throws TaskNotFoundException {

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        taskRepository.delete(task);
    }

    private TaskResponseDTO mapTaskToResponseDTO(Task task) {
        return TaskResponseDTO.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .status(task.getStatus())
            .dueDateTime(task.getDueDate())
            .build();
    }

    private Task mapCreateTaskRequestToTask(CreateTaskRequestDTO createTaskRequestDTO) {
        return Task.builder()
            .title(createTaskRequestDTO .getTitle())
            .description(createTaskRequestDTO.getDescription())
            .status(createTaskRequestDTO.getStatus())
            .dueDate(createTaskRequestDTO.getDueDateTime()).build();
    }

}
