package com.ulrich.usage;

import org.junit.Before;
import org.junit.Test;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntryTest {
    Entry e1, e2, e3, e4, e5, e6, e7, e8;

    @Before
    public void setUP() throws ParseException {
        e1 = new Entry("18.06.2008", "35.30", "14597", "55.74", "1");
        e2 = new Entry("17.07.2008", "34.15", "15143", "52.56", "1");
        e3 = new Entry("08.08.2008", "37.38", "15719", "53.42", "1");
        e4 = new Entry("10.09.2008", "34.56", "16465", "51.46", "1");
        e5 = new Entry("22.09.2008", "26.01", "16768", "37.69", "1");
        e6 = new Entry("03.11.2008", "36.07", "17453", "42.92", "1");
        e7 = new Entry("28.11.2008", "35.28", "18029", "41.95", "0");
        e8 = new Entry("22.12.2008", "32.24", "18507", "35.75", "1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalTest() {
        e1.average(e2);
    }

    @Test
    public void averageTest() {
        assertTrue(e2.average(e1) == 34.15 / (15143 - 14597) * 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noFullTankTest() {
        e8.average(e7);
    }

    @Test
    public void toStringTest() {
        assertEquals(e8.toString(), "Entry{date='22.12.2008', fuel=32.24, kilometer=18507, cost=35.75, full=true}");
    }
}
