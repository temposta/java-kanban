package ru.temposta.app;

import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;
import ru.temposta.app.service.TaskManager;
import ru.temposta.app.util.Managers;


public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

//        //Создайте две задачи, а также эпик с двумя подзадачами (делаем 3) и эпик с одной подзадачей.
//        Task addedTask;
//        addedTask = taskManager.addAnyTask(new Task("Cделать 1 коммит",
//                "Наконец-то добраться до учебы и сделать первый коммит", TaskStatus.NEW));
//        System.out.println("addedTask = " + addedTask);
//        System.out.println("taskManager = " + taskManager);
//
//        Task task2 = new Task("Cделать 2 коммит",
//                "Наконец-то добраться до учебы и сделать первый коммит", TaskStatus.NEW);
//        taskManager.addAnyTask(task2);
//        Epic epic1 = new Epic("Ремонт", "Ремонт санузла");
//        taskManager.addAnyTask(epic1);
//        Epic epic2 = new Epic("Спорт", "Провести несколько занятий на тренажерах");
//        taskManager.addAnyTask(epic2);
//        //через 1 конструктор
//        Subtask subtask1 = new Subtask("Ванна", "Заменить ванну", TaskStatus.NEW, epic1.getId());
//        taskManager.addAnyTask(subtask1);
//        //через 2 конструктор
//        Subtask subtask2 = new Subtask("Смесители", "Установить смесители", TaskStatus.NEW, epic1);
//        taskManager.addAnyTask(subtask2);
//        //через 3 конструктор
//        Task task = new Task("Зеркало", "Повесить зеркало", TaskStatus.NEW);
//        Subtask subtask3 = new Subtask(task, epic1);
//        taskManager.addAnyTask(subtask3);
//        Subtask subtask2_1 = new Subtask("Беговая дорожка", "Пробежать 10 км", TaskStatus.NEW, epic2);
//        taskManager.addAnyTask(subtask2_1);
//
//        //Распечатайте списки эпиков, задач и подзадач через System.out.println(..)
//        //печать содержимого таскМенеджера
//        System.out.println(taskManager);
//        System.out.println();
//
//        //Формируем историю просмотров задач
//        taskManager.getAnyTaskById(1);
//        taskManager.getAnyTaskById(3);
//        taskManager.getAnyTaskById(6);
//        taskManager.getAnyTaskById(0);
//
//        System.out.println("-------История просмотров:-----------------");
//        taskManager.getHistory().forEach(System.out::println);
//        System.out.println("-------Окончание истории просмотров:-------");
//
//        //Измените статусы созданных объектов, распечатайте их.
//        // Проверьте, что статус задачи и подзадачи сохранился,
//        // а статус эпика рассчитался по статусам подзадач.
//        subtask3.setStatus(TaskStatus.IN_PROGRESS);
//        taskManager.updateTask(subtask3);
//        subtask2_1.setStatus(TaskStatus.DONE);
//        taskManager.updateTask(subtask2_1);
//        Task task3 = taskManager.getAnyTaskById(0);
//        task3.setStatus(TaskStatus.DONE);
//        taskManager.updateTask(task3);
//
//        System.out.println(taskManager);
//
//        //И, наконец, попробуйте удалить одну из задач и один из эпиков.
//        taskManager.removeTaskById(1);
//        taskManager.removeTaskById(2);
//
//        System.out.println(taskManager);
//
//        //Здесь удаляем подзадачу, итого подзадач у Эпика станет - 0 -
//        // проверяем возврат статуса Эпика в исходный из DONE в NEW
//        taskManager.removeTaskById(7);
//
//        System.out.println(taskManager);

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
    }
}
