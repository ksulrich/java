package de.ulrich.pmr;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ReadPMR {

    public static void main(String[] args) throws IOException, ParseException {

        List<Event> events = new ArrayList();
        InputStream in = new FileInputStream("55635,180,000.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        {
            String line;
            while ((line = reader.readLine()) != null) {
                if (Event.isEvent(line)) {
                    events.add(new Event(line));
                }
            }
        }
        for (Event e : events) {
            System.out.println(e);
        }
    }



}
