package de.ulrich.pmr;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PmrClass {
    private PmrClassContext _fsm;
    private boolean _is_acceptable;

    public PmrClass() {
        _fsm = new PmrClassContext(this);

        // Uncomment to see debug output.
        // _fsm.setDebugFlag(true);
    }

    public void readPmr(String string) throws IOException, ParseException {
        InputStream in = new FileInputStream(string);
        List<Event> events = new ArrayList();
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
            final String type = e.getType();
            switch (type) {
                case Event.AT_Type:
                    _fsm.AT();
                    break;
                default:
                    _fsm.Unknown();
                    break;
            }
        }
    }

    public void Unknown() {

    }

    public void StartTimer(String type) {
    }

    public void StopTimer(String type) {
    }

    public boolean isSev1() {
        return false;
    }

    public void Report() {

    }
}
