package ru.temposta.app.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Отображение подзадач")
class SubtaskMapperTest {
    SubtaskMapper subtaskMapper;
    Subtask subtask;
    String subtaskString;
    String[] split;

    @BeforeEach
    void setUp() {
        subtaskMapper = new SubtaskMapper();
        subtask = new Subtask("Title", "Description", TaskStatus.NEW, 3).setId(1);
        subtaskString = "SUBTASK,1,Title,Description,NEW,3";
        split = subtaskString.split(",");
    }

    @Test
    @DisplayName("проверка отображения объекта Subtask в строку")
    void toStr() {
        String ts = subtaskMapper.toStr().apply(subtask);
        assertEquals(subtaskString, ts);
    }

    @Test
    @DisplayName("проверка отображения строки в объект Subtask")
    void toObj() {
        Subtask st = subtaskMapper.toObj().apply(split);
        assertEquals(subtask.getId(), st.getId());
        assertEquals(subtask.getTitle(), st.getTitle());
        assertEquals(subtask.getDescription(), st.getDescription());
        assertEquals(subtask.getStatus(), st.getStatus());
        assertEquals(subtask.getParentEpicID(), st.getParentEpicID());
    }
}