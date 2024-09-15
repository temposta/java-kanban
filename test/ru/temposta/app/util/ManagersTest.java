package ru.temposta.app.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.temposta.app.service.HistoryManager;
import ru.temposta.app.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Утилитарный класс")
class ManagersTest {

    @Test
    @DisplayName("инициализация ТаскМенеджера по умолчанию")
    void shouldGetDefault() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
    }

    @Test
    @DisplayName("инициализация Менеджера истории")
    void shouldGetDefaultHistoryManager() {
        HistoryManager manager = Managers.getDefaultHistoryManager();
        assertNotNull(manager);
    }
}