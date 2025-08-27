package ru.get.testclient;


public class A {
    public A() {
    }

    public A(int count) {
        this.count = count;
    }

    private int count;

    @Override
    public String toString() {
        return "A{" +
                "count=" + count +
                '}';
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
