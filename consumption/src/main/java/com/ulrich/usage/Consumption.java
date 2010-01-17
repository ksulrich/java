package com.ulrich.usage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Consumption {

    private final int POS_DATE = 1;
    private final int POS_LITER = 2;
    private final int POS_KM = 3;
    private final int POS_COST = 4;
    private final int POS_FULL = 5;

    private List<Entry> entries;
    private static String input;

    public Consumption() throws IOException, ParseException {
        entries = new ArrayList<Entry>();
        readEntries();
    }

    private void readEntries() throws IOException, ParseException {
        Pattern emptyLine = Pattern.compile("^\\s*$");
        Pattern commentLine = Pattern.compile("^#");
        String linePattern = "^(\\d+\\.\\d+\\.\\d+)\\s+(\\d+\\.\\d+)\\s+(\\d+)\\s+(\\d+\\.\\d+)\\s+(\\d)";
        BufferedReader br = new BufferedReader(new FileReader(input));
        String line;
        while ((line = br.readLine()) != null) {
            if (commentLine.matcher(line).find()) {
                // ignore comments
                continue;
            }
            if (emptyLine.matcher(line).find()) {
                // ignore empty lines
                continue;
            }
            //System.out.println("Line: '" + line + "'");

            Pattern p = Pattern.compile(linePattern);
            Matcher m = p.matcher(line);
            boolean matchFound = m.find();
            if (matchFound) {
                Entry e = new Entry(m.group(POS_DATE), m.group(POS_LITER),
                        m.group(POS_KM), m.group(POS_COST), m.group(POS_FULL));
                entries.add(e);
                //System.out.println("Entry added: " + e);
            }
        }
    }

    public static void main(String[] argv) throws IOException, ParseException {
        if (argv.length < 1) {
            System.err.println("Consumption: Consumption <input-file>");
            System.exit(1);
        }
        input = argv[0];
        //System.out.println("Input=" + input);
        Consumption usage = new Consumption();
        System.out.println("Overall average: " + usage.average());
        for (int i = 1; i < usage.entries.size(); i++) {
            Entry e = usage.entries.get(i);
            System.out.println(e + ": " + usage.average(i));
        }
    }

    private float average() {
        float fuel = 0.0f;
        int kilometer = 0;
        Entry last = null;
        for (Entry e : entries) {
            if (last != null)
                fuel += e.getFuel();
            kilometer = e.getKilometer();
            last = e;
        }
        kilometer -= entries.get(0).getKilometer();
        return fuel / kilometer * 100;
    }

    /**
     * Returns average on position index.
     * @param index use entry on index possition
     * @return average on position index.
     */
    private double average(int index) {
        Entry last = entries.get(index - 1);
        return entries.get(index).average(last);
    }

}
