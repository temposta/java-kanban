package ru.temposta.app.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Эпик")
class EpicTest {

    @Test
    @DisplayName("проверка, что экземпляры класса Epic равны друг другу, если равен их id;")
    void shouldEqualsDifferentEpics() {
        Epic epic1 = new Epic("Title1", "Description1").setId(1);
        Epic epic2 = new Epic("Title2", "Description2").setId(1);
        assertEquals(epic1, epic2);
    }
}