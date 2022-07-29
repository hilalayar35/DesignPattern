import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

public class DecoratorPatternTest {

	public static void main(String[] args) throws Exception {
		Image colorImage = new ColorImage("StoneHouse.png");

		decorationTest1(colorImage, "DecoratedImage1");
		decorationTest2(colorImage, "DecoratedImage2");
		decorationTest3(colorImage, "DecoratedImage3");
		decorationTest4(colorImage, "DecoratedImage4");
	}

	static void decorationTest1(Image colorImage, String outputFileName) throws Exception {
		BufferedImage image = colorImage.getImage();
		ColorImage.saveToFile(image, outputFileName, "png");
		
		System.out.println("Image is decorated. Saved as " + outputFileName + ".png");
	}
	
	static void decorationTest2(Image colorImage, String outputFileName) throws Exception {
		Rectangle rect = new Rectangle(100, 100, 650, 500);
		Image decoratedImage = new ImageGrayScaleDecorator(colorImage, rect);
		
		BufferedImage image = decoratedImage.getImage();
		ColorImage.saveToFile(image, outputFileName, "png");
		
		System.out.println("Image is decorated. Saved as " + outputFileName + ".png");
	}
	
	static void decorationTest3(Image colorImage, String outputFileName) throws Exception {
		int gridSize = 16;
		Rectangle rect = new Rectangle(100, 100, 650, 500);
		Image decoratedImage = new ImageMosaicDecorator(new ImageGrayScaleDecorator(colorImage, rect), gridSize);
		
		BufferedImage image = decoratedImage.getImage();
		ColorImage.saveToFile(image, outputFileName, "png");
		
		System.out.println("Image is decorated. Saved as " + outputFileName + ".png");
	}	
	
	static void decorationTest4(Image colorImage, String outputFileName) throws Exception {
		int gridSize = 16;
		Rectangle rect = new Rectangle(100, 100, 650, 750);
		Image decoratedImage = new ImageMosaicDecorator(colorImage, rect, gridSize);
		
		BufferedImage image = decoratedImage.getImage();
		ColorImage.saveToFile(image, outputFileName, "png");
		
		System.out.println("Image is decorated. Saved as " + outputFileName + ".png");
	}	
}


// Component
interface Image {
	 BufferedImage getImage();
}

// Concrete Component
class ColorImage implements Image {
	private BufferedImage image;
	
	ColorImage(int width, int height) {		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}
	
	ColorImage(String fileName) throws Exception {
		File imageFile = new File(fileName);
		image = ImageIO.read(imageFile);		
		
	}
	
	static void saveToFile(BufferedImage image, String fileName, String extension) throws Exception {
		File outputfile = new File(fileName + "." + extension);
		ImageIO.write(image, extension, outputfile);				
	}
	
	@Override
	public BufferedImage getImage() {
		return image;
	}
}

// Decorator
class ImageDecorator implements Image {
	protected Image image;
	protected Rectangle rect;

	ImageDecorator(Image image) {
		this.image = image;
		this.rect = new Rectangle(image.getImage());
	}

	ImageDecorator(Image image, Rectangle rect) {
		this.image = image;
		this.rect = rect;
	}
	
	@Override
	public BufferedImage getImage() {
		return image.getImage();
	}	
}

// Concrete Decorator 1
class ImageGrayScaleDecorator extends ImageDecorator {

	ImageGrayScaleDecorator(Image image) {
		super(image);
	}
	
	ImageGrayScaleDecorator(Image image, Rectangle rect) {
		super(image, rect);
	}	
	
	@Override
	public BufferedImage getImage() {
		return ImageUtilities.convertToGrayScale(super.getImage(), rect);
	}	
	
}

// Concrete Decorator 2
class ImageMosaicDecorator extends ImageDecorator {
	final int gridSize;
	
	ImageMosaicDecorator(Image image, int gridSize) {
		super(image);
		this.gridSize = gridSize;
	}
	
	ImageMosaicDecorator(Image image, Rectangle rect, int gridSize) {
		super(image, rect);
		this.gridSize = gridSize;
	}
	
	@Override
	public BufferedImage getImage() {
		return ImageUtilities.addMosaic(super.getImage(), rect, gridSize);
	}	
	
}

// Image utilities
class ImageUtilities {
	
	static BufferedImage convertToGrayScale(BufferedImage image) {
		return convertToGrayScale(image, new Rectangle(image));
	}
	
	static BufferedImage convertToGrayScale(BufferedImage image, Rectangle rect) {

		int width = image.getWidth();
		int height = image.getHeight();
		
		BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for(int y = 0; y < height; y++) {
	        for(int x = 0; x < width; x++) {
	        	int color = image.getRGB(x, y);
	        	
	        	if (x >= rect.x1 && x <= rect.x2 && y >= rect.y1 && y <= rect.y2) {
		        	RGBA rgba = new RGBA(color);
		        	int gray = (rgba.red + rgba.green + rgba.blue) / 3;
		        	color = RGBA.getColor(gray, gray, gray, 255);	        		        		
	        	}

	        	grayscaleImage.setRGB(x, y, color);
	        }
		}
		
		return grayscaleImage;
	}
	
	static BufferedImage addMosaic(BufferedImage image, int gridSize) {		
		return addMosaic(image, new Rectangle(image), gridSize);
	}

	static BufferedImage addMosaic(BufferedImage image, Rectangle rect, int gridSize) {

		int width = image.getWidth();
		int height = image.getHeight();
		
		BufferedImage mosaicedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for(int y = 0; y < height; y++) {
	        for(int x = 0; x < width; x++) {
	        	int color = image.getRGB(x, y);
	        	
	        	if (x >= rect.x1 && x <= rect.x2 && y >= rect.y1 && y <= rect.y2) {
	        		if (x % gridSize == 0 || y % gridSize == 0) {
	        			color = 0;
	        		}
	        	}

	        	mosaicedImage.setRGB(x, y, color);
	        }
		}
		
		return mosaicedImage;
	}	
	
}

class RGBA {
	int red;
	int green;
	int blue;
	int alpha;
	
	RGBA(int color) {
    	blue = color & 0xff;
    	green = (color & 0xff00) >> 8;
    	red = (color & 0xff0000) >> 16;
    	alpha = (color & 0xff000000) >>> 24;		
	}
	
	int getColor() {
		return (alpha << 24 | red << 16 | green << 8 | blue);
	}
	
	static int getColor(int red, int green, int blue) {
		return (red << 16 | green << 8 | blue);
	}

	static int getColor(int red, int green, int blue, int alpha) {
		return (alpha << 24 | red << 16 | green << 8 | blue);
	}
	
}

class Rectangle {
	final int x1;
	final int y1;
	final int x2;
	final int y2;
	
	Rectangle(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	Rectangle(BufferedImage image) {
		x1 = 0;
		y1 = 0;
		x2 = image.getWidth() - 1;
		y2 = image.getHeight() - 1;
	}
	
}

