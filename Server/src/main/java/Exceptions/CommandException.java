package Exceptions;

/**
 * Кастомное исключение для команд с чистым выводом сообщения
 */
public class CommandException extends RuntimeException {
    public CommandException(String cleanMessage) {
        super(cleanMessage);
    }

    // Переопределяем, чтобы исключить stack trace
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; // Не заполняем stack trace
    }
}