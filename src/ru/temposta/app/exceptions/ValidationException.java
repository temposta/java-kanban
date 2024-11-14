package ru.temposta.app.exceptions;

import java.io.IOException;

public class ValidationException extends RuntimeException {
    public ValidationException(String message, IOException e) {
        super(message, e);
    }
}
