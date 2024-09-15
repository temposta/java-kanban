package ru.temposta.app.util;

import ru.temposta.app.service.InMemoryHistoryManager;
import ru.temposta.app.service.InMemoryTaskManager;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistoryManager());
    }

    public static InMemoryHistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
