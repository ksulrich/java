package de.ulrich.pmr;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Event {
    private String severity;
    private String compId;

    public static final String CE_Type = "CE";
    public static final String CT_Type = "CT";
    public static final String SCE_Type = "SCE";
    public static final String SCT_Type = "SCT";
    public static final String AT_Type = "AT";

    public final static Pattern pattern = Pattern.compile("^ \\+(......................)-(...........)-(...........)-(....)-(........-.....)-(...)");

    String user;
    String type;
    String line;
    Queue queue;
    TimeStamp timeStamp;

    public Event(String line) throws ParseException {
        this.line = line;
        init();
    }

    public static boolean isEvent(String line) {
        return pattern.matcher(line).find();
    }

    private void init() throws ParseException {
        // Event line has the form of:
        // " +NARIANI, VINEET       -5724E7600  -L13G/WPSTUN-P1S1-11/09/05-09:58--CE"
        // " +......................-...........-...........-....-........-.....-..."
        //Pattern pattern = Pattern.compile("^ +\\+(\\w+,\\s*\\w+)\\s+-(\\w+)\\s+-(\\w+/\\w+)-(P\\dS\\d)-(\\d\\d/\\d\\d/\\d\\d-\\d\\d:\\d\\d)-+(\\w+)");

        //Pattern pattern = Pattern.compile("^\\s+\\+(.*)");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            //System.out.println("User=" + matcher.group(1) + ", CompId=" + matcher.group(2) + ", Queue=" + matcher.group(3) + ", Sev=" + matcher.group(4) + ", Date=" + matcher.group(5) + ", Type=" + matcher.group(6));
            user = matcher.group(1).trim();
            compId = matcher.group(2).trim();
            queue = new Queue(matcher.group(3));
            severity = matcher.group(4);
            timeStamp = new TimeStamp(matcher.group(5));
            type = matcher.group(6).replace("-", "");
        }
    }

    public String getCompId() {
        return compId;
    }

    public String getUser() {
        return user;
    }

    public String getType() {
        return type;
    }

    public Queue getQueue() {
        return queue;
    }

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public boolean isSev1() {
        return severity.charAt(3) == '1';
    }

    public int getSeverity() {
        return Integer.parseInt(severity.substring(3));
    }

    @Override
    public String toString() {
        return "Event{" +
                "severity='" + getSeverity() + '\'' +
                ", compId='" + compId + '\'' +
                ", user='" + user + '\'' +
                ", type='" + type + '\'' +
                ", queueString=" + queue +
                ", timeStamp=" + timeStamp +
                ", line='" + line + '\'' +
                '}';
    }
}

