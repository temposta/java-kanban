package ru.temposta.app.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Отображение задач, подзадач и эпиков")
class MapperTest {
    Mapper mapper = new Mapper();
    Task task;
    Subtask subtask;
    Epic epic;
    String taskString;
    String subtaskString;
    String epicString;

    @BeforeEach
    void setUp() {
        task = new Task("t", "d", TaskStatus.NEW).setId(1);
        taskString = "TASK,1,t,d,NEW";
        subtask = new Subtask("ts", "sd", TaskStatus.NEW, 3).setId(4);
        subtaskString = "SUBTASK,4,ts,sd,NEW,3";
        epic = new Epic("dsd", "sdf").setId(3);
        epicString = "EPIC,3,dsd,sdf,NEW";
    }

    @Test
    @DisplayName("проверка отображения объекта в строку")
    void toStr() {
        assertEquals(mapper.toStr().apply(task), taskString);
        assertEquals(mapper.toStr().apply(subtask), subtaskString);
        assertEquals(mapper.toStr().apply(epic), epicString);
    }

    @Test
    @DisplayName("проверка отображения строки в объект")
    void toObj() {
        Task expectedTask = mapper.toObj().apply(taskString.split(","));
        assertEquals(expectedTask.getId(), task.getId());
        assertEquals(expectedTask.getTitle(), task.getTitle());
        assertEquals(expectedTask.getDescription(), task.getDescription());
        assertEquals(expectedTask.getStatus(), task.getStatus());

        Subtask expectedSubtask = (Subtask) mapper.toObj().apply(subtaskString.split(","));
        assertEquals(expectedSubtask.getId(), subtask.getId());
        assertEquals(expectedSubtask.getTitle(), subtask.getTitle());
        assertEquals(expectedSubtask.getDescription(), subtask.getDescription());
        assertEquals(expectedSubtask.getStatus(), subtask.getStatus());
        assertEquals(expectedSubtask.getParentEpicID(), subtask.getParentEpicID());

        Epic expectedEpic = (Epic) mapper.toObj().apply(epicString.split(","));
        assertEquals(expectedEpic.getId(), epic.getId());
        assertEquals(expectedEpic.getTitle(), epic.getTitle());
        assertEquals(expectedEpic.getDescription(), epic.getDescription());
        assertEquals(expectedEpic.getStatus(), epic.getStatus());
    }
}