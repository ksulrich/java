/**
 * $Id: MainFrame.java,v 1.1.1.1 2008/03/21 13:38:38 klaus Exp $
 */
package com.danet.ulrich.pictures;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

public class MainFrame {
    private static final String DEBUG_STR = System.getProperty("DEBUG", "false");
    private static final String OUTDIR = System.getProperty("OUTDIR", "/tmp/pictures");
    static final Boolean DEBUG = new Boolean(DEBUG_STR);
	
    private JTextArea description;
    private JList fileList;
    private JTextField title;
    private JSplitPane mainPanel;
    private Element[] elements;
    private DefaultListModel model;
    private static JFrame frame;
    private ImageLoader imageComp;
    private Element currentElement;
    private NavigableImagePanel imagePanel;
    //private JLabel imageIcon;
    private JButton delete;
    private double degree;

	public static void main(String[] args) throws IOException {
    	System.out.println("DEBUG=" + DEBUG);
        if (args.length == 1) {
            new MainFrame(args[0]);
        } else {
            System.err.println("Usage: MainFrame <root-dir-of-pictures>");
        }
    }

    public MainFrame(String rootDir) throws IOException {
    	setup();
    	frame = new JFrame("Bilder");
    	
        Container parent = imagePanel.getParent();
        parent.remove(imagePanel);
        imagePanel = new NavigableImagePanel();
        parent.add(imagePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));

        frame.addWindowListener(new ClosingWindowListener());

        description.setLineWrap(true);
        description.setFont(new Font(null, Font.BOLD, 12));
        title.setFont(new Font(null, Font.BOLD, 16));

        fileList.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    degree += 90;
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    degree -= 90;
                } else if (e.getKeyChar() == 'm') {
                	currentElement.mark();
                	fileList.repaint();
                } else {
                    return;
                }
                if (DEBUG) System.out.println("Degree = "+ degree);
                imagePanel.setDegree(degree);
                imagePanel.repaint();
            }
        });
        model = new DefaultListModel();
        elements = Element.readElements(new File(rootDir));
        for (int i = 0; i < elements.length; i++) {
        	if (DEBUG)
        		System.out.println(i + ": " + elements[i].toStringDebug());
            model.add(i, elements[i]);
        }
        fileList.setModel(model);
        fileList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                try {
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    if (currentElement != null) {
                        // we need to free image memory allocated by MediaTracker
                        currentElement.flushImage();

                        // save data if anything was changed
                        currentElement.save(title.getText(), description.getText());
                    }
                    currentElement = (Element) fileList.getSelectedValue();
                    description.setText(currentElement.getDesc());
                    title.setText(currentElement.getTitle());
                    imageComp = new ImageLoader(currentElement.getFileName());
                    BufferedImage image = imageComp.getImage();
                    currentElement.setImage(image);
                    ((NavigableImagePanel) imagePanel).setImage(image);

                    if (DEBUG)
                    	System.out.println("currentElement=" + currentElement.toStringDebug());
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
        fileList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            	if (DEBUG)
            		System.out.println("E=" + e);
                if (e.getButton() == MouseEvent.BUTTON3 ||
                    e.getClickCount() == 2) {
                    currentElement.mark();
                    fileList.repaint();
                }
            }
        });

        fileList.setCellRenderer(new ElementListCellRenderer());

        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (DEBUG)
                	System.out.println("Delete: " + currentElement.getFileName());
                if (!currentElement.deleteJpgFile()) {
                    JOptionPane.showMessageDialog(mainPanel, "Can not delete picture " +
                                                             currentElement.getFileName());
                    return;
                }
                int selectionIndex = fileList.getSelectedIndex();
                if (model.size() > selectionIndex + 1) {
                    fileList.setSelectedIndex(selectionIndex + 1);
                } else if (selectionIndex != 0) {
                    fileList.setSelectedIndex(selectionIndex - 1);
                }
                model.remove(selectionIndex);
            }
        });
        fileList.setSelectedIndex(0);
        frame.setContentPane(mainPanel);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        GraphicsEnvironment ge = 
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle bounds = ge.getMaximumWindowBounds();
		frame.setSize(new Dimension(bounds.width, bounds.height));
		frame.setVisible(true);
    }

    private void setup() {
    	final File dir = new File(OUTDIR);
        if (dir.exists() &&
        	!dir.renameTo(new File(dir.toString() + "." + System.currentTimeMillis()))) {
            System.err.println("Renaming of " + OUTDIR + " failed");
            System.exit(1);
        }
        dir.mkdir();
	}

	{
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // !!! IMPORTANT !!!
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * !!! IMPORTANT !!!
     * DO NOT edit this method OR call it in your code!
     */
    private void $$$setupUI$$$() {
        mainPanel = new JSplitPane();
        mainPanel.setContinuousLayout(false);
        mainPanel.setDividerLocation(300);
        mainPanel.setDividerSize(10);
        mainPanel.setOneTouchExpandable(true);
        mainPanel.setResizeWeight(0.0);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setLeftComponent(panel1);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(0, 150), null, null));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Bilder"));
        fileList = new JList();
        scrollPane1.setViewportView(fileList);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(0, -1), new Dimension(300, -1), null));
        panel2.setBorder(BorderFactory.createTitledBorder("Beschreibung"));
        description = new JTextArea();
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        panel2.add(description, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(0, 150), null, null));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setRightComponent(panel3);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null));
        panel4.setBorder(BorderFactory.createTitledBorder("Titel und Bild"));
        title = new JTextField();
        title.setHorizontalAlignment(0);
        title.setText("");
        panel4.add(title, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(800, -1), null));
        imagePanel = new NavigableImagePanel();
        panel4.add(imagePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(800, 600), new Dimension(800, 600), null));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null));
        delete = new JButton();
        delete.setText("Löschen");
        panel5.add(delete, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
    }

    public class ClosingWindowListener extends WindowAdapter {
		public void windowClosed(WindowEvent e) {
            final File dir = new File(OUTDIR);
            if (!dir.renameTo(new File(dir.toString() + "." + System.currentTimeMillis()))) {
                System.err.println("Renaming of " + OUTDIR + " failed");
            }
            dir.mkdir();
            for (int i = 0; i < model.size(); i++) {
                Element elem = (Element) model.getElementAt(i);
                try {
                    elem.copy(dir);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
	    System.out.println("Marked files saved in " + OUTDIR);
        }
    }
}
