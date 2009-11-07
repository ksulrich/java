package com.danet.ulrich.pictures;

/**  
 * Unused currently
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

class ImagePanel extends JPanel {
	private static final long serialVersionUID = -1197054716741676059L;

	private Image image;
	private boolean inZoom = false;
	private Rectangle zoomRect = new Rectangle(0, 0, 0, 0);;

	public ImagePanel() {
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (MainFrame.DEBUG)
					System.out.println("XXX: mouseClicked: " + e);
				repaint();
			}

			public void mousePressed(MouseEvent e) {
				if (MainFrame.DEBUG)
					System.out.println("XXX: mousePressed: " + e);
				if (e.getClickCount() == 2) {
					zoomRect.x = 0;
					zoomRect.y = 0;
					zoomRect.width = getSize().width;
					zoomRect.height = getSize().height;
					inZoom = false;
					return;
				}
				e.getPoint();
				// inZoom = true;
				zoomRect.x = e.getX();
				zoomRect.y = e.getY();
				zoomRect.width = 1;
				zoomRect.height = 1;
				if (MainFrame.DEBUG)
					System.out.println("mousePressed: zommRect[x=" + zoomRect.x
							+ ",y=" + zoomRect.y + ",width=" + zoomRect.width
							+ ",height=" + zoomRect.height + "]");
				drawZoomRect();
			}

			public void mouseReleased(MouseEvent e) {
				if (MainFrame.DEBUG)
					System.out.println("XXX: mouseReleased: " + e);
				if (inZoom) {
					zoomRect.width = e.getX() - zoomRect.x;
					zoomRect.height = e.getY() - zoomRect.y;
					if (MainFrame.DEBUG)
						System.out.println("mouseReleased: zommRect[x="
								+ zoomRect.x + ",y=" + zoomRect.y + ",width="
								+ zoomRect.width + ",height=" + zoomRect.height
								+ "]");
					repaint();
				}
				// inZoom = false;
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
				// System.out.println("mouseDragged: zommRect[x=" + zoomRect.x +
				// ",y=" + zoomRect.y +
				// ",width=" + zoomRect.width + ",height=" + zoomRect.height +
				// "]");
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
		super.paintComponent(g); // paint background

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
			// imageWidth = zoomRect.width;
			// imageHeight = zoomRect.height;

			scaleWidth = (double) w / (double) zoomRect.width;
			scaleHeight = (double) h / (double) zoomRect.height;
			scaleZoom = Math.min(scaleWidth, scaleHeight);
		}
		if (MainFrame.DEBUG)
			System.out.println("inZoom=" + inZoom + ", scale=" + scale
					+ ", scaleZoom=" + scaleZoom + ", H=" + h + ", w=" + w
					+ ", imgH=" + imageHeight + ", imgW=" + imageWidth
					+ ", zoomRect.x=" + zoomRect.x + ", zoomRect.y="
					+ zoomRect.y + ", zoomRect.heigth=" + zoomRect.height
					+ ", zoomRect.width=" + zoomRect.width);

		if (inZoom) {
			AffineTransform moveTr = AffineTransform.getTranslateInstance(
					-zoomRect.x, -zoomRect.y);
			g2.transform(moveTr);
			// transform.concatenate(moveTr);
		}

		AffineTransform scaleTr = AffineTransform
				.getScaleInstance(scale, scale);
		g2.transform(scaleTr);
		// AffineTransform transform = new AffineTransform();

		if (inZoom) {
			AffineTransform scaleZoomTr = AffineTransform.getScaleInstance(
					scaleZoom, scaleZoom);
			g2.transform(scaleZoomTr);
		}

		double dx = (imageWidth / 2);
		double dy = (imageHeight / 2);
		AffineTransform rotateTr = AffineTransform.getRotateInstance(Math
				.toRadians(degree()), dx, dy);
		g2.transform(rotateTr);

		// Now draw the imageComp scaled.
		// g.drawImage(image, 0, 0, (int) (imageWidth / scale), (int)
		// (imageHeight / scale), this);
		g.drawImage(image, zoomRect.x, zoomRect.y, imageWidth, imageHeight,
				this);

		g2.setTransform(transSaved);
		g2.dispose();

		// double w = getSize().width;
		// double h = getSize().height;
		// double imageHeight = image.getHeight(this);
		// double imageWidth = image.getWidth(this);
		//
		// double scaleWidth = w / imageWidth;
		// double scaleHeight = h / imageHeight;
		// double scale = Math.max(scaleWidth, scaleHeight);
		//
		// AffineTransform scaleTr = new AffineTransform();
		// scaleTr.scale(scale, scale);
		//
		// AffineTransform rotateTr = new AffineTransform();
		// rotateTr.rotate(Math.toRadians(degree));
		//
		// AffineTransform toCenterAt = new AffineTransform();
		//
		//
		// toCenterAt.concatenate(rotateTr);
		// //toCenterAt.concatenate(scaleTr);
		// //toCenterAt.translate(-(w/2), -(h/2));
		//
		// //toCenterAt.translate(+(imageWidth/2), +(imageHeight/2));
		//
		// System.out.println("degree="+degree+
		// ", imageWidth="+imageWidth+", imageHeight="+imageHeight);
		//
		// //trans.translate(-((imageWidth/scale)/2), -((imageHeight/scale)/2));
		// //toCenterAt.translate(-((w)), -((h)));
		// g2.setTransform(toCenterAt);
		//
		// //Draw imageComp rotateTr its natural size first.
		// //g.drawImage(image, 0, 0, this);
		//
		// //Now draw the imageComp scaled.
		// g.drawImage(image, 0, 0, (int) (imageWidth * scale), (int)
		// (imageHeight * scale), this);
		// //g.drawImage(image, 0, 0, (int) (imageWidth), (int) (imageHeight),
		// this);
		// g2.setTransform(transSaved);
	}
}