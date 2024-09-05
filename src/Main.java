import ru.temposta.model.Epic;
import ru.temposta.model.Subtask;
import ru.temposta.model.Task;
import ru.temposta.model.TaskStatus;
import ru.temposta.service.TaskManager;


public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        //Создайте две задачи, а также эпик с двумя подзадачами (делаем 3) и эпик с одной подзадачей.
        Task task1 = new Task("Cделать 1 коммит",
                "Наконец-то добраться до учебы и сделать первый коммит", TaskStatus.NEW);
        Task task2 = new Task("Cделать 2 коммит",
                "Наконец-то добраться до учебы и сделать первый коммит", TaskStatus.NEW);

        taskManager.addAnyTask(task1);
        taskManager.addAnyTask(task2);

        Epic epic = new Epic ("Ремонт", "Ремонт санузла");
        taskManager.addAnyTask(epic);
        //через 1 конструктор
        taskManager.addAnyTask(new Subtask("Ванна", "Заменить ванну", TaskStatus.NEW, epic.getId()));
        //через 2 конструктор
        taskManager.addAnyTask(new Subtask("Смесители", "Установить смесители", TaskStatus.NEW, epic));
        Task task = new Task("Зеркало", "Повесить зеркало", TaskStatus.NEW);
        //через 3 конструктор
        Subtask subtask3 = new Subtask(task, epic);
        taskManager.addAnyTask(subtask3);

        Epic epic2 = new Epic ("Спорт", "Провести несколько занятий на тренажерах");
        taskManager.addAnyTask(epic2);
        Subtask subtask2_1 = new Subtask("Беговая дорожка","Пробежать 10 км", TaskStatus.NEW, epic2);
        taskManager.addAnyTask(subtask2_1);

        //Распечатайте списки эпиков, задач и подзадач через System.out.println(..)
        //печать содержимого таскМенеджера
        System.out.println(taskManager);

        //Измените статусы созданных объектов, распечатайте их.
        // Проверьте, что статус задачи и подзадачи сохранился,
        // а статус эпика рассчитался по статусам подзадач.
        subtask3.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subtask3);
        subtask2_1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask2_1);
        Task task3 = (Task) taskManager.getAnyTaskById(0);
        task3.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task3);

        System.out.println(taskManager);

        //И, наконец, попробуйте удалить одну из задач и один из эпиков.
        taskManager.removeTaskById(1);
        taskManager.removeTaskById(2);

        System.out.println(taskManager);
    }
}
