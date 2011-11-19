package de.ulrich.pmr.test;

import de.ulrich.pmr.Queue;
import org.junit.Assert;
import org.junit.Test;

public class QueueTest {

    @Test
    public void q1() {
        Queue queue = new Queue("L13G/PORT");
        Assert.assertEquals(queue.getCenter(), "13G");
        Assert.assertEquals(queue.getQueueName(), "PORT");
    }

    @Test
    public void q2() {
        Queue queue = new Queue("L13G/-----");

        Assert.assertEquals(queue.getCenter(), "13G");
        Assert.assertEquals(queue.getQueueName(), "");
    }

    @Test
    public void q3() {
        Queue queue = new Queue("L328/HALLO");
        Assert.assertEquals(queue.getCenter(), "328");
        Assert.assertEquals(queue.getQueueName(), "HALLO");
    }
}
