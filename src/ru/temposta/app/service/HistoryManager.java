package ru.temposta.app.service;

import ru.temposta.app.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
