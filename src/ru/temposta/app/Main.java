package ru.temposta.app;

import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;
import ru.temposta.app.service.TaskManager;
import ru.temposta.app.util.Managers;

import java.util.List;


public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        //Дополнительное задание спринт 6. Реализуем пользовательский сценарий
        //1. Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач.
        Task task1 = new Task("Title1", "Description1", TaskStatus.NEW);
        Task task2 = new Task("Title2", "Description2", TaskStatus.NEW);
        Epic epic1 = new Epic("Title1", "Description1");
        Epic epic2 = new Epic("Title2", "Description2");
        taskManager.addAnyTask(task1);
        taskManager.addAnyTask(task2);
        taskManager.addAnyTask(epic1);
        taskManager.addAnyTask(epic2);
        Subtask subtask1 = new Subtask("Title1", "Description1", TaskStatus.NEW, epic1);
        Subtask subtask2 = new Subtask("Title2", "Description2", TaskStatus.NEW, epic1);
        Subtask subtask3 = new Subtask("Title3", "Description3", TaskStatus.NEW, epic1);
        taskManager.addAnyTask(subtask1);
        taskManager.addAnyTask(subtask2);
        taskManager.addAnyTask(subtask3);
        //2. Запросите созданные задачи несколько раз в разном порядке.
        //3. После каждого запроса выведите историю и убедитесь, что в ней нет повторов.
        taskManager.getAnyTaskById(4);
        System.out.println("taskManager.getHistory() = " + taskManager.getHistory());
        taskManager.getAnyTaskById(1);
        System.out.println("taskManager.getHistory() = " + taskManager.getHistory());
        taskManager.getAnyTaskById(0);
        System.out.println("taskManager.getHistory() = " + taskManager.getHistory());
        taskManager.getAnyTaskById(2);
        System.out.println("taskManager.getHistory() = " + taskManager.getHistory());
        taskManager.getAnyTaskById(6);
        System.out.println("taskManager.getHistory() = " + taskManager.getHistory());
        taskManager.getAnyTaskById(4);
        System.out.println("taskManager.getHistory() = " + taskManager.getHistory());
        taskManager.getAnyTaskById(5);
        System.out.println("taskManager.getHistory() = " + taskManager.getHistory());
        taskManager.getAnyTaskById(6);
        System.out.println("taskManager.getHistory() = " + taskManager.getHistory());
        //4. Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться.
        taskManager.removeTaskById(subtask3.getId());
        System.out.println("taskManager.getHistory() = " + taskManager.getHistory());
        //5. Удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.
        taskManager.removeTaskById(epic1.getId());
        System.out.println("taskManager.getHistory() = " + taskManager.getHistory());

        //тестируем доступность изменения истории в рамках исследований кода
        List<Task> history = taskManager.getHistory();
        System.out.println("history = " + history);
        Task t = history.getFirst();
        t.setDescription("sdfs");
        System.out.println("history = " + history);
        List<Task> history1 = taskManager.getHistory();
        System.out.println("history1 = " + history1);
    }
}
