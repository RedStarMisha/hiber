package ru.s3connector.model;

public class RequestToServiceException extends RuntimeException {
    public RequestToServiceException(String path) {
        super("Запрос к реконструктору по пути " + path + " не был успешен");
    }
}
