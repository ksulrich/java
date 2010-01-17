package com.ulrich.usage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Entry {
    private Date date;

    public Date getDate() {
        return date;
    }

    public double getFuel() {
        return fuel;
    }

    public double getCost() {
        return cost;
    }

    public boolean isFull() {
        return full;
    }

    private double fuel;
    private int kilometer;
    private double cost;
    private boolean full;

    private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyy");

    public Entry(String date, String liter, String km, String cost, String full) throws ParseException {
        this.date = df.parse(date);
        this.fuel = Double.parseDouble(liter);
        this.kilometer = Integer.parseInt(km);
        this.cost = Double.parseDouble(cost);
        this.full = Integer.parseInt(full) == 1;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "date='" + df.format(date) + '\'' +
                ", fuel=" + fuel +
                ", kilometer=" + kilometer +
                ", cost=" + cost +
                ", full=" + full +
                '}';
    }

    public int getKilometer() {
        return kilometer;
    }

    /**
     * Returns average usage in liters per 100 kilometers.
     *
     * @param last entry before the calculated entry
     * @return average usage in liters per 100 kilometers.
     */
    public double average(Entry last) {
        if (this.kilometer < last.kilometer) {
            throw new IllegalArgumentException("Kilometer needs to be greater as " + this.kilometer);
        }
        if (!last.full) {
            throw new IllegalArgumentException("Average could not be calculated, because no full tank");
        }
        int diff = this.kilometer - last.kilometer;
        return this.fuel / diff * 100;
    }
}
