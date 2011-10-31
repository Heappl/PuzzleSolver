import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public 

class ImageMover implements MouseMotionListener, MouseWheelListener {
	Point mouseOnImage = new Point(0, 0);
	BufferedImage image;
	double rotationAngle = 0;
	double rotationStep = Math.PI / 100.0;
	JPanel imagePanel;
	JFrame mainFrame;
	
	public ImageMover(BufferedImage image, JPanel imagePanel, JFrame mainFrame) {
		this.image = image;
		this.imagePanel = imagePanel;
		this.mainFrame = mainFrame;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (mouseOnImage.x == 0) {
			mouseOnImage.x = e.getX();
			mouseOnImage.y = e.getY();
		}
		Point point = imagePanel.getMousePosition();
		((Component)e.getSource()).setLocation(point.x - mouseOnImage.x, point.y - mouseOnImage.y);
		mainFrame.repaint();
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseOnImage.x = 0;
		mouseOnImage.y = 0;
		this.rotationStep = Math.PI / 100.0;
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (mouseOnImage.x != 0) {
			this.rotationAngle += this.rotationStep * e.getPreciseWheelRotation();
			this.rotationStep = Math.min(this.rotationStep * 1.25, Math.PI / 4);
			
			AffineTransform at = new AffineTransform();
			at.rotate(this.rotationAngle, image.getWidth(null) / 2, image.getHeight(null) / 2);
			at.preConcatenate(transformOriginalPoints(at, image));
			BufferedImage newImage = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR).filter(image, null);
			
			int nextWidth =  newImage.getWidth() / 4;
			int nextHeight = newImage.getHeight() / 4;
			((ImageIcon)((JLabel)e.getSource()).getIcon()).setImage(newImage.getScaledInstance(nextWidth, nextHeight, 0));
			((JLabel)e.getSource()).setSize(nextWidth, nextHeight);
			mainFrame.repaint();
		}
	}
	

	private AffineTransform transformOriginalPoints(AffineTransform at, BufferedImage image) {
		Point2D originalTopLeft = new Point2D.Double(0, 0);
		Point2D originalTopRight = new Point2D.Double(image.getWidth(), 0);
		Point2D originalBottomLeft = new Point2D.Double(0, image.getHeight());
		Point2D originalBottomRight = new Point2D.Double(image.getWidth(), image.getHeight());
		
		originalBottomLeft = at.transform(originalBottomLeft, null);
		originalBottomRight = at.transform(originalBottomRight, null);
		originalTopLeft = at.transform(originalTopLeft, null);
		originalTopRight = at.transform(originalTopRight, null);

		double ytrans = Math.min(Math.min(originalBottomLeft.getY(), originalBottomRight.getY()),
								 Math.min(originalTopLeft.getY(), originalTopRight.getY()));
		double xtrans = Math.min(Math.min(originalBottomLeft.getX(), originalBottomRight.getX()),
				 				 Math.min(originalTopLeft.getX(), originalTopRight.getX()));
			
		AffineTransform tat = new AffineTransform();
		tat.translate(-xtrans, -ytrans);
		return tat;
	}
}
