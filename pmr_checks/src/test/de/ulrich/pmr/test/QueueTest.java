package test.de.ulrich.pmr.test;

import de.ulrich.pmr.Queue;
import org.junit.Assert;
import org.junit.Test;

public class QueueTest {

    @Test
    public void getCenter() {
        Queue queue = new Queue("HOLSFX,144");
        Assert.assertEquals(queue.getCenter(), "144");
    }
}
