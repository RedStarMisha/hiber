package ru.s3connector.model;

public class ResponseFromServiceException extends RuntimeException {
    public ResponseFromServiceException(String path) {
        super("Ответ от сервиса реконструктора по пути " + path + " не был получен");
    }
}
