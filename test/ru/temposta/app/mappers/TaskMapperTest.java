package ru.temposta.app.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Отображение задач")
class TaskMapperTest {
    TaskMapper taskMapper;
    Task task;
    String taskString;
    String[] split;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapper();
        task = new Task("Title", "Description", TaskStatus.NEW).setId(1);
        taskString = "TASK,1,Title,Description,NEW";
        split = taskString.split(",");
    }

    @Test
    @DisplayName("проверка отображения объекта Task в строку")
    void toStr() {
        String ts = taskMapper.toStr().apply(task);
        assertEquals(taskString, ts);
    }

    @Test
    @DisplayName("проверка отображения строки в объект Task")
    void toObj() {
        Task t = taskMapper.toObj().apply(split);
        assertEquals(task.getId(), t.getId());
        assertEquals(task.getTitle(), t.getTitle());
        assertEquals(task.getDescription(), t.getDescription());
        assertEquals(task.getStatus(), t.getStatus());
    }
}