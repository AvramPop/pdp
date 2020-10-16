package com.company;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Vector vector1 = new Vector(Arrays.asList(2, 4, 6, 8));
        Vector vector2 = new Vector(Arrays.asList(1, 2, 3, 4));
        SharedBuffer buffer = new SharedBuffer();
        Producer producer = new Producer(vector1, vector2, buffer);
        Consumer consumer = new Consumer(vector1.components.size(), buffer);
        producer.start();
        consumer.start();
    }
}
