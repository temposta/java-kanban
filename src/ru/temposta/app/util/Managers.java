package ru.temposta.app.util;

import ru.temposta.app.service.InMemoryTaskManager;
import ru.temposta.app.service.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
