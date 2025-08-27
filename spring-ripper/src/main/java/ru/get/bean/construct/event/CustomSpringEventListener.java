package ru.get.bean.construct.event;

import org.springframework.context.ApplicationListener;

public class CustomSpringEventListener implements ApplicationListener<CustomSpringEvent> {
    @Override
    public void onApplicationEvent(CustomSpringEvent event) {
//        System.out.println(event.getMessage());
    }
}
