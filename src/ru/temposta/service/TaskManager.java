package ru.temposta.service;

import ru.temposta.model.Epic;
import ru.temposta.model.Subtask;
import ru.temposta.model.Task;
import ru.temposta.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private static HashMap<Integer, Task> tasks;
    private static HashMap<Integer, Epic> epics;
    private static HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //a. Получение списка всех задач.
    public ArrayList<Object> getAllTasks() {
        ArrayList<Object> sumList = new ArrayList<>();
        sumList.addAll(tasks.values());
        sumList.addAll(epics.values());
        sumList.addAll(subtasks.values());
        return sumList;
    }

    //b. Удаление всех задач.
    public void clear() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
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
    public void addAnyTask(Object obj) {
        switch (obj) {
            case Epic epic -> addEpic(epic);
            case Subtask subtask -> addSubtask(subtask);
            case Task task -> addTask(task);
            case null, default -> System.out.println("Invalid task");
        }
    }

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Object object) {
        switch (object) {
            case Epic epic -> {
                epics.put(epic.getId(), epic);
                updateEpicStatus(epic);
            }
            case Subtask subtask -> {
                subtasks.put(subtask.getId(), subtask);
                updateEpicStatus(epics.get(subtask.getParentEpicID()));
            }
            case Task task -> tasks.put(task.getId(), task);
            default -> throw new IllegalStateException("Unexpected value: " + object);
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

    public TaskStatus getAnyTaskStatus(Object object) {
        switch (object) {
            case Epic epic -> {
                return epic.getStatus();
            }
            case Subtask subtask -> {
                return subtask.getStatus();
            }
            case Task task -> {
                return task.getStatus();
            }
            default -> throw new IllegalStateException("Unexpected value: " + object);
        }
    }

    private static void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    private static void addSubtask(Subtask subtask) {
        int subtaskID = subtask.getId();
        subtasks.put(subtaskID, subtask);
        Epic epic = epics.get(subtask.getParentEpicID());
        if (epic != null) {
            epic.addSubtaskID(subtaskID);
            updateEpicStatus(epic);
        } else {
            System.out.println("The specified ParentEpicID does not exist");
        }
    }

    private static void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    private void removeTask(int id) {
        tasks.remove(id);
    }

    private void removeEpic(int id) {
        Epic epic = (Epic) getAnyTaskById(id);
        ArrayList<Integer> subTasksList = epic.getSubTasksIDList();
        subTasksList.forEach(i -> subtasks.remove(i));
        epics.remove(id);
    }

    private void removeSubtask(int id) {
        Epic parentEpic = (Epic) getAnyTaskById(subtasks.get(id).getParentEpicID());
        parentEpic.removeSubtaskID(id);
        subtasks.remove(id);
        updateEpicStatus(parentEpic);
    }

    private static void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtasksList = epic.getSubTasksIDList();
        if (!subtasksList.isEmpty()) {
            int numberNEWStatus = 0;
            int numberDONEStatus = 0;
            int numberSubtasks = subtasksList.size();
            for (Integer subtaskID : subtasksList) {
                TaskStatus taskStatus = subtasks.get(subtaskID).getStatus();
                if (Objects.requireNonNull(taskStatus) == TaskStatus.NEW) numberNEWStatus++;
                else if (taskStatus == TaskStatus.DONE) numberDONEStatus++;
            }
            if (numberNEWStatus==numberSubtasks) epic.setStatus(TaskStatus.NEW);
            else if (numberDONEStatus==numberSubtasks) epic.setStatus(TaskStatus.DONE);
            else epic.setStatus(TaskStatus.IN_PROGRESS);
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
