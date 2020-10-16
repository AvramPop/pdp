package com.company;

public class Consumer extends Thread {

    private final SharedBuffer buffer;
    private final int numberOfElements;

    public Consumer(int size, SharedBuffer buffer) {
        super("Consumer");
        this.numberOfElements = size;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        int result = 0;
        for(int i = 0; i < numberOfElements; i++) {
            try {
                result += buffer.get();
                System.out.println("Consumer -> partial result is " + result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Consumer finished. Result is: " + result);
    }
}
