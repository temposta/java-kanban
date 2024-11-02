package ru.temposta.app.mappers;

import ru.temposta.app.model.Epic;
import ru.temposta.app.model.TaskStatus;
import ru.temposta.app.model.TaskType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EpicMapper {

    public Function<Epic, String> toStr() {
        return (epic) -> TaskType.EPIC +
                "," +
                epic.getId() +
                "," +
                epic.getTitle() +
                "," +
                epic.getDescription() +
                "," +
                epic.getStatus() +
                "," +
                idsToString(epic.getSubTasksIDList());
    }

    public Function<String[], Epic> toObj() {
        return EpicMapper::apply;
    }

    private static Epic apply(String[] split) {
        Objects.requireNonNull(split, "Строка не должна быть пустой");
        Epic epic = new Epic(split[2], split[3])
                .setId(Integer.parseInt(split[1]));
        epic.setStatus(TaskStatus.valueOf(split[4]));
        if (split.length > 5) {
            String ids;
            ids = split[5];
            if (ids != null && !ids.isEmpty()) {
                if (ids.contains(":")) {
                    List<String> idsList = Arrays.stream(split[5].split(":")).toList();
                    for (String id : idsList) {
                        epic.getSubTasksIDList().add(Integer.parseInt(id));
                    }
                } else {
                    epic.getSubTasksIDList().add(Integer.parseInt(ids));
                }
            }
        }
        return epic;
    }

    private String idsToString(List<Integer> subTasksIDList) {
        Objects.requireNonNull(subTasksIDList, "Список не должен быть пустым");
        return subTasksIDList.stream().map(Object::toString).collect(Collectors.joining(":"));
    }
}
