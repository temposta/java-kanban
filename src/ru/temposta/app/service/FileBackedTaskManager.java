package ru.temposta.app.service;

import ru.temposta.app.exceptions.ManagerSaveException;
import ru.temposta.app.mappers.Mapper;
import ru.temposta.app.model.*;
import ru.temposta.app.util.Managers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String HEADER_OF_FILE = "task_type,id,title,description,task_status,parent_epic_id";
    private final File file;

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
            reader.readLine();
            String line = reader.readLine();
            int maxID = -1;
            Mapper mapper = new Mapper();
            while (line != null && !line.equals("#HISTORY#") && !line.isBlank()) {
                String[] split = line.split(",");
                int id = Integer.parseInt(split[1]);
                if (id > maxID) maxID = id;
                switch (TaskType.valueOf(split[0])) {
                    case TASK:
                        Task t = mapper.toObj().apply(split);
                        tasks.put(t.getId(), t);
                        break;
                    case SUBTASK:
                        Subtask s = (Subtask) mapper.toObj().apply(split);
                        subtasks.put(s.getId(), s);
                        break;
                    case EPIC:
                        Epic e = (Epic) mapper.toObj().apply(split);
                        epics.put(e.getId(), e);
                }
                line = reader.readLine();
            }
            counter = maxID;
            line = reader.readLine();
            while (line != null) {
                int id = Integer.parseInt(line);
                historyManager.add(getAnyTaskById(id));
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в файле: " + file.getAbsolutePath(), e);
        }
        subtasks.forEach((key, subtask) -> {
            Epic parentEpic = epics.get(subtask.getParentEpicID());
            parentEpic.addSubtaskID(key);
        });
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
        Mapper mapper = new Mapper();

        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(HEADER_OF_FILE);
            writer.newLine();
            saveTasks(writer, mapper);
            saveEpics(writer, mapper);
            saveSubtasks(writer, mapper);
            writer.append("#HISTORY#");
            writer.newLine();
            List<Task> history = historyManager.getHistory();
            for (Task task : history) {
                writer.append(String.valueOf(task.getId()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в файле: " + file.getAbsolutePath(), e);
        }
    }

    private void saveEpics(BufferedWriter writer, Mapper mapper) throws IOException {
        for (Epic epic : epics.values()) {
            writer.append(mapper.toStr().apply(epic));
            writer.newLine();
        }
    }

    private void saveSubtasks(BufferedWriter writer, Mapper mapper) throws IOException {
        for (Subtask subtask : subtasks.values()) {
            writer.append(mapper.toStr().apply(subtask));
            writer.newLine();
        }
    }

    private void saveTasks(BufferedWriter writer, Mapper mapper) throws IOException {
        for (Task task : tasks.values()) {
            writer.append(mapper.toStr().apply(task));
            writer.newLine();
        }
    }
}
