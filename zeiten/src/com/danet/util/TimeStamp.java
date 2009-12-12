// $Id: TimeStamp.java,v 1.4 2004/03/11 10:53:05 ku Exp $

package com.danet.util;

import java.text.NumberFormat;
import java.util.StringTokenizer;

public class TimeStamp {
  private int day, month, year, hour, min;
  private String date, time;

  public TimeStamp(String line) {
    line.trim();
    StringTokenizer tok = new StringTokenizer(line);
    date = tok.nextToken();
    StringTokenizer dateTok = new StringTokenizer(date, "/");
    day = Integer.parseInt(dateTok.nextToken());
    month = Integer.parseInt(dateTok.nextToken());
    year = Integer.parseInt(dateTok.nextToken());

    String timeStr = tok.nextToken();
    StringTokenizer timeTok = new StringTokenizer(timeStr, ":");
    hour = Integer.parseInt(timeTok.nextToken());
    min = Integer.parseInt(timeTok.nextToken());
    NumberFormat nf = NumberFormat.getNumberInstance();
    nf.setMinimumIntegerDigits(2);
    time = hour + ":" + nf.format(min);
  }

  public String getDate() {
    return date;
  }

  public String getTime() {
    return time;
  }

  public int getDay() {
    return day;
  }

  public int getMonth() {
    return month;
  }

  public int getYear() {
    return year;
  }

  public int getHour() {
    return hour;
  }

  public int getMinute() {
    return min;
  }

  public String toString() {
    return new String(getDate() + " " + getTime());
  }
}
