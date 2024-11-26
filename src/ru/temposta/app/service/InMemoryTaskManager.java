package ru.temposta.app.service;

import ru.temposta.app.exceptions.NotFoundException;
import ru.temposta.app.exceptions.ValidationException;
import ru.temposta.app.model.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected int counter = -1;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
        clearEntities(tasks);
    }

    //b.2 Удаление всех задач - Эпиков.
    @Override
    public void clearEpics() {
        clearEntities(epics);
        //Удалять Эпики без удаления подзадач не имеет смысла
        //поэтому удаляем и все подзадачи
        clearEntities(subtasks);
    }

    //b.3 Удаление всех задач - Подзадач.
    @Override
    public void clearSubtasks() {
        clearEntities(subtasks);
        //В связи с удалением подзадач, обновляем статусы Эпиков
        //чистим списки подзадач
        epics.values().forEach(epic -> {
            resetEpic(epic);
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
        if (task.isTakePriority()) {
            checkTimeIntersection(task);
            prioritizedTasks.add(task);
        }
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
                if (oldSubtask == null) throw new NotFoundException("Subtask id=" + subtask.getId() + " not found");
                if (subtask.isTakePriority()) {
                    checkTimeIntersection(oldSubtask);
                    prioritizedTasks.remove(oldSubtask);
                    prioritizedTasks.add(subtask);
                } else {
                    prioritizedTasks.remove(oldSubtask);
                }
                subtasks.put(subtask.getId(), subtask);
                updateEpic(epics.get(subtask.getParentEpicID()));
            }
            default -> {
                Task oldTask = tasks.get(task.getId());
                if (oldTask == null) throw new NotFoundException("Task id=" + task.getId() + " not found");
                if (task.isTakePriority()) {
                    checkTimeIntersection(task);
                    prioritizedTasks.remove(oldTask);
                    prioritizedTasks.add(task);
                } else {
                    //если в обновленной задаче снят учет приоритетности выполнения
                    //просто удаляем задачу из списка, если она существует
                    //isTakePriority() - false
                    prioritizedTasks.remove(oldTask);
                }
                tasks.put(task.getId(), task);
            }
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
        historyManager.remove(id);
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

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
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
            updateEpic(epic);
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
        prioritizedTasks.remove(getAnyTaskById(id));
        historyManager.remove(id);
        tasks.remove(id);
    }

    private void removeEpic(int id) {
        final Epic epic = epics.remove(id);
        historyManager.remove(id);
        List<Integer> subTasksList = epic.getSubTasksIDList();
        subTasksList.forEach(key -> {
            prioritizedTasks.remove(getAnyTaskById(key));
            subtasks.remove(key);
            historyManager.remove(key);
        });
    }

    private void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        prioritizedTasks.remove(subtask);
        Epic parentEpic = epics.get(subtask.getParentEpicID());
        parentEpic.removeSubtaskID(id);
        updateEpic(parentEpic);
        historyManager.remove(id);
    }

    private void updateEpic(Epic epic) {
        List<Integer> subtasksList = epic.getSubTasksIDList();
        //Обязательно проверяем наличие подзадач у Эпика, так как при удалении
        //всех подзадач, если Эпик был в статусе, отличном от NEW,
        //его необходимо вернуть в исходный статус (NEW)
        if (!subtasksList.isEmpty()) {
            int numberNewStatus = 0;
            int numberDoneStatus = 0;
            int numberInProgressStatus = 0;
            LocalDateTime start = LocalDateTime.MAX;
            LocalDateTime end = LocalDateTime.MIN;

            for (Integer subtaskID : subtasksList) {
                Subtask currentSubtask = subtasks.get(subtaskID);
                TaskStatus taskStatus = currentSubtask.getStatus();

                //Ни в коем случае не обеспечиваем ранний выход для обработки времен
                switch (taskStatus) {
                    case NEW -> numberNewStatus++;
                    case DONE -> numberDoneStatus++;
                    case IN_PROGRESS -> numberInProgressStatus++;
                }
                if (currentSubtask.getStartTime().isBefore(start)) start = currentSubtask.getStartTime();
                if (currentSubtask.getEndTime().isAfter(end)) end = currentSubtask.getEndTime();
            }

            if (numberInProgressStatus > 0) epic.setStatus(TaskStatus.IN_PROGRESS);
            else if (numberNewStatus == 0) epic.setStatus(TaskStatus.DONE);
            else if (numberDoneStatus == 0) epic.setStatus(TaskStatus.NEW);
            else epic.setStatus(TaskStatus.IN_PROGRESS);

            epic.setStartTime(start);
            epic.setEndTime(end);
            epic.setDuration((int) ChronoUnit.MINUTES.between(start, end));
        } else {
            resetEpic(epic);
        }
    }

    /* модификатор доступа "protected" для возможности тестирования логики */
    protected void checkTimeIntersection(Task task) {
        for (Task t : prioritizedTasks) {
            if (t.getId() == task.getId()) continue;

            LocalDateTime a = task.getStartTime();
            LocalDateTime b = task.getEndTime();
            LocalDateTime c = t.getStartTime();
            LocalDateTime d = t.getEndTime();

            if ((a.isBefore(c) && (b.isBefore(c) || b.isEqual(c)))
                    || ((a.isAfter(d) || a.isEqual(d)) && b.isAfter(d))) continue;

            throw new ValidationException("Пересечение с задачей: " + t);
        }
    }

    private static void resetEpic(Epic epic) {
        epic.setStatus(TaskStatus.NEW);
        epic.setStartTime(null);
        epic.setEndTime(null);
        epic.setDuration(0);
    }

    private <V extends Task> void clearEntities(Map<Integer, V> map) {
        map.forEach((key, value) -> {
            historyManager.remove(key);
            if (!value.getType().equals(TaskType.EPIC)) prioritizedTasks.remove(value);
        });
        map.clear();
    }
}