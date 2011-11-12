package com.danet.gui;

import com.danet.util.ListElement;
import com.danet.util.TimeStamp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBase implements Serializable {
    private final static String IN = "IN:";
    private final static String OUT = "OUT:";
    
    /**
     * Read file and create a List of ListElement objects, containing
     * a list of in dates and a list of out dates.
     *
     * @param file File to read.
     * @return List of ListElement objects.
     * @see com.danet.util.ListElement
     */
    public static List readData(String file) {
        List dataList = new ArrayList();
        try {
            BufferedReader bin = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String input;
            int line = 0;
            ListElement element = null;
            while ((input = bin.readLine()) != null) {
                line++;
                input = input.trim();
                if (input.charAt(0) != '#') {
                    if (input.startsWith(IN)) {
                        String in = input.substring(IN.length());
                        TimeStamp t = new TimeStamp(in);
                        if (element == null) {
                            element = new ListElement();
                            element.addInElement(t);
                        } else {
                            // we already have an element; check if it is a new entry
                            if (element.getDay() == t.getDay()) {
                                // same day --> additional entry element
                                element.addInElement(t);
                            } else {
                                // a new entry is found --> append current element
                                // and create a new element for this entry
                                dataList.add(element);
                                element = new ListElement();
                                element.addInElement(t);
                            }
                        }
                    } else if (input.startsWith(OUT)) {
                        String out = input.substring(OUT.length());
                        TimeStamp t = new TimeStamp(out);
                        if (element == null) {
                            throw new NoSuchFieldException("Line " + line + ": Corresponding IN element not found");
                        } else {
                            // check if it is a following entry of the same day
                            if (element.getDay() == t.getDay()) {
                                // following entry
                                element.addOutElement(t);
                            } else {
                                // entry for new day found
                                // that means no input element found yet --> ERROR
                                throw new NoSuchFieldException("Line " + line + ": Corresponding IN element not found");
                            }
                        }
                    } else {
                        System.err.println("Input: '" + input + "' in Line " + line + " ignored");
                    }
                }
            }
            // check if we need to save the current element
            if (element != null &&
                    element.getOutList().size() != 0) {
                dataList.add(element);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return dataList;
    }
}