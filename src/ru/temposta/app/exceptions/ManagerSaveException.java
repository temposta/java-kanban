package ru.temposta.app.exceptions;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, IOException e) {
        super(message, e);
    }
}
