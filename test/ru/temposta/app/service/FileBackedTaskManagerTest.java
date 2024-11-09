package ru.temposta.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Реализация таск менеджера с сохранением в файл")
class FileBackedTaskManagerTest {
    File file;
    TaskManager taskManager;

    @BeforeEach
    void setUp() {
        try {
            file = File.createTempFile("databaseTaskManager", "txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManager = new FileBackedTaskManager(file);

    }

    @Test
    @DisplayName("проверка загрузки из файла")
    void shouldLoadFromFile() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("""
                    task_type,id,title,description,task_status,parent_epic_id
                    TASK,0,Title1,Description1,NEW
                    EPIC,1,Title1,Description1,NEW
                    SUBTASK,2,Title1,Description1,NEW,1
                    #HISTORY#
                    2
                    1""");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManager = FileBackedTaskManager.loadFromFile(file);

        Task expectedTask = new Task("Title1", "Description1", TaskStatus.NEW).setId(0);
        List<Task> lt = taskManager.getTasks();
        assertEquals(lt.size(), 1);
        Task actualTask = lt.getFirst();
        assertEquals(expectedTask.getId(), actualTask.getId());
        assertEquals(expectedTask.getTitle(), actualTask.getTitle());
        assertEquals(expectedTask.getDescription(), actualTask.getDescription());
        assertEquals(expectedTask.getStatus(), actualTask.getStatus());

        Subtask expectedSubtask = new Subtask("Title1", "Description1", TaskStatus.NEW, 1).setId(2);
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(subtasks.size(), 1);
        Subtask actualSubtask = subtasks.getFirst();
        assertEquals(expectedSubtask.getId(), actualSubtask.getId());
        assertEquals(expectedSubtask.getTitle(), actualSubtask.getTitle());
        assertEquals(expectedSubtask.getDescription(), actualSubtask.getDescription());
        assertEquals(expectedSubtask.getStatus(), actualSubtask.getStatus());
        assertEquals(expectedSubtask.getParentEpicID(), actualSubtask.getParentEpicID());

        Epic expectedEpic = new Epic("Title1", "Description1").setId(1);
        expectedEpic.addSubtaskID(2);
        expectedEpic.setStatus(TaskStatus.NEW);
        List<Epic> epics = taskManager.getEpics();
        assertEquals(epics.size(), 1);
        Epic actualEpic = epics.getFirst();
        assertEquals(expectedEpic.getId(), actualEpic.getId());
        assertEquals(expectedEpic.getTitle(), actualEpic.getTitle());
        assertEquals(expectedEpic.getDescription(), actualEpic.getDescription());
        assertEquals(expectedEpic.getStatus(), actualEpic.getStatus());
        assertEquals(expectedEpic.getSubTasksIDList().getFirst(), actualEpic.getSubTasksIDList().getFirst());

        List<Task> his = taskManager.getHistory();
        assertEquals(his.size(), 2);
        Task actualHis = his.getFirst();
        assertEquals(1, actualHis.getId());
        actualHis = his.get(1);
        assertEquals(2, actualHis.getId());

    }

    @Test
    @DisplayName("проверка сохранения в файл")
    void shouldSaveToFile() {
        Task task1 = new Task("Title1", "Description1", TaskStatus.NEW);
        Epic epic1 = new Epic("Title1", "Description1");
        taskManager.addAnyTask(task1);
        taskManager.addAnyTask(epic1);
        Subtask subtask1 = new Subtask("Title1", "Description1", TaskStatus.NEW, epic1);
        taskManager.addAnyTask(subtask1);
        taskManager.getAnyTaskById(2);

        try (final FileReader in = new FileReader(file, StandardCharsets.UTF_8);
             final BufferedReader reader = new BufferedReader(in)) {
            String fileContent = reader.lines().collect(Collectors.joining("\n"));

            String expectedContent = """
                    task_type,id,title,description,task_status,parent_epic_id
                    TASK,0,Title1,Description1,NEW
                    EPIC,1,Title1,Description1,NEW
                    SUBTASK,2,Title1,Description1,NEW,1
                    #HISTORY#
                    2""";
            assertEquals(fileContent, expectedContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}