package ru.temposta.app.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("тестирование класса преобразования JSON(String)")
class GsonMapperTest {
    private GsonMapper mapper;
    private Task task;
    private Subtask subtask;
    private Epic epic;
    private String JSONOfTask;
    private String JSONOfSubtask;
    private String JSONOfEpic;

    @BeforeEach
    void beforeEach() {
        mapper = new GsonMapper();
        task = new Task("t", "d", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.parse("2024-03-26T10:30"));
        task.setDuration(30);
        subtask = new Subtask("t", "d", TaskStatus.NEW, 1);
        subtask.setStartTime(LocalDateTime.parse("2024-03-26T10:30"));
        subtask.setDuration(30);
        epic = new Epic("t", "d");
        List.of(1, 2, 3).forEach(i -> epic.addSubtaskID(i));
        JSONOfTask = """
                {
                  "id": -1,
                  "title": "t",
                  "description": "d",
                  "status": "NEW",
                  "startTime": "2024-03-26 10:30:00",
                  "duration": 30,
                  "isTakePriority": true
                }""" ;
        JSONOfSubtask = """
                {
                  "parentEpicID": 1,
                  "id": -1,
                  "title": "t",
                  "description": "d",
                  "status": "NEW",
                  "startTime": "2024-03-26 10:30:00",
                  "duration": 30,
                  "isTakePriority": true
                }""" ;
        JSONOfEpic = """
                {
                  "subTasks": [
                    1,
                    2,
                    3
                  ],
                  "endTime": null,
                  "id": -1,
                  "title": "t",
                  "description": "d",
                  "status": "NEW",
                  "startTime": null,
                  "duration": 0,
                  "isTakePriority": false
                }""" ;

    }

    @Test
    @DisplayName("преобразование задач, подзадач и эпиков в JSON")
    void toJSON() {
        String actualJSON = mapper.toJSON().apply(task);
        assertEquals(JSONOfTask, actualJSON, "Неверно преобразовано в JSON (TASK)");

        actualJSON = mapper.toJSON().apply(subtask);
        assertEquals(JSONOfSubtask, actualJSON, "Неверно преобразовано в JSON (SUBTASK)");

        actualJSON = mapper.toJSON().apply(epic);
        assertEquals(JSONOfEpic, actualJSON, "Неверно преобразовано в JSON (EPIC)");
    }

    @Test
    @DisplayName("преобразование списка задач в JSON")
    void toJSONTaskList() {
        String actualJSON = mapper.toJSONTaskList().apply(List.of(task.setId(1), task.setId(2)));
        String expectedJSON = """
                [
                  {
                    "id": 2,
                    "title": "t",
                    "description": "d",
                    "status": "NEW",
                    "startTime": "2024-03-26 10:30:00",
                    "duration": 30,
                    "isTakePriority": true
                  },
                  {
                    "id": 2,
                    "title": "t",
                    "description": "d",
                    "status": "NEW",
                    "startTime": "2024-03-26 10:30:00",
                    "duration": 30,
                    "isTakePriority": true
                  }
                ]""" ;
        assertEquals(expectedJSON, actualJSON, "Неверно преобразован список задач в JSON");
    }

    @Test
    @DisplayName("преобразование списка подзадач в JSON")
    void toJSONSubtaskList() {
        String actualJSON = mapper.toJSONTaskList().apply(List.of(subtask.setId(1), subtask.setId(2)));
        String expectedJSON = """
                [
                  {
                    "parentEpicID": 1,
                    "id": 2,
                    "title": "t",
                    "description": "d",
                    "status": "NEW",
                    "startTime": "2024-03-26 10:30:00",
                    "duration": 30,
                    "isTakePriority": true
                  },
                  {
                    "parentEpicID": 1,
                    "id": 2,
                    "title": "t",
                    "description": "d",
                    "status": "NEW",
                    "startTime": "2024-03-26 10:30:00",
                    "duration": 30,
                    "isTakePriority": true
                  }
                ]""" ;
        assertEquals(expectedJSON, actualJSON, "Неверно преобразован список подзадач в JSON");
    }

    @Test
    @DisplayName("преобразование списка эпиков в JSON")
    void toJSONEpicList() {
        String actualJSON = mapper.toJSONTaskList().apply(List.of(epic.setId(1), epic.setId(2)));
        String expectedJSON = """
                [
                  {
                    "subTasks": [
                      1,
                      2,
                      3
                    ],
                    "endTime": null,
                    "id": 2,
                    "title": "t",
                    "description": "d",
                    "status": "NEW",
                    "startTime": null,
                    "duration": 0,
                    "isTakePriority": false
                  },
                  {
                    "subTasks": [
                      1,
                      2,
                      3
                    ],
                    "endTime": null,
                    "id": 2,
                    "title": "t",
                    "description": "d",
                    "status": "NEW",
                    "startTime": null,
                    "duration": 0,
                    "isTakePriority": false
                  }
                ]""" ;
        assertEquals(expectedJSON, actualJSON, "Неверно преобразован список эпиков в JSON");
    }

    @Test
    @DisplayName("преобразование в сущность TASK")
    void toTaskEntity() {
        Task actualTask = mapper.toTaskEntity().apply(JSONOfTask);
        assertEquals(task.getId(), actualTask.getId());
        assertEquals(task.getTitle(), actualTask.getTitle());
        assertEquals(task.getDescription(), actualTask.getDescription());
        assertEquals(task.getStatus(), actualTask.getStatus());
        assertEquals(task.getStartTime(), actualTask.getStartTime());
        assertEquals(task.getDuration(), actualTask.getDuration());
        assertEquals(task.isTakePriority(), actualTask.isTakePriority());
    }

    @Test
    @DisplayName("преобразование в сущность SUBTASK")
    void toSubtaskEntity() {
        Subtask actualSubtask = mapper.toSubtaskEntity().apply(JSONOfSubtask);
        assertEquals(subtask.getId(), actualSubtask.getId());
        assertEquals(subtask.getTitle(), actualSubtask.getTitle());
        assertEquals(subtask.getDescription(), actualSubtask.getDescription());
        assertEquals(subtask.getStatus(), actualSubtask.getStatus());
        assertEquals(subtask.getStartTime(), actualSubtask.getStartTime());
        assertEquals(subtask.getDuration(), actualSubtask.getDuration());
        assertEquals(subtask.isTakePriority(), actualSubtask.isTakePriority());
        assertEquals(subtask.getParentEpicID(), actualSubtask.getParentEpicID());
    }

    @Test
    @DisplayName("преобразование в сущность EPIC")
    void toEpicEntity() {
        Epic actualEpic = mapper.toEpicEntity().apply(JSONOfEpic);
        assertEquals(epic.getId(), actualEpic.getId());
        assertEquals(epic.getTitle(), actualEpic.getTitle());
        assertEquals(epic.getDescription(), actualEpic.getDescription());
        assertEquals(epic.getStatus(), actualEpic.getStatus());
        assertEquals(epic.getStartTime(), actualEpic.getStartTime());
        assertEquals(epic.getDuration(), actualEpic.getDuration());
        assertEquals(epic.isTakePriority(), actualEpic.isTakePriority());
        assertEquals(epic.getSubTasksIDList().size(), actualEpic.getSubTasksIDList().size());
        assertEquals(epic.getSubTasksIDList().getFirst(), actualEpic.getSubTasksIDList().getFirst());
    }

    @Test
    @DisplayName("преобразование списка приоритетных задач в JSON")
    void toJSONTreeSetOfTasks() {
        TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        prioritizedTasks.add(task);
        subtask.setStartTime(LocalDateTime.parse("2024-05-15T20:15"));
        prioritizedTasks.add(subtask);
        String actualJSON = mapper.toJSONTreeSetOfTasks().apply(prioritizedTasks);
        String expectedJSON = """
                [
                  {
                    "id": -1,
                    "title": "t",
                    "description": "d",
                    "status": "NEW",
                    "startTime": "2024-03-26 10:30:00",
                    "duration": 30,
                    "isTakePriority": true
                  },
                  {
                    "parentEpicID": 1,
                    "id": -1,
                    "title": "t",
                    "description": "d",
                    "status": "NEW",
                    "startTime": "2024-05-15 20:15:00",
                    "duration": 30,
                    "isTakePriority": true
                  }
                ]""" ;
        assertEquals(actualJSON, expectedJSON);

    }
}