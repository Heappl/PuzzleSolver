import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;


public class DeshredderDisplay extends JFrame {
	private static final long serialVersionUID = 6707804995242868875L;
	
	JPanel imagePanel = new JPanel();
	JFileChooser fileChooser = new JFileChooser();
	BufferedImage[] images = new BufferedImage[0];
	
	class ImageMover implements MouseMotionListener, MouseWheelListener {
		Point mouseOnImage = new Point(0, 0);
		Point2D originalTopLeft;
		Point2D originalTopRight;
		Point2D originalBottomLeft;
		Point2D originalBottomRight;
		BufferedImage image;
		
		public ImageMover(BufferedImage image) {
			this.image = image;
			this.originalTopLeft = new Point2D.Double(0, 0);
			this.originalTopRight = new Point2D.Double(image.getWidth(), 0);
			this.originalBottomLeft = new Point2D.Double(0, image.getHeight());
			this.originalBottomRight = new Point2D.Double(image.getWidth(), image.getHeight());
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if (mouseOnImage.x == 0) {
				mouseOnImage.x = e.getX();
				mouseOnImage.y = e.getY();
			}
			Point point = imagePanel.getMousePosition();
			((Component)e.getSource()).setLocation(point.x - mouseOnImage.x, point.y - mouseOnImage.y);
			DeshredderDisplay.this.repaint();
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			mouseOnImage.x = 0;
			mouseOnImage.y = 0;
		}
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (mouseOnImage.x != 0) {
				
				AffineTransform at = new AffineTransform();
				at.rotate(Math.PI / 10.0 * e.getPreciseWheelRotation(), image.getWidth(null) / 2, image.getHeight(null) / 2);
				at.preConcatenate(transformOriginalPoints(at, image));
				BufferedImage newImage = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR).filter(image, null);
				
				double ymax = Math.max(Math.max(originalBottomLeft.getY(), originalBottomRight.getY()),
									   Math.max(originalTopLeft.getY(), originalTopRight.getY()));
				double xmax = Math.max(Math.max(originalBottomLeft.getX(), originalBottomRight.getX()),
						 			   Math.max(originalTopLeft.getX(), originalTopRight.getX()));
				int nextWidth =  Math.min((int)xmax, newImage.getWidth());
				int nextHeight =  Math.min((int)ymax, newImage.getHeight());
				this.image = newImage.getSubimage(0, 0, nextWidth, nextHeight);
				((JLabel)e.getSource()).setIcon(new ImageIcon(this.image.getScaledInstance(this.image.getWidth() / 4,
																						   this.image.getHeight() / 4,
																						   0)));
				System.err.println(this.image.getHeight() + " " + this.image.getWidth());
				Point point = imagePanel.getMousePosition();
				((Component)e.getSource()).setLocation(point.x, point.y);
				DeshredderDisplay.this.repaint();
			}
		}
		

		private AffineTransform transformOriginalPoints(AffineTransform at, BufferedImage bi) {
			this.originalBottomLeft = at.transform(this.originalBottomLeft, null);
			this.originalBottomRight = at.transform(this.originalBottomRight, null);
			this.originalTopLeft = at.transform(this.originalTopLeft, null);
			this.originalTopRight = at.transform(this.originalTopRight, null);

			double ytrans = Math.min(Math.min(originalBottomLeft.getY(), originalBottomRight.getY()),
									 Math.min(originalTopLeft.getY(), originalTopRight.getY()));
			double xtrans = Math.min(Math.min(originalBottomLeft.getX(), originalBottomRight.getX()),
					 				 Math.min(originalTopLeft.getX(), originalTopRight.getX()));
				
			AffineTransform tat = new AffineTransform();
			tat.translate(-xtrans, -ytrans);
			this.originalBottomLeft = tat.transform(this.originalBottomLeft, null);
			this.originalBottomRight = tat.transform(this.originalBottomRight, null);
			this.originalTopLeft = tat.transform(this.originalTopLeft, null);
			this.originalTopRight = tat.transform(this.originalTopRight, null);
			return tat;
		}
	}
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem openFileMenuItem = new JMenuItem("Open File");
		add(menuBar);
		menuBar.add(menu);
		menu.add(openFileMenuItem);
		openFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fileChooser.showOpenDialog(DeshredderDisplay.this) == JFileChooser.APPROVE_OPTION) {
					images = new ShreddedImagePiecesRecognizer(fileChooser.getSelectedFile()).retrievePieces();
					displayPieces();
				}
			}
		});
		
		setJMenuBar(menuBar);
	}
	
	private void displayPieces() {
		for (int i = 0; i < images.length; ++i) {
			

//			Graphics2D graphics = (Graphics2D)images[i].getGraphics();
//			graphics.transform(AffineTransform.getRotateInstance(Math.PI / 4,
//					images[i].getWidth(null) / 2, images[i].getHeight(null) / 2));
//			graphics.drawImage(images[i], 0, 0, null);
			
			ImageIcon imageIcon = new ImageIcon(images[i].getScaledInstance(
					images[i].getWidth() / 4, images[i].getHeight() / 4, 0));
			JLabel imageLabel = new JLabel();
			ImageMover imageMover = new ImageMover(images[i]);
			imageLabel.addMouseMotionListener(imageMover);
			imageLabel.addMouseWheelListener(imageMover);
			imageLabel.setIcon(imageIcon);
			imagePanel.add(imageLabel, BorderLayout.CENTER);
		}
		this.setVisible(true);
	}
	
	public DeshredderDisplay() {
		createMenu();
		setMinimumSize(new Dimension(1200, 700));
		imagePanel.setBackground(Color.BLUE);
		add(imagePanel);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
}
