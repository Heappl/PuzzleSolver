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
		int mouseOnImageX = 0;
		int mouseOnImageY = 0;
		BufferedImage image;
		
		public ImageMover(BufferedImage image) {
			this.image = image;
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if (mouseOnImageX == 0) {
				mouseOnImageX = e.getX();
				mouseOnImageY = e.getY();
			}
			Point point = imagePanel.getMousePosition();
			((Component)e.getSource()).setLocation(point.x - mouseOnImageX, point.y - mouseOnImageY);
			DeshredderDisplay.this.repaint();
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			mouseOnImageX = 0;
			mouseOnImageY = 0;
		}
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (mouseOnImageX != 0) {
				
				AffineTransform at = new AffineTransform();
				at.scale(4.0, 4.0);
				at.rotate(Math.PI / 4, image.getWidth(null) / 2, image.getHeight(null) / 2);
				BufferedImage newImage = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR).filter(image, null);
				
				((JLabel)e.getSource()).setIcon(new ImageIcon(image));
				DeshredderDisplay.this.repaint();
			}
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