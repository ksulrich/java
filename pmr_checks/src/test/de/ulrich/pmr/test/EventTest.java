package test.de.ulrich.pmr.test;

import de.ulrich.pmr.Event;
import org.junit.Test;

public class EventTest {

    @Test
    public void testEvent() {
        String line = " +NARIANI, VINEET       -5724E7600  -L13G/WPSTUN-P1S1-11/09/05-09:58--CE";

        Event event = new Event(line);
        System.out.println(event);
    }
}
