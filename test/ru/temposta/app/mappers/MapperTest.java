package ru.temposta.app.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Отображение задач, подзадач и эпиков")
class MapperTest {
    final Mapper mapper = new Mapper();
    Task task;
    Subtask subtask;
    Epic epic;
    Epic epicWithNullTime;
    String taskString;
    String subtaskString;
    String epicString;
    String epicStringWithNullTime;
    LocalDateTime localDateTime;

    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.of(2024, 3, 26, 9, 30);
        task = new Task("t", "d", TaskStatus.NEW, localDateTime, 15).setId(1);
        taskString = "TASK,1,t,d,NEW,null,15,2024-03-26T09:30,true";
        subtask = new Subtask("ts", "sd", TaskStatus.NEW, 3, localDateTime, 15).setId(4);
        subtaskString = "SUBTASK,4,ts,sd,NEW,3,15,2024-03-26T09:30,true";
        epic = new Epic("dsd", "sdf")
                .setId(3);
        epic.setDuration(15);
        epic.setStartTime(localDateTime);
        epicString = "EPIC,3,dsd,sdf,NEW,null,15,2024-03-26T09:30,false";
        epicWithNullTime = new Epic("dsd", "sdf")
                .setId(3);
        epicStringWithNullTime = "EPIC,3,dsd,sdf,NEW,null,0,null,false";
    }

    @Test
    @DisplayName("проверка отображения объекта в строку")
    void toStr() {
        assertEquals(mapper.toStr().apply(task), taskString);
        assertEquals(mapper.toStr().apply(subtask), subtaskString);
        assertEquals(mapper.toStr().apply(epic), epicString);
        assertEquals(mapper.toStr().apply(epicWithNullTime), epicStringWithNullTime);
    }

    @Test
    @DisplayName("проверка отображения строки в объект")
    void toObj() {
        Task expectedTask = mapper.toObj().apply(taskString.split(","));
        assertEquals(expectedTask.getId(), task.getId());
        assertEquals(expectedTask.getTitle(), task.getTitle());
        assertEquals(expectedTask.getDescription(), task.getDescription());
        assertEquals(expectedTask.getStatus(), task.getStatus());
        assertEquals(expectedTask.getDuration(), task.getDuration());
        assertEquals(expectedTask.getStartTime(), task.getStartTime());
        assertEquals(expectedTask.getEndTime(), task.getEndTime());

        Subtask expectedSubtask = (Subtask) mapper.toObj().apply(subtaskString.split(","));
        assertEquals(expectedSubtask.getId(), subtask.getId());
        assertEquals(expectedSubtask.getTitle(), subtask.getTitle());
        assertEquals(expectedSubtask.getDescription(), subtask.getDescription());
        assertEquals(expectedSubtask.getStatus(), subtask.getStatus());
        assertEquals(expectedSubtask.getDuration(), subtask.getDuration());
        assertEquals(expectedSubtask.getStartTime(), subtask.getStartTime());
        assertEquals(expectedSubtask.getEndTime(), subtask.getEndTime());
        assertEquals(expectedSubtask.getParentEpicID(), subtask.getParentEpicID());

        Epic expectedEpic = (Epic) mapper.toObj().apply(epicString.split(","));
        assertEquals(expectedEpic.getId(), epic.getId());
        assertEquals(expectedEpic.getTitle(), epic.getTitle());
        assertEquals(expectedEpic.getDescription(), epic.getDescription());
        assertEquals(expectedEpic.getStatus(), epic.getStatus());
        assertEquals(expectedEpic.getDuration(), epic.getDuration());
        assertEquals(expectedEpic.getStartTime(), epic.getStartTime());
        assertEquals(expectedEpic.getEndTime(), epic.getEndTime());

        Epic expectedEpicWithNullTime = (Epic) mapper.toObj().apply(epicStringWithNullTime.split(","));
        assertEquals(expectedEpicWithNullTime.getId(), epicWithNullTime.getId());
        assertEquals(expectedEpicWithNullTime.getTitle(), epicWithNullTime.getTitle());
        assertEquals(expectedEpicWithNullTime.getDescription(), epicWithNullTime.getDescription());
        assertEquals(expectedEpicWithNullTime.getStatus(), epicWithNullTime.getStatus());
        assertEquals(expectedEpicWithNullTime.getDuration(), epicWithNullTime.getDuration());
        assertEquals(expectedEpicWithNullTime.getStartTime(), epicWithNullTime.getStartTime());
        assertEquals(expectedEpicWithNullTime.getEndTime(), epicWithNullTime.getEndTime());
    }
}