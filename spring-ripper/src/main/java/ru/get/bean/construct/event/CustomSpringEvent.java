package ru.get.bean.construct.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
// extends ApplicationEvent больше не обязателен
public class CustomSpringEvent extends ApplicationEvent  {
    @Getter
    private final String message;

    public CustomSpringEvent(Object object, String message) {
        super(object);
        this.message = message;
    }
}
