package ru.temposta.app.service;

import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;

import java.util.ArrayList;

public interface TaskManager {
    //a.1 Получение списка всех задач.
    ArrayList<Task> getTasks();

    //a.2 Получение списка всех Эпиков.
    ArrayList<Epic> getEpics();

    //a.3 Получение списка всех Подзадач.
    ArrayList<Subtask> getSubtasks();

    //b.1 Удаление всех задач.
    void clearTasks();

    //b.2 Удаление всех задач - Эпиков.
    void clearEpics();

    //b.3 Удаление всех задач - Подзадач.
    void clearSubtasks();

    //c. Получение по идентификатору.
    Task getAnyTaskById(int id);

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    //Универсальный метод для добавления любого типа задачи
    Task addAnyTask(Task task);

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Task task);

    // f. Удаление по идентификатору.
    void removeTaskById(int id);
}
