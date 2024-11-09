package ru.temposta.app.mappers;

import ru.temposta.app.model.*;

import java.util.Objects;
import java.util.function.Function;

public class Mapper {

    public Function<Task, String> toStr() {
        return Mapper::apply;
    }

    public Function<String[], Task> toObj() {
        return Mapper::apply;
    }

    private static Task apply(String[] s) {
        Objects.requireNonNull(s, "Строка не должна быть пустой");
        TaskType type = TaskType.valueOf(s[0]);
        switch (type) {
            case TASK:
                return new Task(s[2], s[3], TaskStatus.valueOf(s[4])).setId(Integer.parseInt(s[1]));
            case EPIC:
                Epic epic = new Epic(s[2], s[3]).setId(Integer.parseInt(s[1]));
                epic.setStatus(TaskStatus.valueOf(s[4]));
                return epic;
            case SUBTASK:
                int id = Integer.parseInt(s[1]);
                int parentEpicID = Integer.parseInt(s[5]);
                return new Subtask(s[2],
                        s[3],
                        TaskStatus.valueOf(s[4]),
                        parentEpicID).setId(id);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    private static String apply(Task task) {
        Objects.requireNonNull(task, "Передаваемый объект не должен быть равным null");
        TaskType type = task.getType();
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(type.name())
                .append(",").append(task.getId())
                .append(",").append(task.getTitle())
                .append(",").append(task.getDescription())
                .append(",").append(task.getStatus());
        if (type == TaskType.SUBTASK) {
            sb.append(",");
            Subtask subtask = (Subtask) task;
            sb.append(subtask.getParentEpicID());
        }
        return sb.toString();
    }
}
