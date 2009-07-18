/**
 * $Id: MainFrame.java,v 1.1.1.1 2008/03/21 13:38:38 klaus Exp $
 */
package com.danet.ulrich.pictures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
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
    private static final Boolean DEBUG = new Boolean(DEBUG_STR);
	
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
            frame = new JFrame("Bilder");
            MainFrame mainFrame = new MainFrame(args[0]);
            frame.setContentPane(mainFrame.mainPanel);

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            GraphicsEnvironment ge = 
				GraphicsEnvironment.getLocalGraphicsEnvironment();
			Rectangle bounds = ge.getMaximumWindowBounds();
			frame.setSize(new Dimension(bounds.width, bounds.height));
			frame.setVisible(true);		
        } else {
            System.err.println("Usage: MainFrame <root-dir-of-pictures>");
        }
    }

    public MainFrame(String rootDir) throws IOException {
    	setup();
        Container parent = imagePanel.getParent();
        parent.remove(imagePanel);
        imagePanel = new NavigableImagePanel();
        parent.add(imagePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));

        frame.addWindowListener(new ClosingWindowListener());

        //        frame.addComponentListener(new ComponentAdapter() {
        //            public void componentResized(ComponentEvent e) {
        //                imagePanel.repaint();
        //            }
        //        });

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

    class ImagePanel extends JPanel {
		private static final long serialVersionUID = -1197054716741676059L;
		
		private Image image;
        private boolean inZoom = false;
        private Rectangle zoomRect = new Rectangle(0, 0, 0, 0);;

        public ImagePanel() {
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (DEBUG) System.out.println("XXX: mouseClicked: " + e);
                    repaint();
                }

                public void mousePressed(MouseEvent e) {
                    if (DEBUG) System.out.println("XXX: mousePressed: " + e);
                    if (e.getClickCount() == 2) {
                        zoomRect.x = 0;
                        zoomRect.y = 0;
                        zoomRect.width = getSize().width;
                        zoomRect.height = getSize().height;
                        inZoom = false;
                        return;
                    }
                    e.getPoint();
                    //inZoom = true;
                    zoomRect.x = e.getX();
                    zoomRect.y = e.getY();
                    zoomRect.width = 1;
                    zoomRect.height = 1;
                    if (DEBUG) System.out.println("mousePressed: zommRect[x=" + zoomRect.x + ",y=" + zoomRect.y +
                                       ",width=" + zoomRect.width + ",height=" + zoomRect.height + "]");
                    drawZoomRect();
                }

                public void mouseReleased(MouseEvent e) {
                    if (DEBUG) System.out.println("XXX: mouseReleased: " + e);
                    if (inZoom) {
                        zoomRect.width = e.getX() - zoomRect.x;
                        zoomRect.height = e.getY() - zoomRect.y;
                        if (DEBUG)
                        	System.out.println("mouseReleased: zommRect[x=" + zoomRect.x + ",y=" + zoomRect.y +
                        			",width=" + zoomRect.width + ",height=" + zoomRect.height + "]");
                        repaint();
                    }
//                    inZoom = false;
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    inZoom = true;
                    int nx = e.getX();
                    int ny = e.getY();
                    nx = nx - zoomRect.x;
                    ny = ny - zoomRect.y;
                    if (nx < 0)
                        nx = nx * (-1);
                    if (ny < 0)
                        ny = ny * (-1);
                    drawZoomRect();
                    zoomRect.width = nx;
                    zoomRect.height = ny;
//                    System.out.println("mouseDragged: zommRect[x=" + zoomRect.x + ",y=" + zoomRect.y +
//                                       ",width=" + zoomRect.width + ",height=" + zoomRect.height + "]");
                    drawZoomRect();
                }

            });
        }

        public void drawZoomRect() {
            Graphics2D g = (Graphics2D) (getGraphics());
            g.setColor(Color.red);
            g.setStroke(new BasicStroke(1.0f));
            g.setXORMode(Color.white);
            g.drawRect(zoomRect.x, zoomRect.y, zoomRect.width, zoomRect.height);
            g.setPaintMode();
        }

        public void add(Image image) {
            this.image = image;
            repaint();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g); //paint background

            Graphics2D g2 = (Graphics2D) g;
            AffineTransform transSaved = g2.getTransform();

            int w = getSize().width;
            int h = getSize().height;

            int imageWidth, imageHeight;
            double scaleWidth, scaleHeight, scaleZoom = 1.0d;
            imageWidth = image.getWidth(this);
            imageHeight = image.getHeight(this);
            scaleWidth = (double) w / (double) imageWidth;
            scaleHeight = (double) h / (double) imageHeight;
            double scale = Math.min(scaleWidth, scaleHeight);
            if (inZoom) {
//                imageWidth = zoomRect.width;
//                imageHeight = zoomRect.height;

                scaleWidth =  (double) w / (double) zoomRect.width;
                scaleHeight = (double) h / (double) zoomRect.height;
                scaleZoom = Math.min(scaleWidth, scaleHeight);
            }
            if (DEBUG)
            	System.out.println("inZoom=" + inZoom + ", scale=" + scale + ", scaleZoom=" + scaleZoom +
            			", H=" + h + ", w=" + w +
            			", imgH=" + imageHeight + ", imgW=" + imageWidth +
            			", zoomRect.x=" + zoomRect.x + ", zoomRect.y=" + zoomRect.y +
            			", zoomRect.heigth=" + zoomRect.height + ", zoomRect.width=" + zoomRect.width);

            if (inZoom) {
                AffineTransform moveTr = AffineTransform.getTranslateInstance(-zoomRect.x,
                                                                              -zoomRect.y);
                g2.transform(moveTr);
//                transform.concatenate(moveTr);
            }

            AffineTransform scaleTr = AffineTransform.getScaleInstance(scale, scale);
            g2.transform(scaleTr);
//            AffineTransform transform = new AffineTransform();

            if (inZoom) {
                AffineTransform scaleZoomTr = AffineTransform.getScaleInstance(scaleZoom, scaleZoom);
                g2.transform(scaleZoomTr);
            }

            double dx = (imageWidth / 2);
            double dy = (imageHeight / 2);
            AffineTransform rotateTr = AffineTransform.getRotateInstance(Math.toRadians(degree), dx, dy);
            g2.transform(rotateTr);

            //Now draw the imageComp scaled.
            //g.drawImage(image, 0, 0, (int) (imageWidth / scale), (int) (imageHeight / scale), this);
            g.drawImage(image, zoomRect.x, zoomRect.y, imageWidth, imageHeight, this);

            g2.setTransform(transSaved);
            g2.dispose();

            //            double w = getSize().width;
            //            double h = getSize().height;
            //            double imageHeight = image.getHeight(this);
            //            double imageWidth = image.getWidth(this);
            //
            //            double scaleWidth = w / imageWidth;
            //            double scaleHeight = h / imageHeight;
            //            double scale = Math.max(scaleWidth, scaleHeight);
            //
            //            AffineTransform scaleTr = new AffineTransform();
            //            scaleTr.scale(scale, scale);
            //
            //            AffineTransform rotateTr = new AffineTransform();
            //            rotateTr.rotate(Math.toRadians(degree));
            //
            //            AffineTransform toCenterAt = new AffineTransform();
            //
            //
            //            toCenterAt.concatenate(rotateTr);
            //            //toCenterAt.concatenate(scaleTr);
            //            //toCenterAt.translate(-(w/2), -(h/2));
            //
            //            //toCenterAt.translate(+(imageWidth/2), +(imageHeight/2));
            //
            //            System.out.println("degree="+degree+ ", imageWidth="+imageWidth+", imageHeight="+imageHeight);
            //
            //            //trans.translate(-((imageWidth/scale)/2), -((imageHeight/scale)/2));
            //            //toCenterAt.translate(-((w)), -((h)));
            //            g2.setTransform(toCenterAt);
            //
            //            //Draw imageComp rotateTr its natural size first.
            //            //g.drawImage(image, 0, 0, this);
            //
            //            //Now draw the imageComp scaled.
            //            g.drawImage(image, 0, 0, (int) (imageWidth * scale), (int) (imageHeight * scale), this);
            //            //g.drawImage(image, 0, 0, (int) (imageWidth), (int) (imageHeight), this);
            //            g2.setTransform(transSaved);
        }
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
