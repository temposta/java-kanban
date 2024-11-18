package ru.temposta.app.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Подзадача")
class SubtaskTest {

    @Test
    @DisplayName("проверка, что экземпляры класса Subtask равны друг другу, если равен их id;")
    void shouldEqualsDifferentSubtasks() {
        //подготовка
        Subtask subtask1 = new Subtask("Title1", "Description1", TaskStatus.NEW, 1).setId(1);
        Subtask subtask2 = new Subtask("Title2", "Description2", TaskStatus.NEW, 2).setId(1);
        //тестирование
        //проверка
        assertEquals(subtask1, subtask2);
    }
}