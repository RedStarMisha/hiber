package ru.get.proxy;

import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.FixedValue;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/*
Профилирование с помощью proxy библиотеки CGLIB
 */
public class ProxyTestService {
    public void testProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PersonService.class);
        enhancer.setCallback((FixedValue) () -> "good bye");
        PersonService proxy = (PersonService) enhancer.create();

        String result = proxy.sayHello("xxx");
        String result2 = proxy.sayHello2("xxx");

        System.out.println(result);
        System.out.println(result2);
//        enhancer.setCallback((FixedValue) () -> 12);
//
//        PersonService proxy = (PersonService) enhancer.create();
//
//        int result = proxy.lengthOfName("asd");
//
//        System.out.println(result);
    }

    public void testProxy2() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PersonService.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            //Указываем что метод не принадлежит классу Object (как toString и hashCode) и то что он возвращает String
            if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class) {
                return "Hello Tom!";
            } else {
                return proxy.invokeSuper(obj, args);
            }
        });
        PersonService proxy = (PersonService) enhancer.create();

        String result = proxy.sayHello("xxx");
        String result2 = proxy.sayHello2("xxx");


        System.out.println(result);
        System.out.println(result2);

    }

    public void testProxy3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BeanGenerator beanGenerator = new BeanGenerator();

        beanGenerator.addProperty("name", String.class);
        Object myBean = beanGenerator.create();
        Method setter = myBean.getClass().getMethod("setName", String.class);
        setter.invoke(myBean, "some string value set by a cglib");

        Method getter = myBean.getClass().getMethod("getName");

        System.out.println(getter.invoke(myBean));
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        new ProxyTestService().testProxy2();
    }
}
