# HMCTS Task Management Backend

This project uses a PostgreSQL database running on `localhost` with the following configuration, as defined in `application.properties`:

Port = 5432
Username = postgres
Password = postgres

## API Endpoints

### Create Task
**POST** `/create`

- **Request Body** (JSON):
  ```json
  {
    "title": "Task Title",
    "description": "Task Description",
    "status": "TODO",
    "dueDateTime": "2025-05-01T14:30:00"
  }
Response (200 OK) with the created task if successful. 

### Retrieve Task by ID
**GET** `/retrieve/{id}`

Path Variable: id (long)

Response (200 OK) with the task object if ID is found in the database
Error: 404 Not Found if ID is not present

### Retrieve All Tasks
**GET** /retrieveAllTasks

Response (200 OK) with a json list of all tasks
Returns an empty list if no tasks are found.

### Update Task Status
**PATCH** /update/{id}?status={status}

Path Variable: id (long)

Query Parameter: status (string)

Response (200 OK) with the newly updated task object if successsful
Error: 404 Not Found if ID is not present

### Delete Task
**DELETE** /delete/{id}

Path Variable: id (long)

Response (200 OK) with the ID if deleted successfully 
Error: 404 Not Found if ID is not present