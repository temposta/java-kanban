package ru.temposta.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Менеджер истории")
class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    @DisplayName("при добавлении 1 задачи размер списка равен 1, задача добавлена")
    void shouldAddAndRemove1Task() {
        //подготовка данных
        Task task = new Task("Title", "Description", TaskStatus.NEW);

        //тестируем
        historyManager.add(task);

        //проверяем
        assertFalse(historyManager.history.isEmpty());
        assertEquals(1, historyManager.history.size());

        //снова тестируем
        historyManager.remove(task.getId());

        //снова проверяем
        assertTrue(historyManager.history.isEmpty());
    }

    @Test
    @DisplayName("при добавлении 10 задач размер списка равен 10")
    void shouldAdd10Tasks() {
        //подготовка данных
        Task task = new Task("Title", "Description", TaskStatus.NEW);

        //тестируем
        for (int i = 0; i < 10; i++) {
            historyManager.add(task.setId(i));
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
            historyManager.add(task.setId(i));
        }

        //проверяем
        assertEquals(13, historyManager.history.size());
    }

    @Test
    @DisplayName("получение списка просмотренных задач при вызове метода")
    void getHistory() {
        //подготовка данных
        Task task = new Task("Title", "Description", TaskStatus.NEW).setId(5);
        Subtask subtask = new Subtask("Title", "description", TaskStatus.IN_PROGRESS, 1).setId(6);
        Epic epic = new Epic("Title", "description").setId(1);
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
    @DisplayName("проверка неизменности задачи при добавлении еще одной задачи в менеджер")
    void shouldUnchangedPrevAddTask() {
        //подготовка данных
        Task task = new Task("Title", "Description", TaskStatus.NEW).setId(1);
        Subtask subtask = new Subtask("Title", "description", TaskStatus.IN_PROGRESS, 1).setId(2);
        //тестирование
        historyManager.add(task);
        historyManager.add(subtask);
        //проверка
        Task firstTaskInArrayList = historyManager.getHistory().getFirst();
        assertTasks(task, firstTaskInArrayList);
    }

    @Test
    @DisplayName("проверка уникальности при добавлении одинаковых задач и порядка перечисления")
    void shouldUniqueWhenAddTasks() {
        Task task = new Task("Title", "Description", TaskStatus.NEW).setId(1);
        Subtask subtask = new Subtask("Title", "description", TaskStatus.IN_PROGRESS, 3).setId(2);
        Epic epic = new Epic("Title", "description").setId(3);

        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.add(epic);
        List<Task> tasks = historyManager.getHistory();
        System.out.println("Порядок до повторных добавлений задач: \n" + tasks);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(task);

        assertEquals(3, historyManager.history.size());
        tasks = historyManager.getHistory();

        assertTasks(epic, tasks.get(0));
        assertTasks(subtask, tasks.get(1));
        assertTasks(task, tasks.get(2));

        System.out.println("Порядок после: \n" + tasks);

        historyManager.remove(epic.getId());

        assertEquals(2, historyManager.history.size());
        tasks = historyManager.getHistory();
        assertTasks(subtask, tasks.get(0));
        assertTasks(task, tasks.get(1));

        System.out.println("Порядок после удаления эпика на месте root: \n" + tasks);

        historyManager.remove(task.getId());
        assertEquals(1, historyManager.history.size());
        tasks = historyManager.getHistory();
        assertTasks(subtask, tasks.getFirst());

        System.out.println("Порядок после удаления задачи с конца: \n" + tasks);

        historyManager.remove(subtask.getId());
        assertTrue(historyManager.history.isEmpty());
    }

    private void assertTasks(Task task, Task task1) {
        assertEquals(task.getTitle(), task1.getTitle());
        assertEquals(task.getDescription(), task1.getDescription());
        assertEquals(task.getStatus(), task1.getStatus());
        assertEquals(task.getId(), task1.getId());
    }
}