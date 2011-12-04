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
        _fsm.setDebugFlag(true);
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
                case Event.CE_Type:
                    _fsm.CE();
                    break;
                case Event.CT_Type:
                    _fsm.CT();
                    break;
                case Event.SCE_Type:
                    _fsm.SCE();
                    break;
                case Event.SCT_Type:
                    _fsm.SCT();
                case Event.AT_Type:
                    _fsm.AT();
                    break;
                case Event.SAT_Type:
                    _fsm.SAT();
                    break;

                case Event.AL_Type:
                    _fsm.AL();
                    break;

                case Event.CR_Type:
                    _fsm.CR();
                    break;

                default:
                    _fsm.Unknown();
                    break;
            }
        }
    }

    public void StartTimer(String type) {
        System.out.println("StartTimer calling: " + type);
    }

    public void StopTimer(String type) {
        System.out.println("StopTimer calling: " + type);
    }

    public boolean isSev1() {
        return false;
    }

    public void Report() {
        System.out.println("Report calling");
    }

    public void AT() {
        System.out.println("AT calling");
    }

    public void SCE() {
        System.out.println("SCE calling");
    }

    public void SAT() {
        System.out.println("SAT calling");
    }

    public void AL() {
        System.out.println("AL calling");
    }

    public void CR() {
        System.out.println("CR calling");
    }

    public void CE() {
        System.out.println("CE calling");
    }

    public void CT() {
        System.out.println("CT calling");
    }

    public void SCT() {
        System.out.println("SCT calling");
    }

    public void Unknown() {
        System.out.println("Unknown calling");
    }


}
