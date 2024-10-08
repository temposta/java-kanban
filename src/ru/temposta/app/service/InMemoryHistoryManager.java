package ru.temposta.app.service;

import ru.temposta.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task item;
        Node prev;
        Node next;

        Node(Task item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    Map<Integer, Node> history;
    Node root;
    Node current;
    Node last;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
        root = new Node(null, null, null);
        current = root;
        last = root;
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        if (history.isEmpty()) {
            addFirst(task);
            return;
        }
        addLast(task);
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            Node old = history.remove(id);
            if (history.isEmpty()) {
                root = last = current = null;
                return;
            }
            if (history.size() == 1) {
                if (old == root) {
                    last.prev = null;
                    root = last;
                } else {
                    root.next = null;
                    last = root;
                }
                return;
            }
            if (old == root) {
                root = old.next;
                root.prev = null;
                return;
            }
            if (old == last) {
                last = old.prev;
                last.next = null;
                return;
            }

            old.prev.next = old.next;
            old.next.prev = old.prev;
        }
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        current = root;
        while (current != null) {
            tasks.add(current.item);
            current = current.next;
        }
        return List.copyOf(tasks);
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "history=" + history +
                '}';
    }

    private void addLast(Task task) {
        current = new Node(task, last, null);
        last.next = current;
        last = current;
        history.put(task.getId(), current);
    }

    private void addFirst(Task task) {
        current = new Node(task, null, null);
        root = last = current;
        history.put(task.getId(), current);
    }
}
