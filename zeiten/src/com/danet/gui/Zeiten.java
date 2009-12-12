// $Id$

package com.danet.gui;

import com.danet.util.ListElement;
import com.danet.util.SwingWorker;
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
import java.awt.print.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a GUI to display working time.
 * <nl>
 * <li>First a perl script is started to create the data.
 * <li>Second the data is displayed.
 * </nl>
 */
public class Zeiten extends JFrame {
    final static String IN = "IN:", OUT = "OUT:", START = "START", END = "END";
    final static String[] MONTH = {
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

    private static Zeiten frame;

    private JTextField timeField, startField, endField, sumField;

    private JLabel topicLabel;
    private JList list;

    private SwingWorker worker;

    private int startIndex = -1, endIndex = -1;

    public static void main(String[] argv) {
        if (argv.length != 1) {
            System.err.println("Usage: Zeiten <database>");
            System.exit(1);
        }
        List data = Zeiten.readData(argv[0]);
        frame = new Zeiten(data);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
        frame.setFocus();
    }

    /**
     * Read file and create a List of ListElement objects, containing
     * a list of in dates and a list of out dates.
     *
     * @param file File to read.
     * @return List of ListElement objects.
     * @see ListElement
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

    public Zeiten(List data) {
        super("Zeiten");

        // topic
        topicLabel = new JLabel();
        topicLabel.setHorizontalAlignment(JLabel.CENTER);

        // print buttons
        JButton printListButton = new JButton("Print List");
        printListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        JButton printButton = new JButton("Print Dialog");
        printButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                final PrinterJob printerJob = PrinterJob.getPrinterJob();
                Book book = new Book();
                PageFormat page = new PageFormat();
                PrintImage printImg = new PrintImage(list, page);
                // get the number of pages needed for the printout
                int pages = printImg.getPages();
                for (int i = 0; i < pages; i++) {
                    // for every page a reference to PrintTable and the page
                    // format has to be appended
                    book.append(printImg, page);
                }
                // the Book object should be printed
                printerJob.setPageable(book);

                final PrintInfo pi = new PrintInfo();
                if (printerJob.printDialog()) {
                    worker = new SwingWorker() {
                        public Object construct() {
                            try {
                                printerJob.print();
                            } catch (PrinterException ex) {
                                ex.printStackTrace();
                            }

                            return 1;
                        }

                        public void finished() {
                            pi.dispose();
                        }
                    };
                    worker.start();
                    pi.pack();
                    pi.setVisible(true);
                }
            }
        });


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

        // Print panel
        JPanel printPane = new JPanel(new BorderLayout());
        printPane.add(printButton, BorderLayout.WEST);
        printPane.setBorder(new EmptyBorder(15, 15, 15, 15));

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
//    contentPane.add(printPane, BorderLayout.SOUTH);
    }

    public void setFocus() {
        // set input focus to list of data
        list.requestFocus();
    }

    protected int calcCurrentMonth() {
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

    protected void setSumField() {
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
    protected void display(String date, int hours, int minutes) {
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
        private Border noFocusBorder;

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

    class ButtonActionListener implements ActionListener {
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

    class PrintImage implements Printable {
        private JComponent comp;
        private int pageOffset;
        private double scaleFactor;
        private int numPages;

        /**
         * Creates a <code>PrintImage</code> and calculates some parameters
         * (scaling factor, number of pages needed).
         *
         * @param comp   The image to be printed
         * @param format Page format for printout
         */
        public PrintImage(JComponent comp, PageFormat format) {
            this(comp, format, 0);
        }

        /**
         * Creates a <code>PrintImage</code> and calculates some parameters
         * (scaling factor, number of pages needed).
         *
         * @param comp   The image to be printed
         * @param format Page format for printout
         * @param offset Page offset for printout
         */
        public PrintImage(JComponent comp, PageFormat format, int offset) {
            this.comp = comp;
            this.pageOffset = offset;
            this.scaleFactor = 1.0; //calcScaleFactor();

            numPages = (int) (comp.getSize().getHeight() / format.getHeight()) + 1;
        }

        /**
         * Returns number of pages needed for printout.
         *
         * @return number of pages needed for printout.
         */
        public int getPages() {
            return numPages;
        }

        // interface Printable

        /**
         * Implementation of print method.
         *
         * @param g         - the context into which the page is drawn
         * @param format    the size and orientation of the page being drawn
         * @param pageIndex the zero based index of the page to be drawn
         * @return PAGE_EXISTS if the page is rendered successfully
         *         or NO_SUCH_PAGE if <code>pageIndex</code> specifies a
         *         non-existent page.
         * @throws java.awt.print.PrinterException
         *          thrown when the print job is terminated.
         */
        public int print(Graphics g, PageFormat format, int pageIndex)
                throws PrinterException {

            Graphics2D g2d = (Graphics2D) g.create();
            double tx = format.getImageableX();
            double ty = format.getImageableY() -
                    format.getImageableHeight() * (pageIndex - pageOffset);

            g2d.translate(tx, ty);

            g2d.scale(scaleFactor, scaleFactor);

            Dimension compSize = comp.getSize();
            g2d.clipRect(0, 0, compSize.width, compSize.height);

            comp.paint(g2d);
            g2d.dispose();

            return Printable.PAGE_EXISTS;
        }

    }

    // interface Printable

    class PrintInfo extends JDialog {
        public PrintInfo() {
            super(frame, "Print Dialog", true);
            JLabel label = new JLabel("<html><b><font siez=+2>Printing...</b></font>", new ImageIcon("/home/ku/lib/printer.gif"), JLabel.CENTER);
            label.setPreferredSize(new Dimension(200, 200));
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(label, BorderLayout.CENTER);

            JButton button = new JButton("Cancel");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Cancel pressed");
                    worker.interrupt();
                }
            });
            panel.add(button, BorderLayout.SOUTH);
            getContentPane().add(panel, BorderLayout.CENTER);
        }
    }
}

    
