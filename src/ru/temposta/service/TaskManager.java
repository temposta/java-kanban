package ru.temposta.service;

import ru.temposta.model.Epic;
import ru.temposta.model.Subtask;
import ru.temposta.model.Task;
import ru.temposta.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private int counter = -1;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //Метод для выдачи очередного уникального идентификатора
    private int getNextID() {
        return ++counter;
    }

    //a.1 Получение списка всех задач.
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    //a.2 Получение списка всех Эпиков.
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    //a.3 Получение списка всех Подзадач.
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    //b.1 Удаление всех задач.
    public void clearTasks() {
        tasks.clear();
    }

    //b.2 Удаление всех задач - Эпиков.
    public void clearEpics() {
        epics.clear();
        //Удалять Эпики без удаления подзадач не имеет смысла
        //поэтому удаляем и все подзадачи
        subtasks.clear();
    }

    //b.3 Удаление всех задач - Подзадач.
    public void clearSubtasks() {
        subtasks.clear();
        //В связи с удалением подзадач, обновляем статусы Эпиков
        epics.values().forEach(epic -> epic.setStatus(TaskStatus.NEW));
    }

    //c. Получение по идентификатору.
    public Object getAnyTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) return task;
        Epic epic = epics.get(id);
        if (epic != null) return epic;
        return subtasks.get(id);
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    //Универсальный метод для добавления любого типа задачи
    public void addAnyTask(Task task) {
        switch (task) {
            case Epic epic -> addEpic(epic);
            case Subtask subtask -> addSubtask(subtask);
            default -> addTask(task);
        }
    }

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task) {
        switch (task) {
            case Epic epic -> {
                epics.put(epic.getId(), epic);
                updateEpicStatus(epic);
            }
            case Subtask subtask -> {
                subtasks.put(subtask.getId(), subtask);
                updateEpicStatus(epics.get(subtask.getParentEpicID()));
            }
            default -> tasks.put(task.getId(), task);
        }

    }

    // f. Удаление по идентификатору.
    public void removeTaskById(int id) {
        if (tasks.get(id) != null) {
            removeTask(id);
        } else if (epics.get(id) != null) {
            removeEpic(id);
        } else if (subtasks.get(id) != null) {
            removeSubtask(id);
        }
    }

    private void addEpic(Epic epic) {
        int id = getNextID();
        epics.put(id, epic.setId(id));
    }

    private void addSubtask(Subtask subtask) {
        int id = getNextID();
        subtasks.put(id, subtask.setId(id));
        Epic epic = epics.get(subtask.getParentEpicID());
        if (epic != null) {
            epic.addSubtaskID(id);
            updateEpicStatus(epic);
        } else {
            System.out.println("The specified ParentEpicID does not exist");
        }
    }

    private void addTask(Task task) {
        int id = getNextID();
        tasks.put(id, task.setId(id));
    }

    private void removeTask(int id) {
        tasks.remove(id);
    }

    private void removeEpic(int id) {
        final Epic epic = epics.remove(id);
        ArrayList<Integer> subTasksList = epic.getSubTasksIDList();
        subTasksList.forEach(subtasks::remove);
    }

    private void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        Epic parentEpic = (Epic) getAnyTaskById(subtask.getParentEpicID());
        parentEpic.removeSubtaskID(id);
        updateEpicStatus(parentEpic);
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtasksList = epic.getSubTasksIDList();
        //Обязательно проверяем наличие подзадач у Эпика, так как при удалении
        //всех подзадач, если Эпик был в статусе, отличном от NEW,
        //его необходимо вернуть в исходный статус (NEW)
        if (!subtasksList.isEmpty()) {
            int numberNEWStatus = 0;

            for (Integer subtaskID : subtasksList) {
                TaskStatus taskStatus = subtasks.get(subtaskID).getStatus();

                //обеспечиваем ранний выход, если хоть одна подзадача в работе
                if (taskStatus == TaskStatus.IN_PROGRESS) {
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                    return;
                }

                if (Objects.requireNonNull(taskStatus) == TaskStatus.NEW) numberNEWStatus++;
            }

            if (numberNEWStatus > 0) epic.setStatus(TaskStatus.NEW);
            else epic.setStatus(TaskStatus.DONE);

        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("TaskManager{\nTasks:\n");
        for (Task task : tasks.values()) result.append(task.toString()).append("\n");
        result.append("Epics:\n");
        for (Epic epic : epics.values()) result.append(epic.toString()).append("\n");
        result.append("Subtasks:\n");
        for (Subtask subtask : subtasks.values()) result.append(subtask.toString()).append("\n");
        result.append("}");
        return result.toString();
    }
}