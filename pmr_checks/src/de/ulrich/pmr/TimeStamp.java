package de.ulrich.pmr;

public class TimeStamp {

    // of the form "11/09/05-09:58"
    private String timeStamp;

    public TimeStamp(String group) {
        this.timeStamp = group;
    }

    @Override
    public String toString() {
        return "TimeStamp{" +
                "timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
