package de.ulrich.pmr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeStamp {

    private final static Calendar cal = GregorianCalendar.getInstance();
    // 11/09/05-15:02
    private final static DateFormat df = new SimpleDateFormat("yy/MM/dd-HH:mm");

    // of the form "11/09/05-09:58"
    private String timeStampInput;
    private Date timeStamp;

    public TimeStamp(String group) throws ParseException {
        this.timeStampInput = group;
        timeStamp = df.parse(timeStampInput);
    }

    public Date getDate() {
        return timeStamp;
    }
    @Override
    public String toString() {
        return "TimeStamp{" +
                "timeStamp=" + df.format(timeStamp) +
                '}';
    }
}
