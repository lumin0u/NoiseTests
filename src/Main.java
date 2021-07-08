import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class Main extends JFrame
{
	public static void main(String[] args) throws Exception
	{
		int l = new Random().nextInt();
		Main f = new Main();
		f.setTitle(Integer.toHexString(l));
		
		Thread.sleep(500);
		
//		File outputfile = new File(Integer.toHexString(l) + ".png");
//		ImageIO.write(f.draw(l), "png", outputfile);
		f.draw(l);
	}
	
	public Main()
	{
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLocation(0, 0);
		setSize(1000, 1000);
		setVisible(true);
	}
	
	public static double noise(double x, double y, double z, int octaves, double persistence)
	{
		double total = 0;
		double frequency = 1;
		double amplitude = 1;
		double maxValue = 0;  // Used for normalizing result to 0.0 - 1.0
		for(int i = 0; i < octaves; i++)
		{
			total += ImprovedNoise.noise(x * frequency, y * frequency, z * frequency) * amplitude;
			
			maxValue += amplitude;
			
			amplitude *= persistence;
			frequency *= 2;
		}
		
		return total / maxValue;
	}
	
	public BufferedImage draw(double z)
	{
		BufferedImage img = getGraphicsConfiguration().createCompatibleImage(1000, 1000);
		Graphics g = img.createGraphics();
		
		int res = 30;
		
		for(int x = 0; x < 1000 / res; x++)
		{
			for(int y = 0; y < 1000 / res; y++)
			{
				g.setColor(new Color(255, 255, 255));
				g.fillRect(x * res, y * res, res, res);
				
				if(noise((double) x / 10, (double) y / 10, z / 5, 4, 2) > 0)
				{
					int r = (int) ((noise((double) (x+1000) / 20, (double) (y+1000) / 20, z / 5, 3, 0.5) + 1) * 128);
					int b = (int) ((noise((double) (x+5000) / 20, (double) (y+5000) / 20, z / 5, 3, 0.5) + 1) * 128);
					g.setColor(new Color(r, 0, b));
					
					boolean reversed = noise((double) (x+10000), (double) (y+10000), z / 5, 1, 0.2) > 0;
					double size = noise((double) (x+20000) / 2, (double) (y+20000) / 2, z / 5, 1, 0.2) / 2 + 0.5;
					Polygon polygon = new Polygon();
					
					int yPoint1 = (int) ((1 - size) * res / 2);
					polygon.addPoint(x * res + res / 2,
							(int) (y * res + (reversed ? res - yPoint1 : yPoint1)));
					
					polygon.addPoint(x * res + (int) ((1 - size) * res / 2),
							(int) (y * res + (!reversed ? res - yPoint1 : yPoint1)));
					
					polygon.addPoint(x * res + res - (int) ((1 - size) * res / 2),
							(int) (y * res + (!reversed ? res - yPoint1 : yPoint1)));
					g.fillPolygon(polygon);
				}
			}
		}
		
		getGraphics().drawImage(img, 0, 0, this);
		
		return img;
	}
}
