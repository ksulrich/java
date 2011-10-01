package de.ulrich.pmr;

public class Queue {
    // RETAIN queue of the form "HOLSFX,144"
    String queue;

    public Queue(String queue) {
        this.queue = queue;
    }

    public String getCenter() {
        String[] tmp = queue.split(",");
        assert tmp.length > 0;
        return tmp[1];
    }

    @Override
    public String toString() {
        return "Queue{" +
                "queue='" + queue + '\'' +
                '}';
    }
}
