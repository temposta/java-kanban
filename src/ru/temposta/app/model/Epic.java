package ru.temposta.app.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks;
    protected LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
        subTasks = new ArrayList<>();
        startTime = null;
        endTime = null;
        duration = 0;
        isTakePriority = false;
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
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                '}';
    }
}
