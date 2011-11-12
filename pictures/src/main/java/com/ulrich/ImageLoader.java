/**
 * $Id: ImageLoader.java,v 1.4 2007/12/29 18:47:46 klaus Exp $
 */
package com.ulrich;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLoader extends Component {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6299225708761812733L;
	private BufferedImage image;

	public ImageLoader(String fileName) {
		try {
			image = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}

		// Toolkit toolkit = Toolkit.getDefaultToolkit();
		// image = toolkit.getImage(fileName);
		// MediaTracker mediaTracker = new MediaTracker(this);
		// mediaTracker.addImage(image, 0);
		// try {
		// mediaTracker.waitForID(0);
		// } catch (InterruptedException ie) {
		// System.err.println(ie);
		// System.exit(1);
		// }
	}

	public BufferedImage getImage() {
		return image;
	}
}
