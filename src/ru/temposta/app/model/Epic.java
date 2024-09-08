package ru.temposta.app.model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTasks;

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

    //3) a. Получение списка всех подзадач определённого эпика.
    public ArrayList<Integer> getSubTasksIDList() {
        return subTasks;
    }

    @Override
    public Epic setId(int id) {
        return (Epic) super.setId(id);
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
