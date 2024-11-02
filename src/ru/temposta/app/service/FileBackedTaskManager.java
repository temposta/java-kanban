package ru.temposta.app.service;

import ru.temposta.app.exceptions.ManagerSaveException;
import ru.temposta.app.mappers.EpicMapper;
import ru.temposta.app.mappers.SubtaskMapper;
import ru.temposta.app.mappers.TaskMapper;
import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;
import ru.temposta.app.util.Managers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTaskManager(File file) {
        this(Managers.getDefaultHistoryManager(), file);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.init(file);
        return manager;
    }

    public static void main(String[] args) {

        TaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("src/database/database.txt"));

        //Дополнительное задание спринт 6. Реализуем пользовательский сценарий
        Task task1 = new Task("Title1", "Description1", TaskStatus.NEW);
        Task task2 = new Task("Title2", "Description2", TaskStatus.NEW);
        Epic epic1 = new Epic("Title1", "Description1");
        Epic epic2 = new Epic("Title2", "Description2");
        taskManager.addAnyTask(task1);
        taskManager.addAnyTask(task2);
        taskManager.addAnyTask(epic1);
        taskManager.addAnyTask(epic2);
        Subtask subtask1 = new Subtask("Title1", "Description1", TaskStatus.NEW, epic1);
        Subtask subtask2 = new Subtask("Title2", "Description2", TaskStatus.NEW, epic1);
        Subtask subtask3 = new Subtask("Title3", "Description3", TaskStatus.NEW, epic1);
        taskManager.addAnyTask(subtask1);
        taskManager.addAnyTask(subtask2);
        taskManager.addAnyTask(subtask3);
        taskManager.getAnyTaskById(4);
        taskManager.getAnyTaskById(1);
        taskManager.getAnyTaskById(0);
        taskManager.getAnyTaskById(2);
        taskManager.getAnyTaskById(6);
        taskManager.getAnyTaskById(4);
        taskManager.getAnyTaskById(5);
        taskManager.getAnyTaskById(6);
    }

    public void init(File file) {
        try (final FileReader in = new FileReader(file, StandardCharsets.UTF_8);
             final BufferedReader reader = new BufferedReader(in)) {
            //final List<String> lines = reader.lines().toList();
            String line = reader.readLine();
            int maxID = -1;
            while (line != null && !line.equals("#HISTORY#") && !line.isBlank()) {
                String[] split = line.split(",");
                int id = Integer.parseInt(split[1]);
                if (id > maxID) maxID = id;
                switch (split[0]) {
                    case "TASK":
                        TaskMapper taskMapper = new TaskMapper();
                        Task t = taskMapper.toObj().apply(split);
                        tasks.put(t.getId(), t);
                        break;
                    case "SUBTASK":
                        SubtaskMapper subtaskMapper = new SubtaskMapper();
                        Subtask s = subtaskMapper.toObj().apply(split);
                        subtasks.put(s.getId(), s);
                        break;
                    case "EPIC":
                        EpicMapper epicMapper = new EpicMapper();
                        Epic e = epicMapper.toObj().apply(split);
                        epics.put(e.getId(), e);
                }
                line = reader.readLine();
            }
            counter = maxID;
            line = reader.readLine();
            while (line != null) {
                String[] split = line.split(",");
                switch (split[0]) {
                    case "TASK":
                        TaskMapper taskMapper = new TaskMapper();
                        Task t = taskMapper.toObj().apply(split);
                        historyManager.add(t);
                        break;
                    case "SUBTASK":
                        SubtaskMapper subtaskMapper = new SubtaskMapper();
                        Subtask s = subtaskMapper.toObj().apply(split);
                        historyManager.add(s);
                        break;
                    case "EPIC":
                        EpicMapper epicMapper = new EpicMapper();
                        Epic e = epicMapper.toObj().apply(split);
                        historyManager.add(e);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в файле: " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public Task addAnyTask(Task task) {
        Task t = super.addAnyTask(task);
        save();
        return t;
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public Task getAnyTaskById(int id) {
        Task t = super.getAnyTaskById(id);
        save();
        return t;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    private void save() {
        TaskMapper taskMapper = new TaskMapper();
        SubtaskMapper subtaskMapper = new SubtaskMapper();
        EpicMapper epicMapper = new EpicMapper();

        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("");
            saveTasks(writer, taskMapper);
            saveSubtasks(writer, subtaskMapper);
            saveEpics(writer, epicMapper);
            writer.append("#HISTORY#");
            writer.newLine();
            List<Task> history = historyManager.getHistory();
            for (Task task : history) {
                if (task instanceof Subtask) {
                    writer.append(subtaskMapper.toStr().apply((Subtask) task));
                    writer.newLine();
                } else if (task instanceof Epic) {
                    writer.append(epicMapper.toStr().apply((Epic) task));
                    writer.newLine();
                } else if (task != null) {
                    writer.append(taskMapper.toStr().apply(task));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в файле: " + file.getAbsolutePath(), e);
        }
    }

    private void saveEpics(BufferedWriter writer, EpicMapper epicMapper) throws IOException {
        for (Epic epic : super.epics.values()) {
            writer.append(epicMapper.toStr().apply(epic));
            writer.newLine();
        }
    }

    private void saveSubtasks(BufferedWriter writer, SubtaskMapper subtaskMapper) throws IOException {
        for (Subtask subtask : super.subtasks.values()) {
            writer.append(subtaskMapper.toStr().apply(subtask));
            writer.newLine();
        }
    }

    private void saveTasks(BufferedWriter writer, TaskMapper taskMapper) throws IOException {
        for (Task task : super.tasks.values()) {
            writer.append(taskMapper.toStr().apply(task));
            writer.newLine();
        }
    }
}
