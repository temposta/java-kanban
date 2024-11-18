package ru.temposta.app.model;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int parentEpicID;

    public Subtask(String title,
                   String description,
                   TaskStatus status,
                   int parentEpicID
    ) {
        super(title, description, status);
        this.parentEpicID = parentEpicID;
    }

    public Subtask(String title,
                   String description,
                   TaskStatus status,
                   Epic parentEpic
    ) {
        super(title, description, status);
        this.parentEpicID = parentEpic.getId();
    }

    public Subtask(String title,
                   String description,
                   TaskStatus status,
                   int parentEpicID,
                   LocalDateTime startTime,
                   int duration
    ) {
        super(title, description, status, startTime, duration);
        this.parentEpicID = parentEpicID;
    }

    @Override
    public Subtask setId(int id) {
        return (Subtask) super.setId(id);
    }

    @Override
    public Integer getParentEpicID() {
        return parentEpicID;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "parentEpicID=" + parentEpicID +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + startTime.plusMinutes(duration) +
                '}';
    }
}
