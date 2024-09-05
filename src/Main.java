import ru.temposta.model.Epic;
import ru.temposta.model.Subtask;
import ru.temposta.model.Task;
import ru.temposta.model.TaskStatus;
import ru.temposta.service.TaskManager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        taskManager.addTask(new Task ("Просто", "Положить на все", TaskStatus.DONE));
        taskManager.addTask(new Epic("Ремонт", "Ремонт санузла"));
        taskManager.addTask(new Subtask("Ванна", "Заменить ванну", TaskStatus.NEW, 1));
        taskManager.addTask(new Subtask("Смесители", "Установить смесители", TaskStatus.NEW, 1));
        taskManager.addTask(new Subtask("Зеркало", "Повесить зеркало", TaskStatus.NEW, 1));
        System.out.println("taskManager = " + taskManager);


        Subtask st = new Subtask("Зеркало", "Повесить зеркало", TaskStatus.DONE, 1);
        st.setId(4);
        taskManager.updateTask(st);
        System.out.println("taskManager = " + taskManager);
        Object task = taskManager.getTaskById(0);
        System.out.println("task = " + task);

        System.out.println("taskManager.getTaskStatus(taskManager.getTaskById(0)) = " + taskManager.getTaskStatus(task));
        System.out.println("taskManager.getTaskStatus(taskManager.getTaskById(1)) = " + taskManager.getTaskStatus(taskManager.getTaskById(1)));
        System.out.println("taskManager.getTaskStatus(taskManager.getTaskById(2)) = " + taskManager.getTaskStatus(taskManager.getTaskById(2)));
        System.out.println("taskManager.getTaskStatus(taskManager.getTaskById(3)) = " + taskManager.getTaskStatus(taskManager.getTaskById(3)));
        System.out.println("taskManager.getTaskStatus(taskManager.getTaskById(4)) = " + taskManager.getTaskStatus(taskManager.getTaskById(4)));



        System.out.println("taskManager.getTaskById(0) = " + taskManager.getTaskById(0).toString());
        System.out.println("taskManager.getTaskById(1) = " + taskManager.getTaskById(1).toString());
        System.out.println("taskManager.getTaskById(2) = " + taskManager.getTaskById(2).toString());
        Object object = taskManager.getTaskById(5);
        System.out.println("taskManager.getTaskById(5) = " + object);
        ArrayList<Object> tasks = taskManager.getTasks();
        System.out.println("tasks = " + tasks);

        taskManager.removeTaskById(0);
        taskManager.removeTaskById(4);
        System.out.println("taskManager = " + taskManager);
        //taskManager.removeTask(2);

        taskManager.clearAllTasks();
        System.out.println("taskManager = " + taskManager);









    }
}
