package uk.gov.hmcts.reform.dev.services;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.dto.TaskResponse;
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

    public TaskResponse createTask(CreateTaskRequest createTaskRequest) {

        Task task = mapCreateTaskRequestToTask(createTaskRequest);
        task = taskRepository.save(task);

        return mapTaskToResponseDTO(task);
    }

    public TaskResponse retrieveTask(Long id) throws TaskNotFoundException {

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(String.format("No task found with ID: %d", id)));

        return mapTaskToResponseDTO(task);
    }

    public TaskResponse updateTaskStatus(long id, String newStatus) throws TaskNotFoundException {

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(String.format("No task found with ID: %d", id)));

        task.setStatus(newStatus);
        task = taskRepository.save(task);

        return mapTaskToResponseDTO(task);
    }

    public List<TaskResponse> retrieveAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        List<TaskResponse> responses = new ArrayList<>();

        for (Task task : tasks) {
            TaskResponse taskResponse = mapTaskToResponseDTO(task);
            responses.add(taskResponse);
        }

        return responses;
    }

    public void deleteTask(long id) throws TaskNotFoundException {

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        taskRepository.delete(task);
    }

    private TaskResponse mapTaskToResponseDTO(Task task) {
        return TaskResponse.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .status(task.getStatus())
            .dueDateTime(task.getDueDateTime())
            .build();
    }

    private Task mapCreateTaskRequestToTask(CreateTaskRequest createTaskRequest) {
        return Task.builder()
            .title(createTaskRequest.getTitle())
            .description(createTaskRequest.getDescription())
            .status(createTaskRequest.getStatus())
            .dueDateTime(createTaskRequest.getDueDateTime()).build();
    }

}
