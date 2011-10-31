import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

class PieceState {
	public Point location;
	public double angle;
}

public class ShreddedPiecesStateStorage {
	
	Hashtable<BufferedImage, Point> locations = new Hashtable<BufferedImage, Point>();
	Hashtable<BufferedImage, Double> angles = new Hashtable<BufferedImage, Double>();
	
	public ShreddedPiecesStateStorage() {
	}
	
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
	}

	public void readState(File selectedFile) {
	}
}
