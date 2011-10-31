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
	ShreddedPiecesStateStorage state;
	JLabel imageLabel;
	
	public ImageMover(BufferedImage image,
					  JPanel imagePanel,
					  JFrame mainFrame,
					  ShreddedPiecesStateStorage state,
					  double angle,
					  JLabel imageLabel) {
		this.image = image;
		this.imagePanel = imagePanel;
		this.mainFrame = mainFrame;
		this.state = state;
		this.rotationAngle = angle;
		if (angle != 0) updateAngle();
		this.imageLabel = imageLabel;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (mouseOnImage.x == 0) {
			mouseOnImage.x = e.getX();
			mouseOnImage.y = e.getY();
		}
		Point point = imagePanel.getMousePosition();
		Point newLocation = new Point(point.x - mouseOnImage.x, point.y - mouseOnImage.y);
		((Component)e.getSource()).setLocation(newLocation);
		state.setImageLocation(image, newLocation);
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
			this.updateAngle();
		}
	}
	
	private void updateAngle() {
		AffineTransform at = new AffineTransform();
		at.rotate(this.rotationAngle, image.getWidth(null) / 2, image.getHeight(null) / 2);
		at.preConcatenate(transformOriginalPoints(at, image));
		BufferedImage newImage = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR).filter(image, null);
		
		int nextWidth =  newImage.getWidth() / 4;
		int nextHeight = newImage.getHeight() / 4;
		((ImageIcon)imageLabel.getIcon()).setImage(newImage.getScaledInstance(nextWidth, nextHeight, 0));
		imageLabel.setSize(nextWidth, nextHeight);
		state.setImageAngle(image, rotationAngle);
		mainFrame.repaint();
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
