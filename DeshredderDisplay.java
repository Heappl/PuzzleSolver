import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
	JFileChooser saveDirChooser = new JFileChooser();
	JFileChooser readSaveDirChooser = new JFileChooser();
	BufferedImage[] images = new BufferedImage[0];
	JLabel[] imageLabels = new JLabel[0];
	ShreddedPiecesStateStorage state = new ShreddedPiecesStateStorage();
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		add(menuBar);
		menuBar.add(menu);

		JMenuItem openFileMenuItem = new JMenuItem("Open File");
		menu.add(openFileMenuItem);
		openFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (fileChooser.showOpenDialog(DeshredderDisplay.this) == JFileChooser.APPROVE_OPTION) {
					images = new ShreddedImagePiecesRecognizer(fileChooser.getSelectedFile()).retrievePieces();
					displayPieces(false);
				}
			}
		});

		saveDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		JMenuItem saveMenuItem = new JMenuItem("Save to directory");
		menu.add(saveMenuItem);
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (saveDirChooser.showOpenDialog(DeshredderDisplay.this) == JFileChooser.APPROVE_OPTION) {
					state.save(saveDirChooser.getSelectedFile());
				}
			}
		});

		readSaveDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		JMenuItem loadMenuItem = new JMenuItem("Read saved data");
		menu.add(loadMenuItem);
		loadMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (readSaveDirChooser.showOpenDialog(DeshredderDisplay.this) == JFileChooser.APPROVE_OPTION) {
					state.readState(readSaveDirChooser.getSelectedFile());
					images = state.getImages();
					displayPieces(true);
				}
			}
		});
		
		setJMenuBar(menuBar);
	}
	
	private void displayPieces(boolean fromFile) {
		System.err.println("TESTING " + this.images.length);
		imageLabels = new JLabel[images.length];
		for (int i = 0; i < images.length; ++i) {
			ImageIcon imageIcon = new ImageIcon(images[i].getScaledInstance(
					images[i].getWidth() / 4, images[i].getHeight() / 4, 0));
			JLabel imageLabel = new JLabel();
			imageLabel.setIcon(imageIcon);
			ImageMover imageMover = new ImageMover(images[i], imagePanel, this, state, state.getAngle(images[i]), imageLabel);
			imageLabel.addMouseMotionListener(imageMover);
			imageLabel.addMouseWheelListener(imageMover);
			imagePanel.add(imageLabel, BorderLayout.CENTER);
			imageLabels[i] = imageLabel;
		}
		this.setVisible(true);
		if (fromFile){
			for (int i = 0; i < images.length; ++i) {
				imageLabels[i].setLocation(state.getLocation(images[i]));
			}
			this.repaint();
		} else {
			for (int i = 0; i < images.length; ++i) {
				state.setImageLocation(images[i], imageLabels[i].getLocation());
			}
		}
		
		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
				for (int i = 0; i < images.length; ++i) {
					imageLabels[i].setLocation(state.getLocation(images[i]));
				}
			}
			@Override
			public void componentResized(ComponentEvent e) {
				for (int i = 0; i < images.length; ++i) {
					imageLabels[i].setLocation(state.getLocation(images[i]));
				}
			}
			@Override
			public void componentMoved(ComponentEvent e) {
				for (int i = 0; i < images.length; ++i) {
					imageLabels[i].setLocation(state.getLocation(images[i]));
				}
			}
			@Override
			public void componentHidden(ComponentEvent e) {
				System.err.println("componentHidden");
			}
		});
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
