import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

class PieceState {
	public Point location;
	public double angle;
}

public class ShreddedPiecesStateStorage {
	
	Hashtable<BufferedImage, Point> locations = new Hashtable<BufferedImage, Point>();
	Hashtable<BufferedImage, Double> angles = new Hashtable<BufferedImage, Double>();
	
	public void setImageLocation(BufferedImage image, Point location) {
		locations.put(image, location);
	}
	
	public void setImageAngle(BufferedImage image, double angle) {
		angles.put(image, angle);
	}
	
	public BufferedImage[] getImages() {
		return locations.keySet().toArray(new BufferedImage[0]);
	}
	
	public Point getLocation(BufferedImage image) {
		return locations.get(image);
	}
	
	public double getAngle(BufferedImage image) {
		if (angles.containsKey(image)) return angles.get(image);
		return 0;
	}
	
	public void save(File dir) {
		System.err.println(dir.getAbsolutePath());
		
		dir.mkdir();
		String[] data = new String[locations.size()];
		int index = 0;
		for (BufferedImage image : locations.keySet()) {
			Point location = getLocation(image);
			double angle = getAngle(image);
			String imagePath = dir.getAbsolutePath() + "/image" +  index + ".png";
			data[index] = imagePath + " x=" + location.x + " y=" + location.y + " angle=" + angle + "\n";
			index++;
			File outputFile = new File(imagePath);
			try {
				ImageIO.write(image, "png", outputFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileWriter save = new FileWriter(dir.getAbsoluteFile() + "/save.txt");
			BufferedWriter writer = new BufferedWriter(save);
			for (String line : data) writer.write(line);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void readState(File selectedFile) {
		
	}
}
