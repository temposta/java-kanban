package ru.temposta.app.service;

import ru.temposta.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        final Task item;
        Node prev;
        Node next;

        Node(Task item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    final Map<Integer, Node> history;
    private Node root;
    private Node last;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        addNew(task);
    }

    @Override
    public void remove(int id) {
        final Node old = history.remove(id);
        if (old == null) {
            return;
        }
        final Node next = old.next;
        final Node prev = old.prev;
        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
        }
        if (prev == null) {
            root = next;
        } else {
            prev.next = next;
        }
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>(history.size());
        Node current = root;
        while (current != null) {
            tasks.add(current.item);
            current = current.next;
        }
        return tasks;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "history=" + history +
                '}';
    }

    private void addNew(Task task) {
        Node newNode = new Node(task, last, null);
        if (root == null) {
            root = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
        history.put(task.getId(), newNode);
    }


}
