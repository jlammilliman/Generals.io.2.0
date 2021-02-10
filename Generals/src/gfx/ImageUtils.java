package gfx;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {

	
	public static Image loadImage(String filePath) {
		try {
			
			return ImageIO.read(new File(filePath));
			
		} catch (IOException e){
			System.out.println("Could not load Image from path" + filePath);
		}
		return null;
	}
}
