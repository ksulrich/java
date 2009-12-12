// $Id: ListElement.java,v 1.4 2004/03/11 10:53:32 ku Exp $

package com.danet.util;

import com.danet.exceptions.IllegalSizeException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListElement {
  private List in, out;
  private int sumHour, sumMin;
  private boolean calculate;

  public ListElement() {
    calculate = true;
    in = new ArrayList();
    out = new ArrayList();
  }

  public void addInElement(TimeStamp in) {
    calculate = true;
    this.in.add(in);
  }

  public void addOutElement(TimeStamp out) {
    calculate = true;
    this.out.add(out);
  }

  public List getInList() {
    return this.in;
  }

  public List getOutList() {
    return this.out;
  }

  public String getDate() {
    TimeStamp t = (TimeStamp) this.in.get(0);
    return t.getDate();
  }

  public int getDay() {
    TimeStamp t = (TimeStamp) this.in.get(0);
    return t.getDay();
  }

  public String getAccuTime() {
    calculateAccuTime();
    return new String(sumHour + ":" + sumMin);
  }

  public String toString() {
    String output = "";
    Iterator inIter = in.iterator();
    Iterator outIter = out.iterator();
    calculateAccuTime();
    while (inIter.hasNext() && outIter.hasNext()) {
      TimeStamp inStamp = (TimeStamp) inIter.next();
      TimeStamp outStamp = (TimeStamp) outIter.next();
      output += "IN: " + inStamp.toString() + " OUT: " + outStamp.toString() +
          " ==> " + sumHour + ":" + sumMin + "\n";
    }
    return output;
  }

  public int getAccuHours() throws IllegalSizeException {
    if (calculate) {
      calculateAccuTime();
    }
    return sumHour;
  }

  public int getAccuMinutes() throws IllegalSizeException {
    if (calculate) {
      calculateAccuTime();
    }
    return sumMin;
  }

  protected void calculateAccuTime() throws IllegalSizeException {
    if (in.size() < out.size()) {
      throw new IllegalSizeException("IN list has less elements than OUT list");
    }
    Iterator inIter = in.iterator();
    Iterator outIter = out.iterator();
    int minutes = 0;
    while (inIter.hasNext()) {
      TimeStamp tin = (TimeStamp) inIter.next();
      TimeStamp tout = (TimeStamp) outIter.next();
      int minutes_in = tin.getHour() * 60 + tin.getMinute();
      int minutes_out = tout.getHour() * 60 + tout.getMinute();
      minutes += minutes_out - minutes_in;
    }
    sumHour = minutes / 60;
    sumMin = minutes % 60;

    calculate = false;
  }
}
