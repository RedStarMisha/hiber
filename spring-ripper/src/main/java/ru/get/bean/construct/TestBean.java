package ru.get.bean.construct;

import jakarta.annotation.PostConstruct;
import org.springframework.context.event.EventListener;
import ru.get.bean.construct.event.CustomSpringEvent;
import ru.get.bean.construct.postprocess.afterbpp.PostProxy;
import ru.get.bean.construct.postprocess.afterinitialization.Profiling;
import ru.get.bean.construct.postprocess.beforeinitialization.InjectRandomInt;
import ru.get.beandefenition.TestBean2;
import ru.get.beandefenition.construct.DeprecatedClass;

@Profiling
@DeprecatedClass(newImpl = TestBean2.class)
public class TestBean implements TestInterface {
    @InjectRandomInt(min = 2, max = 7)
    private int repeat;
    private String message;
    @Override
    public void say() {
        for (int i = 0; i < repeat; i++) {
            System.out.println(message);
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("Phase2");
        System.out.println(repeat);
    }

    public TestBean() {
        System.out.println("Phase1");
    }

    @PostProxy
    public void postProxy() {
        System.out.println("Phase 3");
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void handleCustomEvent(CustomSpringEvent event) {
        System.out.println(event.getMessage());
    }
}
