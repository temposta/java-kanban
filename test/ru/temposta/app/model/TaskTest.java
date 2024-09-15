package ru.temposta.app.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Задача")
class TaskTest {

    @Test
    @DisplayName("проверка, что экземпляры класса Task равны друг другу, если равен их id;")
    void shouldEqualsDifferentTasks() {
        //подготовка данных
        Task task1 = new Task("Title1", "Description1", TaskStatus.NEW).setId(1);
        Task task2 = new Task("Title2", "Description2", TaskStatus.NEW).setId(1);
        //тестирование
        //проверка
        assertEquals(task1, task2);
    }
}