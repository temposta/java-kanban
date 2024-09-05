package ru.temposta.model;

public class Subtask extends Task {
    private final int parentEpicID;

    public Subtask(String title, String description, TaskStatus status, int parentEpicID) {
        super(title, description, status);
        this.parentEpicID = parentEpicID;
    }

    public Subtask(String title, String description, TaskStatus status, Epic parentEpic) {
        super(title, description, status);
        this.parentEpicID = parentEpic.getId();
    }

    public Subtask(Task task, Epic parentEpic) {
        super(task.title, task.description, task.status);
        this.parentEpicID = parentEpic.getId();
    }

    public int getParentEpicID() {
        return parentEpicID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "parentEpicID=" + parentEpicID +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
