package com.danet.gui;

import com.danet.util.ListElement;
import com.danet.util.TimeStamp;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/* $Id: Zeiten2.java,v 1.3 2004/12/23 22:43:42 klaus Exp $
 * COPYRIGHT (c) 2004 Danet IS GmbH
 */

public class Zeiten2 {

    private final static String IN = "IN:";
    private final static String OUT = "OUT:";
    private final static String START = "START";
    private final static String END = "END";
    private final static String[] MONTH = {
            "JANUAR",
            "FEBRUAR",
            "MÄRZ",
            "APRIL",
            "MAI",
            "JUNI",
            "JULI",
            "AUGUST",
            "SEPTEMBER",
            "OKTOBER",
            "NOVEMBER",
            "DEZEMBER"
    };

    private int startIndex = -1, endIndex = -1;

    private JScrollPane listScrollPane;
    private JList list;
    private JLabel topicLabel;
    private JTextField timeField;
    private JTextField sumField;
    private JComponent mainPanel;
    private JTextField endField;
    private JButton endButton;
    private JTextField startField;
    private JButton startButton;

    public static void main(String[] argv) {
        if (argv.length != 1) {
            System.err.println("Usage: Zeiten <database>");
            System.exit(1);
        }
        List data = Zeiten2.readData(argv[0]);
        Zeiten2 zeiten = new Zeiten2(data);
        zeiten.setFocus();

        JFrame frame = new JFrame("Arbeitszeiten");
        frame.setContentPane(zeiten.mainPanel);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Read file and create a List of ListElement objects, containing
     * a list of in dates and a list of out dates.
     *
     * @param file File to read.
     * @return List of ListElement objects.
     * @see com.danet.util.ListElement
     */
    private static List readData(String file) {
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

    private Zeiten2(List data) {
        startButton.addActionListener(new Zeiten2.ButtonActionListener());
        startButton.setActionCommand(START);

        endButton.addActionListener(new Zeiten2.ButtonActionListener());
        endButton.setActionCommand(END);

        // insert data elements into list
        DefaultListModel listModel = new DefaultListModel();
        list.setModel(listModel);
        for (Object aData : data) {
            listModel.addElement(aData);
        }
        list.setCellRenderer(new Zeiten2.ListElementCellRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ListElement element = (ListElement) list.getSelectedValue();
                    display(element.getDate(), element.getAccuHours(), element.getAccuMinutes());
                }
            }
        });

        // select first day in current month
        int selectionIndex = calcCurrentMonth();
        if (selectionIndex != -1) {
            // we have a position in the list to select
            list.setSelectedIndex(selectionIndex);
            Point viewPoint = list.indexToLocation(selectionIndex);
            JViewport viewPort = listScrollPane.getViewport();
            viewPort.setViewPosition(viewPoint);
            listScrollPane.setViewport(viewPort);
        }

        //add(listScrollPane, BorderLayout.WEST);
        //add(labelPane, BorderLayout.CENTER);
        //add(printPane, BorderLayout.SOUTH);

        // main container
        //Container contentPane = getContentPane();
        //contentPane.add(listScrollPane, BorderLayout.WEST);
        //contentPane.add(labelPane, BorderLayout.CENTER);
//    contentPane.add(printPane, BorderLayout.SOUTH);
    }

    int calcCurrentMonth() {
        int index = -1;
        int lastIndex = list.getModel().getSize() - 1;
        ListElement elementList = (ListElement) list.getModel().getElementAt(lastIndex);
        List inList = elementList.getInList();
        TimeStamp lastEntry = (TimeStamp) inList.get(0);
        int thisMonth = lastEntry.getMonth();
        for (int i = lastIndex; i >= 0; i--) {
            ListElement eList = (ListElement) list.getModel().getElementAt(i);
            List iList = eList.getInList();
            TimeStamp t = (TimeStamp) iList.get(0);
            if (t.getMonth() == thisMonth) {
                index = i;
            } else {
                break;
            }
        }
        return index;
    }

    void display(String date, int hours, int minutes) {
        TimeStamp tStamp = new TimeStamp(date + " " + hours + ":" + minutes);
        topicLabel.setText("<html><h2><b>" + tStamp.getDay() + " " +
                MONTH[tStamp.getMonth() - 1] +
                " " + tStamp.getYear() + "</b></h2>");
        timeField.setText(tStamp.getTime());

        if (tStamp.getHour() < 8) {
            timeField.setBackground(Color.orange);
        } else {
            timeField.setBackground(Color.green);
        }
    }

    void setFocus() {
        // set input focus to list of data
        list.requestFocus();
    }

    void setSumField() {
        if ((endIndex != -1 && startIndex != -1) &&
                (endIndex >= startIndex)) {
            int hours = 0;
            int minutes = 0;
            for (int i = startIndex; i <= endIndex; i++) {
                ListElement element = (ListElement) list.getModel().getElementAt(i);
                hours += element.getAccuHours();
                minutes += element.getAccuMinutes();
            }
            hours += minutes / 60;
            minutes = minutes % 60;

            sumField.setText(hours + ":" + minutes);
            sumField.setBackground(Color.yellow);
        } else {
            sumField.setText("");
            sumField.setBackground(Color.white);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        listScrollPane = new JScrollPane();
        listScrollPane.setHorizontalScrollBarPolicy(30);
        listScrollPane.setVerticalScrollBarPolicy(20);
        mainPanel.add(listScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        list = new JList();
        listScrollPane.setViewportView(list);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        topicLabel = new JLabel();
        topicLabel.setText("DATUM");
        panel3.add(topicLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Gesamtzeit:");
        panel4.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        timeField = new JTextField();
        timeField.setColumns(6);
        timeField.setEditable(false);
        timeField.setHorizontalAlignment(0);
        panel4.add(timeField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel5.setEnabled(false);
        panel3.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        startButton = new JButton();
        startButton.setText("Start:");
        panel5.add(startButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        startField = new JTextField();
        startField.setColumns(10);
        startField.setEditable(false);
        startField.setHorizontalAlignment(0);
        panel5.add(startField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(100, -1), null, 0, false));
        endButton = new JButton();
        endButton.setText("Ende:");
        panel5.add(endButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        endField = new JTextField();
        endField.setColumns(10);
        endField.setEditable(false);
        endField.setHorizontalAlignment(0);
        panel5.add(endField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Summe:");
        panel5.add(label2, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sumField = new JTextField();
        sumField.setEditable(false);
        sumField.setHorizontalAlignment(0);
        panel5.add(sumField, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }


    class ListElementCellRenderer extends JLabel implements ListCellRenderer {
        private final Border noFocusBorder;

        public ListElementCellRenderer() {
            super();
            setOpaque(true);
            noFocusBorder = new EmptyBorder(1, 1, 1, 1);
            setBorder(noFocusBorder);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean hasFocus) {
            ListElement element = (ListElement) value;
            List inList = element.getInList();
            TimeStamp tin = (TimeStamp) inList.get(0);
            setText(tin.getDate());
            setComponentOrientation(list.getComponentOrientation());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setBorder((hasFocus) ?
                    UIManager.getBorder("List.focusCellHighlightBorder") :
                    noFocusBorder);

            return this;
        }
    }

    private class ButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ListElement elementList = (ListElement) list.getSelectedValue();
            List inList = elementList.getInList();
            List outList = elementList.getOutList();
            TimeStamp tin = (TimeStamp) inList.get(0);
            TimeStamp tout = (TimeStamp) outList.get(outList.size() - 1);
            if (e.getActionCommand().equals(START)) {
                startField.setText(tin.getDate());
                startIndex = list.getSelectedIndex();
            } else {
                endField.setText(tout.getDate());
                endIndex = list.getSelectedIndex();
            }
            setSumField();
        }
    }

}
