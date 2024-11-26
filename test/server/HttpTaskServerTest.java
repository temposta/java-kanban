package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.temposta.app.mappers.GsonMapper;
import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;
import ru.temposta.app.service.InMemoryTaskManager;
import ru.temposta.app.service.TaskManager;
import ru.temposta.app.util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    HttpTaskServer server;
    HttpClient client;
    GsonMapper mapper;


    @BeforeEach
    void setUpBeforeClass() {
        mapper = new GsonMapper();
        TaskManager tm = new InMemoryTaskManager(Managers.getDefaultHistoryManager());
        Task task1 = new Task("Title", "Description", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.parse("2024-03-26T10:30"));
        tm.addAnyTask(task1);
        Task task2 = new Task("Title2", "Description2", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.parse("2024-03-26T12:30"));
        tm.addAnyTask(task2);
        Epic epic = new Epic("T1", "sde");
        tm.addAnyTask(epic);
        Subtask subtask = new Subtask("T1", "sdf", TaskStatus.IN_PROGRESS, epic);
        subtask.setStartTime(LocalDateTime.parse("2024-03-26T11:35"));
        tm.addAnyTask(subtask);
        tm.getAnyTaskById(1);
        tm.getAnyTaskById(3);

        server = new HttpTaskServer(tm);
        server.start();

        client = HttpClient.newHttpClient();


    }

    @AfterEach
    void tearDownAfterClass() {
        server.stop();
    }

    @Test
    @DisplayName("тестирование GET запросов сервера")
    void testGetQuery() throws IOException, InterruptedException {
        HttpResponse<String> response = get(client, "/tasks");
        String expectedBody = """
                [
                  {
                    "id": 0,
                    "title": "Title",
                    "description": "Description",
                    "status": "NEW",
                    "startTime": "2024-03-26 10:30:00",
                    "duration": 15,
                    "isTakePriority": true
                  },
                  {
                    "id": 1,
                    "title": "Title2",
                    "description": "Description2",
                    "status": "NEW",
                    "startTime": "2024-03-26 12:30:00",
                    "duration": 15,
                    "isTakePriority": true
                  }
                ]""" ;
        assertEquals(200, response.statusCode());
        String actualBody = response.body();
        assertEquals(expectedBody, actualBody);

        response = get(client, "/subtasks");
        expectedBody = """
                [
                  {
                    "parentEpicID": 2,
                    "id": 3,
                    "title": "T1",
                    "description": "sdf",
                    "status": "IN_PROGRESS",
                    "startTime": "2024-03-26 11:35:00",
                    "duration": 15,
                    "isTakePriority": true
                  }
                ]""" ;
        assertEquals(200, response.statusCode());
        actualBody = response.body();
        assertEquals(expectedBody, actualBody);

        response = get(client, "/epics");
        expectedBody = """
                [
                  {
                    "subTasks": [
                      3
                    ],
                    "endTime": "2024-03-26 11:50:00",
                    "id": 2,
                    "title": "T1",
                    "description": "sde",
                    "status": "IN_PROGRESS",
                    "startTime": "2024-03-26 11:35:00",
                    "duration": 15,
                    "isTakePriority": false
                  }
                ]""" ;
        assertEquals(200, response.statusCode());
        actualBody = response.body();
        assertEquals(expectedBody, actualBody);

        response = get(client, "/history");
        expectedBody = """
                [
                  {
                    "id": 1,
                    "title": "Title2",
                    "description": "Description2",
                    "status": "NEW",
                    "startTime": "2024-03-26 12:30:00",
                    "duration": 15,
                    "isTakePriority": true
                  },
                  {
                    "parentEpicID": 2,
                    "id": 3,
                    "title": "T1",
                    "description": "sdf",
                    "status": "IN_PROGRESS",
                    "startTime": "2024-03-26 11:35:00",
                    "duration": 15,
                    "isTakePriority": true
                  }
                ]""" ;
        assertEquals(200, response.statusCode());
        actualBody = response.body();
        assertEquals(expectedBody, actualBody);

        response = get(client, "/prioritized");
        expectedBody = """
                [
                  {
                    "id": 0,
                    "title": "Title",
                    "description": "Description",
                    "status": "NEW",
                    "startTime": "2024-03-26 10:30:00",
                    "duration": 15,
                    "isTakePriority": true
                  },
                  {
                    "parentEpicID": 2,
                    "id": 3,
                    "title": "T1",
                    "description": "sdf",
                    "status": "IN_PROGRESS",
                    "startTime": "2024-03-26 11:35:00",
                    "duration": 15,
                    "isTakePriority": true
                  },
                  {
                    "id": 1,
                    "title": "Title2",
                    "description": "Description2",
                    "status": "NEW",
                    "startTime": "2024-03-26 12:30:00",
                    "duration": 15,
                    "isTakePriority": true
                  }
                ]""" ;
        assertEquals(200, response.statusCode());
        actualBody = response.body();
        assertEquals(expectedBody, actualBody);

        response = get(client, "/tasks/1");
        expectedBody = """
                {
                  "id": 1,
                  "title": "Title2",
                  "description": "Description2",
                  "status": "NEW",
                  "startTime": "2024-03-26 12:30:00",
                  "duration": 15,
                  "isTakePriority": true
                }""" ;
        assertEquals(200, response.statusCode());
        actualBody = response.body();
        assertEquals(expectedBody, actualBody);

        response = get(client, "/subtasks/3");
        expectedBody = """
                {
                  "parentEpicID": 2,
                  "id": 3,
                  "title": "T1",
                  "description": "sdf",
                  "status": "IN_PROGRESS",
                  "startTime": "2024-03-26 11:35:00",
                  "duration": 15,
                  "isTakePriority": true
                }""" ;
        assertEquals(200, response.statusCode());
        actualBody = response.body();
        assertEquals(expectedBody, actualBody);

        response = get(client, "/tasks/5");
        assertEquals(404, response.statusCode());

        response = get(client, "/subtasks/10");
        assertEquals(404, response.statusCode());

        response = get(client, "/epics/2");
        expectedBody = """
                {
                  "subTasks": [
                    3
                  ],
                  "endTime": "2024-03-26 11:50:00",
                  "id": 2,
                  "title": "T1",
                  "description": "sde",
                  "status": "IN_PROGRESS",
                  "startTime": "2024-03-26 11:35:00",
                  "duration": 15,
                  "isTakePriority": false
                }""" ;
        assertEquals(200, response.statusCode());
        actualBody = response.body();
        assertEquals(expectedBody, actualBody);

        response = get(client, "/epics/2/subtasks");
        expectedBody = """
                [
                  {
                    "parentEpicID": 2,
                    "id": 3,
                    "title": "T1",
                    "description": "sdf",
                    "status": "IN_PROGRESS",
                    "startTime": "2024-03-26 11:35:00",
                    "duration": 15,
                    "isTakePriority": true
                  }
                ]""" ;
        assertEquals(200, response.statusCode());
        actualBody = response.body();
        assertEquals(expectedBody, actualBody);
    }

    @Test
    @DisplayName("тестирование POST запросов сервера")
    void testPostQuery() throws IOException, InterruptedException {
        Task t = new Task("w", "d", TaskStatus.NEW);

        String postBody = mapper.toJSON().apply(t);
        HttpResponse<String> response = post(client, "/tasks", postBody);
        assertEquals(201, response.statusCode());

        Subtask s = new Subtask("d", "s", TaskStatus.NEW, 2);
        s.setStartTime(LocalDateTime.parse("2024-05-26T11:50"));
        s.setDuration(15);

        postBody = mapper.toJSON().apply(s);
        response = post(client, "/subtasks", postBody);
        assertEquals(201, response.statusCode());

        Epic epic = new Epic("d", "s");
        postBody = mapper.toJSON().apply(epic);
        response = post(client, "/epics", postBody);
        assertEquals(201, response.statusCode());
    }

    @Test
    @DisplayName("тестирование DELETE запросов сервера")
    void testDeleteQuery() throws IOException, InterruptedException {
        HttpResponse<String> response = delete(client, "/tasks/1");
        assertEquals(200, response.statusCode());

        response = delete(client, "/subtasks/3");
        assertEquals(200, response.statusCode());

        response = delete(client, "/epics/2");
        assertEquals(200, response.statusCode());

    }

    private HttpResponse<String> get(HttpClient client, String path) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + path))
                .GET()
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(HttpClient client, String path, String body) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + path))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> delete(HttpClient client, String path) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080" + path))
                .DELETE()
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }


}