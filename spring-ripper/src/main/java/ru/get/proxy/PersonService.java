package ru.get.proxy;

public class PersonService {
    public String sayHello(String name) {
        return "Hello " + name;
    }

    public String sayHello2(String name) {
        return "Hi " + name;
    }

    public Integer lengthOfName(String name) {
        return name.length();
    }
}
