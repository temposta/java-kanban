package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.temposta.app.exceptions.NotFoundException;
import ru.temposta.app.exceptions.ValidationException;
import ru.temposta.app.mappers.GsonMapper;
import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;
import ru.temposta.app.service.Endpoint;
import ru.temposta.app.service.InMemoryTaskManager;
import ru.temposta.app.service.TaskManager;
import ru.temposta.app.util.Managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskServer {
    private final TaskManager taskManager;
    private final HttpServer server;
    private static final int PORT = 8080;
    GsonMapper gsonMapper;
    BaseHttpHandler httpHandler;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/", this::Handler);
        gsonMapper = new GsonMapper();
        httpHandler = new BaseHttpHandler();
    }

    public void start() {
        server.setExecutor(null);
        server.start();
        System.out.println("Сервер запущен на порту: " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер на порту: " + PORT + " остановлен.");
    }

    private void Handler(HttpExchange httpExchange) throws IOException {
        try (httpExchange) {
            Endpoint endpoint = getEndpoint(httpExchange);
            try {
                switch (endpoint) {
                    case GET_TASKS -> httpHandler.sendText(httpExchange,
                            gsonMapper.toJSONTaskList().apply(taskManager.getTasks()));
                    case GET_SUBTASKS -> httpHandler.sendText(httpExchange,
                            gsonMapper.toJSONSubtaskList().apply(taskManager.getSubtasks()));
                    case GET_EPICS -> httpHandler.sendText(httpExchange,
                            gsonMapper.toJSONEpicList().apply(taskManager.getEpics()));
                    case GET_HISTORY -> httpHandler.sendText(httpExchange,
                            gsonMapper.toJSONTaskList().apply(taskManager.getHistory()));
                    case GET_TASK_BY_ID, GET_EPIC_BY_ID, GET_SUBTASK_BY_ID -> getAnyTask(httpExchange);
                    case DELETE_TASK_BY_ID, DELETE_SUBTASK_BY_ID, DELETE_EPIC_BY_ID -> deleteAnyTask(httpExchange);
                    case POST_TASK, POST_SUBTASK, POST_EPIC -> postAnyTask(httpExchange, endpoint);
                    case GET_EPIC_SUBTASKS_BY_ID -> {
                        int epicID = Integer.parseInt(httpExchange.getRequestURI()
                                .toString()
                                .split("/")[2]);
                        Epic currentEpic = (Epic) taskManager.getAnyTaskById(epicID);
                        List<Integer> subtaskIdsList = currentEpic.getSubTasksIDList();
                        List<Task> subtasks = subtaskIdsList.stream()
                                .map(taskManager::getAnyTaskById)
                                .toList();
                        httpHandler.sendText(httpExchange,
                                gsonMapper.toJSONTaskList().apply(subtasks));
                    }
                    case GET_PRIORITIZED_TASKS -> httpHandler.sendText(httpExchange,
                            gsonMapper.toJSONTreeSetOfTasks().apply(taskManager.getPrioritizedTasks()));
                    case NONE -> httpHandler.sendBadRequest(httpExchange);
                    default -> throw new IllegalStateException("Unexpected value: " + endpoint);
                }
            } catch (ValidationException validationException) {
                //validationException.printStackTrace();
                httpHandler.sendNonAcceptable(httpExchange);
            } catch (NotFoundException notFoundException) {
                httpHandler.sendNotFound(httpExchange);
            } catch (IllegalStateException e) {
                //e.printStackTrace();
                httpHandler.sendServerError(httpExchange);
            }
        }
    }

    private void postAnyTask(HttpExchange httpExchange, Endpoint endpoint) throws IOException {
        Task task = null;
        String body;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()))) {
            body = reader.lines().collect(Collectors.joining());
        }
        switch (endpoint) {
            case POST_TASK -> task = gsonMapper.toTaskEntity().apply(body);
            case POST_SUBTASK -> task = gsonMapper.toSubtaskEntity().apply(body);
            case POST_EPIC -> task = gsonMapper.toEpicEntity().apply(body);
        }

        if (task == null) {
            httpHandler.sendNotFound(httpExchange);
        } else {
            if (task.getId() == -1) {
                taskManager.addAnyTask(task);
                httpHandler.sendCreated(httpExchange);
            } else {
                taskManager.updateTask(task);
                httpHandler.sendUpdated(httpExchange);
            }
        }
    }

    private void deleteAnyTask(HttpExchange httpExchange) throws IOException {
        int taskID = Integer.parseInt(httpExchange.getRequestURI()
                .toString()
                .split("/")[2]);
        taskManager.removeTaskById(taskID);
        httpHandler.sendText(httpExchange, "Удалено");
    }

    private void getAnyTask(HttpExchange httpExchange) throws IOException {
        int taskID = Integer.parseInt(httpExchange.getRequestURI()
                .toString()
                .split("/")[2]);
        Task task = taskManager.getAnyTaskById(taskID);
        if (task == null) {
            httpHandler.sendNotFound(httpExchange);
        } else {
            httpHandler.sendText(httpExchange,
                    gsonMapper.toJSON().apply(taskManager.getAnyTaskById(taskID)));
        }
    }

    private Endpoint getEndpoint(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String requestURI = httpExchange.getRequestURI().toString();
        String[] requestParts = requestURI.split("/");
        String entity = requestParts[1];
        switch (method) {
            case "GET":
                if (entity.equals("tasks")) {
                    if (requestParts.length == 2) return Endpoint.GET_TASKS;
                    if (requestParts.length == 3) return Endpoint.GET_TASK_BY_ID;
                } else if (entity.equals("subtasks")) {
                    if (requestParts.length == 2) return Endpoint.GET_SUBTASKS;
                    if (requestParts.length == 3) return Endpoint.GET_SUBTASK_BY_ID;
                } else if (entity.equals("epics")) {
                    if (requestParts.length == 2) return Endpoint.GET_EPICS;
                    if (requestParts.length == 3) return Endpoint.GET_EPIC_BY_ID;
                    if (requestParts.length == 4 && requestParts[3].equals("subtasks")) {
                        return Endpoint.GET_EPIC_SUBTASKS_BY_ID;
                    }
                } else if (entity.equals("history") && requestParts.length == 2) {
                    return Endpoint.GET_HISTORY;
                } else if (entity.equals("prioritized") && requestParts.length == 2) {
                    return Endpoint.GET_PRIORITIZED_TASKS;
                } else {
                    httpHandler.sendBadRequest(httpExchange);
                }
            case "POST":
                switch (entity) {
                    case "tasks" -> {
                        return Endpoint.POST_TASK;
                    }
                    case "subtasks" -> {
                        return Endpoint.POST_SUBTASK;
                    }
                    case "epics" -> {
                        return Endpoint.POST_EPIC;
                    }
                    default -> httpHandler.sendBadRequest(httpExchange);
                }

            case "DELETE":
                if (requestParts.length == 3) {
                    switch (entity) {
                        case "tasks" -> {
                            return Endpoint.DELETE_TASK_BY_ID;
                        }
                        case "subtasks" -> {
                            return Endpoint.DELETE_SUBTASK_BY_ID;
                        }
                        case "epics" -> {
                            return Endpoint.DELETE_EPIC_BY_ID;
                        }
                        default -> httpHandler.sendBadRequest(httpExchange);
                    }
                }
            default:
                httpHandler.sendBadRequest(httpExchange);
        }
        return Endpoint.NONE;
    }

    public static void main(String[] args) {
        TaskManager t = new InMemoryTaskManager(Managers.getDefaultHistoryManager());
        t.addAnyTask(new Task("Title", "Description", TaskStatus.NEW));
        t.addAnyTask(new Task("Title2", "Description2", TaskStatus.NEW));
        Epic epic = new Epic("T1", "sde");
        t.addAnyTask(epic);
        Subtask subtask = new Subtask("T1", "sdf", TaskStatus.IN_PROGRESS, epic);
        subtask.setStartTime(LocalDateTime.parse("2024-03-26T10:35"));
        t.addAnyTask(subtask);
        t.getAnyTaskById(1);
        t.getAnyTaskById(3);


        HttpTaskServer server = new HttpTaskServer(t);
        server.start();
    }
}
