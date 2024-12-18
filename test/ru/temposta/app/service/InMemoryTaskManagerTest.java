package ru.temposta.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.temposta.app.exceptions.ValidationException;
import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Реализация таск менеджера")
class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager;
    Task task;
    Task task2;
    Task task3;
    Subtask subtask;
    Subtask subtask2;
    Subtask subtask3;
    Epic epic;
    Epic epic2;
    Epic epic3;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(new HistoryManager() {
            @Override
            public void add(Task task) {

            }

            @Override
            public void remove(int id) {

            }

            @Override
            public List<Task> getHistory() {
                return List.of();
            }
        });

        task = taskManager.addAnyTask(new Task("Title1", "Description1", TaskStatus.NEW));
        task2 = taskManager.addAnyTask(new Task("Title2", "Description2", TaskStatus.NEW));

        epic = (Epic) taskManager.addAnyTask(new Epic("Title", "Description"));
        epic2 = (Epic) taskManager.addAnyTask(new Epic("Title2", "Description2"));

        subtask = (Subtask) taskManager.addAnyTask(new Subtask("Title1", "Description1",
                TaskStatus.NEW, epic.getId()));
        subtask2 = (Subtask) taskManager.addAnyTask(new Subtask("Title2", "Description2",
                TaskStatus.NEW, epic2.getId()));

        task3 = new Task("Title3", "Description3", TaskStatus.NEW);
        epic3 = new Epic("Title3", "Description3");
        subtask3 = new Subtask("Title3", "Description3", TaskStatus.NEW, epic2.getId());
    }


    @Test
    @DisplayName("проверка получения списка задач")
    void shouldGetTasks() {
        List<Task> tasks = taskManager.getTasks();
        assertArrayEquals(tasks.toArray(), taskManager.tasks.values().toArray());
    }

    @Test
    @DisplayName("проверка получения списка Эпиков")
    void shouldGetEpics() {
        List<Epic> epics = taskManager.getEpics();
        assertArrayEquals(epics.toArray(), taskManager.epics.values().toArray());
    }

    @Test
    @DisplayName("проверка получения списка подзадач")
    void shouldGetSubtasks() {
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertArrayEquals(subtasks.toArray(), taskManager.subtasks.values().toArray());
    }

    @Test
    @DisplayName("проверка очистки массива задач")
    void shouldClearTasks() {
        //тестирование
        taskManager.clearTasks();
        //проверка
        assertTrue(taskManager.tasks.isEmpty());
    }

    @Test
    @DisplayName("проверка очистки массива Эпиков и массива подзадач")
    void shouldClearEpics() {
        taskManager.clearEpics();
        assertTrue(taskManager.epics.isEmpty());
        assertTrue(taskManager.subtasks.isEmpty());
    }

    @Test
    @DisplayName("проверка очистки массива подзадач")
    void shouldClearSubtasks() {
        taskManager.clearSubtasks();
        assertTrue(taskManager.subtasks.isEmpty());
    }

    @Test
    @DisplayName("получение задачи по ID")
    void getAnyTaskById() {
        Task result = taskManager.getAnyTaskById(task.getId());
        assertEquals(taskManager.tasks.get(task.getId()), result);
    }

    @Test
    @DisplayName("проверка, что ТаскМенеджер действительно добавляет задачи разных типов")
    void shouldAddAnyTask() {
        int tasksSize = taskManager.tasks.size();
        int subtasksSize = taskManager.subtasks.size();
        int epicsSize = taskManager.epics.size();
        taskManager.addAnyTask(task3);
        taskManager.addAnyTask(subtask3);
        taskManager.addAnyTask(epic3);
        int newTasksSize = taskManager.tasks.size();
        int newSubtasksSize = taskManager.subtasks.size();
        int newEpicsSize = taskManager.epics.size();
        assertEquals(tasksSize + 1, newTasksSize);
        assertEquals(subtasksSize + 1, newSubtasksSize);
        assertEquals(epicsSize + 1, newEpicsSize);
    }

    @Test
    @DisplayName("проверка обновления статуса Эпика при обновлении/добавлении/удалении его подзадач")
    void shouldUpdateEpicStatusWhenAddDeleteUpdateSubtasks() {
        //проверка исходного состояния
        assertEquals(epic.getStatus(), TaskStatus.NEW);
        //подготовка
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        //тест
        taskManager.updateTask(subtask);
        //проверка
        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS);
        //подготовка
        subtask.setStatus(TaskStatus.DONE);
        //тест
        taskManager.updateTask(subtask);
        //проверка
        assertEquals(epic.getStatus(), TaskStatus.DONE);
        //подготовка
        Subtask subtask5 = (Subtask) taskManager.addAnyTask(new Subtask("Title5", "Description5",
                TaskStatus.NEW, epic.getId()));
        //тест
        taskManager.addAnyTask(subtask5);
        //проверка
        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS);
        //тест
        taskManager.removeTaskById(subtask.getId());
        taskManager.removeTaskById(subtask5.getId());
        //проверка
        assertEquals(epic.getStatus(), TaskStatus.NEW);

    }

    @Test
    @DisplayName("проверка удаления задач")
    void shouldRemoveTask() {
        taskManager.removeTaskById(task.getId());
        assertEquals(taskManager.tasks.size(), 1);
        taskManager.removeTaskById(task2.getId());
        assertTrue(taskManager.tasks.isEmpty());
    }

    @Test
    @DisplayName("проверка удаления Эпика, при этом каскадное удаление связанной задачи")
    void shouldRemoveEpic() {
        int subtasksSize = taskManager.subtasks.size();
        taskManager.removeTaskById(epic.getId());
        assertEquals(taskManager.epics.size(), 1);
        assertEquals(taskManager.subtasks.size(), subtasksSize - 1);
    }

    @Test
    @DisplayName("проверка логики теста на пересечение задач")
    void shouldChecksValidIntersection() {
        taskManager.clearEpics();
        taskManager.clearTasks();
        task.setStartTime(LocalDateTime.of(2024, 11, 10, 10, 0));
        task.setDuration(20);
        task2.setStartTime(LocalDateTime.of(2024, 11, 10, 10, 21));
        task2.setDuration(9);
        taskManager.prioritizedTasks.add(task);
        taskManager.prioritizedTasks.add(task2);
        task3.setStartTime(LocalDateTime.of(2024, 11, 10, 9, 50));
        task3.setDuration(9);
        assertDoesNotThrow(() -> taskManager.checkTimeIntersection(task3));
        task3.setStartTime(LocalDateTime.of(2024, 11, 10, 10, 20));
        task3.setDuration(1);
        assertDoesNotThrow(() -> taskManager.checkTimeIntersection(task3));
        task3.setStartTime(LocalDateTime.of(2024, 11, 10, 10, 50));
        assertDoesNotThrow(() -> taskManager.checkTimeIntersection(task3));
        task3.setStartTime(LocalDateTime.of(2024, 11, 10, 10, 5));
        task3.setDuration(5);
        assertThrows(ValidationException.class, () -> taskManager.checkTimeIntersection(task3));
        task3.setStartTime(LocalDateTime.of(2024, 11, 10, 10, 10));
        task3.setDuration(15);
        assertThrows(ValidationException.class, () -> taskManager.checkTimeIntersection(task3));
    }

    @Test
    @DisplayName("Проверка отсутствия в списке приоритета при добавлении задач без приоритета")
    void shouldCorrectNotPrioritizedTasks() {
        taskManager.clearEpics();
        taskManager.clearTasks();
        //Проверка пустого TreeSet при добавлении задачи без приоритета
        task = taskManager.addAnyTask(new Task("Title1", "Description1", TaskStatus.NEW));
        taskManager.addAnyTask(task);
        assertTrue(taskManager.prioritizedTasks.isEmpty());
        epic = (Epic) taskManager.addAnyTask(new Epic("Title", "Description"));
        taskManager.addAnyTask(epic);
        assertTrue(taskManager.prioritizedTasks.isEmpty());
        subtask = (Subtask) taskManager.addAnyTask(new Subtask("Title1", "Description1",
                TaskStatus.NEW, epic.getId()));
        taskManager.addAnyTask(subtask);
        assertTrue(taskManager.prioritizedTasks.isEmpty());
    }

    @Test
    @DisplayName("Проверка приоритета при добавлении задач c приоритетом")
    void shouldCorrectPrioritizedTasks() {
        taskManager.clearEpics();
        taskManager.clearTasks();
        //Проверка пустого TreeSet при добавлении задач c приоритетом
        task = taskManager.addAnyTask(new Task("Title1", "Description1", TaskStatus.NEW));
        task.setStartTime(LocalDateTime.parse("2024-03-26T09:30"));
        task.setDuration(30);
        taskManager.addAnyTask(task);
        assertEquals(taskManager.prioritizedTasks.size(), 1);

        epic = (Epic) taskManager.addAnyTask(new Epic("Title", "Description"));
        taskManager.addAnyTask(epic);
        System.out.println("taskManager.getPrioritizedTasks() = " + taskManager.getPrioritizedTasks());
        assertEquals(taskManager.prioritizedTasks.size(), 1);
        subtask = (Subtask) taskManager.addAnyTask(new Subtask("Title1", "Description1",
                TaskStatus.NEW, epic.getId()));
        subtask.setStartTime(LocalDateTime.parse("2024-03-25T10:30"));
        subtask.setDuration(30);
        taskManager.addAnyTask(subtask);
        //проверям наличие подзадачи и отсутствие эпика
        assertEquals(taskManager.prioritizedTasks.size(), 2);
        //проверяем, что у подзадачи выше приоритет
        assertEquals(taskManager.prioritizedTasks.getFirst().getId(), subtask.getId());
        System.out.println("taskManager.getPrioritizedTasks() = " + taskManager.getPrioritizedTasks());
    }


}