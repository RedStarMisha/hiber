package ru.get.beandefenition;

import lombok.Setter;
import ru.get.bean.construct.TestBean;
import ru.get.bean.construct.TestInterface;

public class TestBean2 extends TestBean implements TestInterface {
    @Setter
    private String message;

    @Override
    public void say() {
        System.out.println("TestBean2 message");
    }
}
