// $Id$

package com.danet.gui;

import com.danet.util.ListElement;
import com.danet.util.TimeStamp;

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
import java.util.List;

/**
 * Creates a GUI to display working time.
 * <nl>
 * <li>First a perl script is started to create the data.
 * <li>Second the data is displayed.
 * </nl>
 */
public class Zeiten extends JFrame {
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

    private final JTextField timeField;
    private final JTextField startField;
    private final JTextField endField;
    private final JTextField sumField;

    private final JLabel topicLabel;
    private final JList list;

    private int startIndex = -1, endIndex = -1;

    public static void main(String[] argv) {
        if (argv.length != 1) {
            System.err.println("Usage: Zeiten <database>");
            System.exit(1);
        }
        List data = FileBase.readData(argv[0]);
        Zeiten frame = new Zeiten(data);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
        frame.setFocus();
    }

    private Zeiten(List data) {
        super("Zeiten");

        // topic
        topicLabel = new JLabel();
        topicLabel.setHorizontalAlignment(JLabel.CENTER);

        // time label
        JLabel timeLabel = new JLabel("Gesamtzeit:");

        // time field
        timeField = new JTextField(5);
        timeField.setHorizontalAlignment(JTextField.CENTER);
        timeField.setEditable(false);
        timeField.setBackground(Color.white);

        // time panel
        JPanel timePane = new JPanel();
        timePane.add(timeLabel);
        timePane.add(timeField);

        // start button
        JButton startButton = new JButton("Start:");
        startButton.addActionListener(new ButtonActionListener());
        startButton.setActionCommand(START);

        // start text field
        startField = new JTextField(10);
        startField.setHorizontalAlignment(JTextField.CENTER);
        startField.setEditable(false);
        startField.setBackground(Color.white);

        // end button
        JButton endButton = new JButton("Ende:");
        endButton.addActionListener(new ButtonActionListener());
        endButton.setActionCommand(END);

        // start text field
        endField = new JTextField(10);
        endField.setHorizontalAlignment(JTextField.CENTER);
        endField.setEditable(false);
        endField.setBackground(Color.white);

        // sum label
        JLabel sumLabel = new JLabel("Summe:");

        // start text field
        sumField = new JTextField(10);
        sumField.setHorizontalAlignment(JTextField.CENTER);
        sumField.setEditable(false);
        sumField.setBackground(Color.white);

        // sum panel
        JPanel sumPane = new JPanel();
        sumPane.add(startButton);
        sumPane.add(startField);
        sumPane.add(endButton);
        sumPane.add(endField);
        sumPane.add(sumLabel);
        sumPane.add(sumField);

        // Label panel
        JPanel labelPane = new JPanel(new BorderLayout());
        labelPane.add(topicLabel, BorderLayout.NORTH);
        labelPane.add(timePane, BorderLayout.CENTER);
        labelPane.add(sumPane, BorderLayout.SOUTH);
        labelPane.setBorder(new EmptyBorder(15, 15, 15, 15));

        // insert data elements into list
        DefaultListModel listModel = new DefaultListModel();
        for (Object aData : data) {
            listModel.addElement(aData);
        }

        //Create the list and put it in a scroll pane
        list = new JList(listModel);
        list.setCellRenderer(new ListElementCellRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ListElement element = (ListElement) list.getSelectedValue();
                    display(element.getDate(), element.getAccuHours(), element.getAccuMinutes());
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(list);

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

        // main container
        Container contentPane = getContentPane();
        contentPane.add(listScrollPane, BorderLayout.WEST);
        contentPane.add(labelPane, BorderLayout.CENTER);
    }

    void setFocus() {
        // set input focus to list of data
        list.requestFocus();
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

    /**
     * Display selected line in gui
     *
     * @param date
     */
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

    
