package ru.temposta.app.mappers;

import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;
import ru.temposta.app.model.TaskType;

import java.util.Objects;
import java.util.function.Function;

public class TaskMapper {

    public Function<Task, String> toStr() {
        return (task) -> TaskType.TASK +
                "," +
                task.getId() +
                "," +
                task.getTitle() +
                "," +
                task.getDescription() +
                "," +
                task.getStatus();
    }

    public Function<String[], Task> toObj() {
        return TaskMapper::apply;
    }

    private static Task apply(String[] split) {
        Objects.requireNonNull(split, "Строка не должна быть пустой");
        return new Task(split[2], split[3], TaskStatus.valueOf(split[4])).setId(Integer.parseInt(split[1]));
    }
}
