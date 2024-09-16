package ru.temposta.app.service;

import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int counter = -1;
    final HashMap<Integer, Task> tasks;
    final HashMap<Integer, Epic> epics;
    final HashMap<Integer, Subtask> subtasks;
    final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    //a.1 Получение списка всех задач.
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    //a.2 Получение списка всех Эпиков.
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    //a.3 Получение списка всех Подзадач.
    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    //b.1 Удаление всех задач.
    @Override
    public void clearTasks() {
        tasks.clear();
    }

    //b.2 Удаление всех задач - Эпиков.
    @Override
    public void clearEpics() {
        epics.clear();
        //Удалять Эпики без удаления подзадач не имеет смысла
        //поэтому удаляем и все подзадачи
        subtasks.clear();
    }

    //b.3 Удаление всех задач - Подзадач.
    @Override
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
    @Override
    public Task getAnyTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        }
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        }
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    //Универсальный метод для добавления любого типа задачи
    @Override
    public Task addAnyTask(Task task) {
        Task result;
        switch (task) {
            case Epic epic -> result = addEpic(epic);
            case Subtask subtask -> result = addSubtask(subtask);
            default -> result = addTask(task);
        }
        return result;
    }

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
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
                oldSubtask.setStatus(subtask.getStatus());
                updateEpicStatus(epics.get(subtask.getParentEpicID()));
            }
            default -> tasks.put(task.getId(), task);
        }

    }

    // f. Удаление по идентификатору.
    @Override
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Метод для выдачи очередного уникального идентификатора
    private int getNextID() {
        return ++counter;
    }

    private Epic addEpic(Epic epic) {
        int id = getNextID();
        epics.put(id, epic.setId(id));
        return epics.get(id);
    }

    private Subtask addSubtask(Subtask subtask) {
        int id = getNextID();
        Epic epic = epics.get(subtask.getParentEpicID());
        if (epic != null) {
            epic.addSubtaskID(id);
            subtasks.put(id, subtask.setId(id));
            updateEpicStatus(epic);
        } else {
            System.out.println("The specified ParentEpicID does not exist");
            return null;
        }
        return subtasks.get(id);
    }

    private Task addTask(Task task) {
        int id = getNextID();
        tasks.put(id, task.setId(id));
        return tasks.get(id);

    }

    private void removeTask(int id) {
        tasks.remove(id);
    }

    private void removeEpic(int id) {
        final Epic epic = epics.remove(id);
        List<Integer> subTasksList = epic.getSubTasksIDList();
        subTasksList.forEach(subtasks::remove);
    }

    private void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        Epic parentEpic = epics.get(subtask.getParentEpicID());
        parentEpic.removeSubtaskID(id);
        updateEpicStatus(parentEpic);
    }

    private void updateEpicStatus(Epic epic) {
        List<Integer> subtasksList = epic.getSubTasksIDList();
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