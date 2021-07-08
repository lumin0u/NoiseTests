import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main_fire extends JFrame
{
	public final int SCREEN_SIZE = 500;
	
	public static void main(String[] args) throws Exception
	{
		int l = new Random().nextInt();
		Main_fire f = new Main_fire();
		f.setTitle(Integer.toHexString(l));
		
		Thread.sleep(500);
		
		for(int i = 0; i < 10000000; i++)
		{
			f.draw((double) i * 0.8);
		}
	}
	
	private BufferedImage img;
	private Settings settings;
	
	public Main_fire()
	{
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLocation(0, 0);
		setSize(SCREEN_SIZE, SCREEN_SIZE);
		setVisible(true);
		
		img = getGraphicsConfiguration().createCompatibleImage(SCREEN_SIZE, SCREEN_SIZE);
		img.createGraphics();
		
//		settings = new Settings();
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
	
	public static int a(double a, double b)
	{
		a = a % (b * 2);
		return (int) (a < b ? a : b * 2 - a - 1);
	}
	
	public static double distance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	public BufferedImage draw(double z)
	{
		Graphics g = img.getGraphics();
		
		int res = 2;
		
		for(double x = 0; x < (double) SCREEN_SIZE / res; x++)
		{
			for(double y = 0; y < (double) SCREEN_SIZE / res; y++)
			{
				double freq = 18;
				
				double distance = distance(x, y, (double) SCREEN_SIZE / res / 2, (double) SCREEN_SIZE / res / 2);
				double lx = Math.acos((x - (double) SCREEN_SIZE / res / 2) / distance);
				double ly = distance - z * 5 + lx * Math.min(distance, ImprovedNoise.noise(lx, distance / 100, z / 10) * 20);
				double n10 = noise(x / freq + 10000 + z / 40, y / freq + z / 40, 0, 2, 0.4);
				double n11 = noise(x / freq + 30000 + z / 40, y / freq + z / 40, 0, 2, 0.4);
				double n2 = noise(x / freq + 20000 + n10*1.5 + z / 5, y / freq + n11*1.5 + z / 3, z / 5, 4, 0.4);
				
				float gr = (float) Math.max(0, Math.min(1, (y / (double) SCREEN_SIZE + 0.) / 1.1));
//				gr = (float) ImprovedNoise.fade(gr);
//				float gr = (float) Math.max(0,
//						Math.min(1,
//								1 - (distance + 50) / 200
//						)
//				);
				
				float r = 0;
				float ge = 0;
				float c = (float) (n2 * gr * 2);
				if(c > 0.25f)
				{
					g.setColor(new Color(Math.min(1, c*3f), Math.max(0, Math.min(1, c * 2 - 0.5f)), c * 0.2f));
				}
				else if(c > 0.21f)
				{
					c = (c - 0.2f) * 4;
					r = Math.min(1, c*3f);
					ge = Math.max(0, Math.min(1, c * 2 - 0.5f));
				}
				if(c <= 0.25f)
				{
//					n2 = noise(x / freq + 20000 + n10*0.8 + z / 8, y / freq + n11*0.8 + z / 5, z / 10, 2, 0.4);
					//					g.setColor(new Color((float) Math.min(1, gr + n2 / 2) / 4f + r, (float) (gr + n2 / 2) / 8f + ge, 0));
					g.setColor(new Color(r, ge, 0));
				}
//				c = c > 0.24f ? c : c > 0.815f ? Math.max(0, c / 40 + 0.18f) : 0;
				
				g.fillRect((int) x * res, (int) y * res, res, res);
			}
		}
		
		getGraphics().drawImage(img, 0, 0, this);
		
		return img;
	}
	
	public class Settings extends JFrame
	{
		public List<Param> params;
		
		public Settings()
		{
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setLocationRelativeTo(null);
			setLocation(1000, 300);
			setSize(400, 270);
			setVisible(true);
			JPanel panel = new JPanel();
			setContentPane(panel);
			
			params = Arrays.asList(new Param("n1 - negativity", 0.5, 2, 0.7), new Param("n2 - negativity", 0.5, 2, 0.7), new Param("n - factor", 0, 10, 2), new Param("n2 - factor", 0, 10, 3), new Param("zoom", 0, 10, 1));
			
			int y = 0;
			for(Param param : params)
			{
				JSlider jSlider = new JSlider((int) (param.min * 100), (int) (param.max * 100), (int) (param.value * 100));
				panel.add(jSlider);
				jSlider.setLocation(0, y);
				y += 100;
				
				jSlider.addChangeListener(new ChangeListener()
				{
					@Override
					public void stateChanged(ChangeEvent e)
					{
						param.value = (double) jSlider.getValue() / 100;
					}
				});
			}
		}
		
		public double getParam(String param)
		{
			return params.stream().filter(p -> p.name.equals(param)).findAny().get().value;
		}
	}
	
	public class Param
	{
		public final String name;
		public final double min;
		public final double max;
		public double value;
		
		public Param(String name, double min, double max, double defaultValue)
		{
			this.name = name;
			this.min = min;
			this.max = max;
			this.value = defaultValue;
		}
	}
}
