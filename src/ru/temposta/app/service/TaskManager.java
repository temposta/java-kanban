package ru.temposta.app.service;

import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

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

    //a.1 Получение списка всех задач.
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    //a.2 Получение списка всех Эпиков.
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    //a.3 Получение списка всех Подзадач.
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
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
        //чистим списки подзадач
        epics.values().forEach(epic -> {
            epic.setStatus(TaskStatus.NEW);
            epic.clearSubtasks();
        });
    }

    //c. Получение по идентификатору.
    public Task getAnyTaskById(int id) {
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
                Epic oldEpic = epics.get(epic.getId());
                oldEpic.setTitle(epic.getTitle());
                oldEpic.setDescription(epic.getDescription());
            }
            case Subtask subtask -> {
                Subtask oldSubtask = subtasks.get(subtask.getId());
                oldSubtask.setTitle(subtask.getTitle());
                oldSubtask.setDescription(subtask.getDescription());
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

    //Метод для выдачи очередного уникального идентификатора
    private int getNextID() {
        return ++counter;
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
        Epic parentEpic = epics.get(subtask.getParentEpicID());
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
            int numberDONEStatus = 0;

            for (Integer subtaskID : subtasksList) {
                TaskStatus taskStatus = subtasks.get(subtaskID).getStatus();

                switch (taskStatus) {
                    case NEW:
                        numberNEWStatus++;
                        break;
                    case DONE:
                        numberDONEStatus++;
                        break;
                    case IN_PROGRESS: {
                        //обеспечиваем ранний выход, если хоть одна подзадача в работе
                        epic.setStatus(TaskStatus.IN_PROGRESS);
                        return;
                    }
                }

                //обеспечиваем ранний выход, если есть задачи и NEW и DONE
                if ((numberNEWStatus > 0) && (numberDONEStatus > 0)) {
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                    return;
                }
            }
            if (numberNEWStatus > 0) epic.setStatus(TaskStatus.NEW);
            else epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }
}
