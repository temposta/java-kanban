package ru.temposta.app.mappers;

import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.TaskStatus;
import ru.temposta.app.model.TaskType;

import java.util.Objects;
import java.util.function.Function;

public class SubtaskMapper {

    public Function<Subtask, String> toStr() {
        return (subtask) -> TaskType.SUBTASK +
                "," +
                subtask.getId() +
                "," +
                subtask.getTitle() +
                "," +
                subtask.getDescription() +
                "," +
                subtask.getStatus() +
                "," +
                subtask.getParentEpicID();
    }

    public Function<String[], Subtask> toObj() {
        return SubtaskMapper::apply;
    }

    private static Subtask apply(String[] split) {
        Objects.requireNonNull(split, "Строка не должна быть пустой");
        return new Subtask(split[2], split[3], TaskStatus.valueOf(split[4]), Integer.parseInt(split[5])).setId(Integer.parseInt(split[1]));
    }
}
