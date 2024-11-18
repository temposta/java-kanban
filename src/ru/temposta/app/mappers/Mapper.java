package ru.temposta.app.mappers;

import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;
import ru.temposta.app.model.TaskStatus;
import ru.temposta.app.model.TaskType;

import java.time.LocalDateTime;
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
                return new Task(s[2],
                        s[3],
                        TaskStatus.valueOf(s[4]),
                        LocalDateTime.parse(s[7]),
                        Integer.parseInt(s[6])
                ).setId(Integer.parseInt(s[1])).setTakePriority(Boolean.getBoolean(s[8]));
            case EPIC:
                Epic epic = new Epic(s[2], s[3]).setId(Integer.parseInt(s[1]));
                epic.setStatus(TaskStatus.valueOf(s[4]));
                epic.setDuration(Integer.parseInt(s[6]));
                String startTime = s[7];
                if (!startTime.equals("null")) epic.setStartTime(LocalDateTime.parse(s[7]));
                return epic.setTakePriority(Boolean.getBoolean(s[8]));
            case SUBTASK:
                int id = Integer.parseInt(s[1]);
                int parentEpicID = Integer.parseInt(s[5]);
                return new Subtask(s[2],
                        s[3],
                        TaskStatus.valueOf(s[4]),
                        parentEpicID,
                        LocalDateTime.parse(s[7]),
                        Integer.parseInt(s[6])
                ).setId(id).setTakePriority(Boolean.getBoolean(s[8]));
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
                .append(",").append(task.getStatus())
                .append(",").append(task.getParentEpicID())
                .append(",").append(task.getDuration())
                .append(",").append(task.getStartTime())
                .append(",").append(task.isTakePriority());
        return sb.toString();
    }
}
