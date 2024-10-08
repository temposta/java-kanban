package ru.temposta.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджер истории")
class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    @DisplayName("при добавлении 1 задачи размер списка равен 1, задача добавлена")
    void shouldAdd1Task() {
        //подготовка данных
        Task task = new Task("Title", "Description", TaskStatus.NEW);

        //тестируем
        historyManager.add(task);

        //проверяем
        assertFalse(historyManager.history.isEmpty());
        assertEquals(1, historyManager.history.size());
    }

    @Test
    @DisplayName("при добавлении 10 задач размер списка равен 10")
    void shouldAdd10Tasks() {
        //подготовка данных
        Task task = new Task("Title", "Description", TaskStatus.NEW);

        //тестируем
        for (int i = 0; i < 10; i++) {
            historyManager.add(task);
        }

        //проверяем
        assertEquals(10, historyManager.history.size());
    }

    @Test
    @DisplayName("при добавлении 13 задач размер списка равен 13")
    void shouldAdd13Tasks() {
        //подготовка данных
        Task task = new Task("Title", "Description", TaskStatus.NEW);

        //тестируем
        for (int i = 0; i < 13; i++) {
            historyManager.add(task);
        }

        //проверяем
        assertEquals(13, historyManager.history.size());
    }

    @Test
    @DisplayName("получение списка просмотренных задач при вызове метода")
    void getHistory() {
        //подготовка данных
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        Subtask subtask = new Subtask("Title", "description", TaskStatus.IN_PROGRESS, 1);
        Epic epic = new Epic("Title", "description");
        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.add(epic);
        //тестирование
        List<Task> history = historyManager.getHistory();
        //проверка
        assertEquals(3, historyManager.history.size());
        //для наглядности выводим список в консоль
        System.out.println("history = " + history);


    }

    @Test
    @DisplayName("провера неизменности задачи при добавлении еще одной задачи в менеджер")
    void shouldUnchangedPrevAddTask() {
        //подготовка данных
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        Subtask subtask = new Subtask("Title", "description", TaskStatus.IN_PROGRESS, 1);
        //тестирование
        historyManager.add(task);
        historyManager.add(subtask);
        //проверка
        assertTasks(task, historyManager.history.removeFirst());
    }

    private void assertTasks(Task task, Task task1) {
        assertEquals(task.getTitle(), task1.getTitle());
        assertEquals(task.getDescription(), task1.getDescription());
        assertEquals(task.getStatus(), task1.getStatus());
        assertEquals(task.getId(), task1.getId());
    }
}