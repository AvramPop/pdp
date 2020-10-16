package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedBuffer {
    private final Lock lock;
    private final Condition condition;
    private final List<Integer> container;

    public SharedBuffer() {
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.container = new ArrayList<>(1);
    }

    public void put(int value) throws InterruptedException {
        lock.lock();
        try {
            while(container.size() == 1) {
                System.out.println("Buffer is full. Waiting");
                condition.await();
            }
            container.add(value);
            System.out.println("Added value " + value + " to the shared buffer");
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public int get() throws InterruptedException {
        lock.lock();
        try {
            while(container.size() == 0) {
                System.out.println("Buffer is empty. Waiting");
                condition.await();
            }
            int value = container.remove(0);
            System.out.println("Returning value " + value + " from the shared buffer");
            condition.signal();
            return value;
        } finally {
            lock.unlock();
        }
    }
}
