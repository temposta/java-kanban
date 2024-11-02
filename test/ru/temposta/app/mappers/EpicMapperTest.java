package ru.temposta.app.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.temposta.app.model.Epic;
import ru.temposta.app.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Отображение эпиков")
class EpicMapperTest {
    EpicMapper epicMapper;
    Epic epic;
    String epicString;
    String[] split;

    @BeforeEach
    void setUp() {
        epicMapper = new EpicMapper();
        epic = new Epic("Title", "Description").setId(1);
        epic.setStatus(TaskStatus.NEW);
        epic.addSubtaskID(5);
        epic.addSubtaskID(6);
        epic.addSubtaskID(4);
        epicString = "EPIC,1,Title,Description,NEW,5:6:4";
        split = epicString.split(",");
    }

    @Test
    @DisplayName("проверка отображения объекта Epic в строку")
    void toStr() {
        String ts = epicMapper.toStr().apply(epic);
        assertEquals(epicString, ts);
    }

    @Test
    @DisplayName("проверка отображения строки в объект Epic")
    void toObj() {
        Epic ep = epicMapper.toObj().apply(split);
        List<Integer> ids = List.of(5, 6, 4);
        assertEquals(epic.getId(), ep.getId());
        assertEquals(epic.getTitle(), ep.getTitle());
        assertEquals(epic.getDescription(), ep.getDescription());
        assertEquals(epic.getStatus(), ep.getStatus());
        assertEquals(epic.getSubTasksIDList(), ids);
    }
}