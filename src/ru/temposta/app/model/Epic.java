package ru.temposta.app.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks;

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
        subTasks = new ArrayList<>();
    }

    public void addSubtaskID(int id) {
        subTasks.add(id);
    }

    public void removeSubtaskID(Integer id) {
        subTasks.remove(id);
    }

    public void clearSubtasks() {
        subTasks.clear();
    }

    //3) a. Получение списка всех подзадач определённого эпика.
    public List<Integer> getSubTasksIDList() {
        return subTasks;
    }

    @Override
    public Epic setId(int id) {
        return (Epic) super.setId(id);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
