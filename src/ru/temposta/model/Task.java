package ru.temposta.model;

public class Task {
    protected static int counter = -1;
    protected int id;
    protected String title;
    protected String description;
    protected TaskStatus status;

    public Task(String title, String description, TaskStatus status) {
        this.id = ++counter;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    //Метод необходим при обновлении задач, чтоб сохранить id старого объекта
    //При создании новых задач, эпиков, подзадач - не используется
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public TaskStatus getStatus() {
        return this.status;
    }
}