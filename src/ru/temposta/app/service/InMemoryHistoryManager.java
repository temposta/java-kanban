package ru.temposta.app.service;

import ru.temposta.app.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int historySize;
    List<Task> history;

    public InMemoryHistoryManager() {
        history = new LinkedList<>();
        historySize = 10;
    }

    //Конструктор с указанием размера истории просмотров
    public InMemoryHistoryManager(int historySize) {
        history = new LinkedList<>();
        this.historySize = historySize;
    }

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > historySize) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "history=" + history +
                '}';
    }
}
