package de.ulrich.pmr.test;

import de.ulrich.pmr.Event;
import junit.framework.Assert;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class EventTest {

    @Test
    public void testEvent() throws ParseException {
        String line = " +NARIANI, VINEET       -5724E7600  -L13G/WPSTUN-P1S1-11/09/05-09:58--CE";
        DateFormat df = new SimpleDateFormat("yy/MM/dd-HH:mm");

        Event event = new Event(line);
        Assert.assertEquals(event.getUser(), "NARIANI, VINEET");
        Assert.assertEquals(event.getCompId(), "5724E7600");
        Assert.assertEquals(event.getQueue().getQueueName(), "WPSTUN");
        Assert.assertEquals(event.getQueue().getCenter(), "13G");
        Assert.assertEquals(event.getSeverity(), "P1S1");
        Assert.assertEquals(event.getTimeStamp().getDate(), df.parse("11/09/05-09:58"));
        Assert.assertEquals(event.getType(), Event.CE_Type);
    }
}
