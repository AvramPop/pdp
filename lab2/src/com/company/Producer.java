package com.company;

public class Producer extends Thread {

    private final Vector vector1;
    private final Vector vector2;
    private final SharedBuffer buffer;

    public Producer(Vector vector1, Vector vector2, SharedBuffer buffer) {
        super("Producer");
        this.vector1 = vector1;
        this.vector2 = vector2;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        for(int i = 0; i < vector1.components.size(); i++) {
            try {
                int result = vector1.components.get(i) * vector2.components.get(i);
                System.out.println("Multiplying value at index " + i + " = " + result);
                buffer.put(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
