package de.ulrich.pmr;

public class Queue {
    // RETAIN queueString of the form "HOLSFX,144"
    String queueString;
    String queueName;
    String center;

    public Queue(String queue) {
        this.queueString = queue;
        init();
    }

    private void init() {
        assert queueString.length() > 4;
        assert queueString.charAt(4) == '/';
        center = queueString.substring(1, 4);
        queueName = "";
        if (queueString.length() > 4 && queueString.charAt(5) != '-') {
            queueName = queueString.substring(5);
            queueName.replaceAll("-", "");
        }
    }

    public String getCenter() {
        return center;
    }

    public String getQueueName() {
        return queueName;
    }

    @Override
    public String toString() {
        return "Queue{" +
                "queueString='" + queueString + '\'' +
                '}';
    }
}
