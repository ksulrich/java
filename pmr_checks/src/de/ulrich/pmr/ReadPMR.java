package de.ulrich.pmr;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ReadPMR {


    public static void main(String[] args) throws IOException, ParseException {

        PmrClass pmrObject = new PmrClass();

        if (args.length < 1) {
            System.err.println("Usage: ReadPMR <input_file>");
            System.exit(1);
        }
        pmrObject.readPmr(args[0]);
    }
}
