import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;


public class ShreddedImagePiecesRecognizer {
	Color[] pixels = null;
	Color averageColor;
	int averageDistance;
	int width;
	int height;
	
	public ShreddedImagePiecesRecognizer(File file) {
		try {
			BufferedImage image = ImageIO.read(file);
			System.err.println(file.getAbsolutePath() + ": " + image);
			
			long averageRed = 0;
			long averageGreen = 0;
			long averageBlue = 0;
			width = image.getWidth();
			height = image.getHeight();
			int size = width * height;
			pixels = new Color[size];
			for (int r = 0; r < height; ++r) {
				for (int c = 0; c < width; ++c) {
					pixels[r * width + c] = new Color(image.getRGB(c, r));
					averageRed += pixels[r * width + c].getRed();
					averageGreen += pixels[r * width + c].getGreen();
					averageBlue += pixels[r * width + c].getBlue();
				}
			}
			averageColor = new Color((int)(averageRed / (long)size),
									 (int)(averageGreen / (long)size),
									 (int)(averageBlue / (long)size));
			
			long sumDistance = 0;
			for (Color pixel : pixels) {
				sumDistance += colorDistance(pixel, averageColor);
			}
			averageDistance = (int) (sumDistance / pixels.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int colorDistance(Color first, Color second) {
		return (first.getRed() - second.getRed()) * (first.getRed() - second.getRed())
			   + (first.getGreen() - second.getGreen()) * (first.getGreen() - second.getGreen());
	}
	
	private boolean isShreddedPiecePixel(Color pixel) {
		return colorDistance(pixel, averageColor) > averageDistance;
	}
	
	private void checkPixel(int r, int c, LinkedList<Integer[]> next, boolean[] seen) {
		if ((!seen[r * width + c] == true) && isShreddedPiecePixel(pixels[r * width + c])) {
			next.add(new Integer[]{r, c});
		}
		seen[r * width + c] = true;
	}
	
	private BufferedImage retrievePiece(int startingPixel, boolean[] seen) {
		System.err.println("Retrieving piece");
		LinkedList<Integer[]> piecePixels = new LinkedList<Integer[]>();
		LinkedList<Integer[]> next = new LinkedList<Integer[]>();
		next.add(new Integer[]{startingPixel / width, startingPixel % width});
		
		int minRow = startingPixel / width;
		int maxRow = startingPixel / width;
		int minCol = startingPixel % width;
		int maxCol = startingPixel % width;
		while (!next.isEmpty()) {
			Integer[] pixel = next.pollFirst();
			if (minRow > pixel[0]) minRow = pixel[0];
			if (maxRow < pixel[0]) maxRow = pixel[0];
			if (minCol > pixel[1]) minCol = pixel[1];
			if (maxCol < pixel[1]) maxCol = pixel[1];
			if (pixel[0] + 1 < height) checkPixel(pixel[0] + 1, pixel[1], next, seen);
			if (pixel[0] - 1 > 0) checkPixel(pixel[0] - 1, pixel[1], next, seen);
			if (pixel[1] + 1 < width) checkPixel(pixel[0], pixel[1] + 1, next, seen);
			if (pixel[1] - 1 > 0) checkPixel(pixel[0], pixel[1] - 1, next, seen);
			piecePixels.add(pixel);
		}
		if (piecePixels.size() < 200) return null;
		System.err.println("Piece recognized " + piecePixels.size() + " pixels");
		
		int pieceWidth = maxCol - minCol + 1;
		int pieceHeight = maxRow - minRow + 1;
		BufferedImage piece = new BufferedImage(pieceWidth, pieceHeight, BufferedImage.TYPE_INT_ARGB);
		piece.setRGB(0, 0, pieceWidth, pieceHeight, new int[pieceWidth * pieceHeight], 0, pieceWidth);
		for (Integer[] pixel : piecePixels) {
			int newRow = pixel[0] - minRow;
			int newCol = pixel[1] - minCol;
			piece.setRGB(newCol, newRow, this.pixels[pixel[0] * this.width + pixel[1]].getRGB());
		}
		return piece;
	}
	
	public BufferedImage[] retrievePieces() {
		boolean[] seen = new boolean[pixels.length];
		ArrayList<BufferedImage> pieces = new ArrayList<BufferedImage>();
		
		for (int i = 0; i < pixels.length; ++i) {
			if (seen[i] == true) continue;
			seen[i] = true;
			if (isShreddedPiecePixel(pixels[i])) {
				BufferedImage piece = retrievePiece(i, seen);
				if (piece != null) pieces.add(piece);
				System.err.println("Piece stored");
			}
		}
		
		System.err.println("Recognized " + pieces.size() + " pieces");
		return pieces.toArray(new BufferedImage[0]);
	}
}

