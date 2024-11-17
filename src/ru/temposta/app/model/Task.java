package ru.temposta.app.model;


import java.time.LocalDateTime;

public class Task {
    protected int id = -1;
    protected String title;
    protected String description;
    protected TaskStatus status;
    protected LocalDateTime startTime;
    protected int duration;
    protected boolean isTakePriority;

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = LocalDateTime.now();
        this.duration = 15;
        this.isTakePriority = false;
    }

    public Task(String title, String description, TaskStatus status, LocalDateTime startTime, int duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.isTakePriority = true;
    }

    public int getId() {
        return id;
    }

    public Task setId(int id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Integer getParentEpicID() {
        return null;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        this.isTakePriority = true;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public boolean isTakePriority() {
        return isTakePriority;
    }

    public Task setTakePriority(boolean takePriority) {
        isTakePriority = takePriority;
        return this;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + startTime.plusMinutes(duration) +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}