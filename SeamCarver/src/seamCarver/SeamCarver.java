import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
	private Picture pic; 
	
	public SeamCarver(Picture picture)
	{
		if (picture == null)
		{
			throw new IllegalArgumentException(); 
		}
		pic = new Picture(picture); 
	}
	public Picture picture()
	{
		return new Picture(pic); 
	}
	public int width()
	{
		return pic.width(); 
	}
	public int height()
	{
		return pic.height(); 
	}
	
	private int energy(int x, int y, int i)
	{
		Color a = pic.get(x - i, y - (1 - i)); 
		Color b = pic.get(x + i, y + (1 - i)); 
		
		int red = a.getRed() - b.getRed(); 
		int blue = a.getBlue() - b.getBlue(); 
		int green = a.getGreen() - b.getGreen(); 
		
		return (red * red) + (blue * blue) + (green * green); 
	}
	public double energy(int x, int y)
	{
		if (x < 0 || x >= width() || y < 0 || y >= height())
		{
			throw new IllegalArgumentException(); 
		}
		if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1)
		{
			return 1000; 
		}
		
		int xE = energy(x, y, 1); 
		int yE = energy(x, y, 0); 
		
		return Math.sqrt((xE) + (yE)); 
	}
	public int[] findHorizontalSeam()
	{
		return findSeam(new Horizontal()); 
	}
	public int[] findVerticalSeam()
	{
		return findSeam(new Vertical()); 
	}
	
	private void checkSeam(int[] seam)
	{
		for (int i = 1; i < seam.length; i++)
		{
			int dx = Math.abs(seam[i] - seam[i - 1]); 
			if (dx != 1 && dx != 0)
			{
				throw new IllegalArgumentException(); 
			}
		}
	}
	
	public void removeHorizontalSeam(int[] seam)
	{
		if (seam == null || height() <= 1 || seam.length != width())
		{
			throw new IllegalArgumentException(); 
		}
		checkSeam(seam); 
		
		Picture newPic = new Picture(width(), height() - 1); 
		for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (y < seam[x])
                    newPic.setRGB(x, y, pic.getRGB(x, y));
                else if (y > seam[x])
                    newPic.setRGB(x, y - 1, pic.getRGB(x, y));
            }
        }

        pic = newPic;
		
	}
	public void removeVerticalSeam(int[] seam)
	{
		if (seam == null || width() <= 1 || seam.length != height())
		{
			throw new IllegalArgumentException(); 
		}
		checkSeam(seam); 
		Picture newPic = new Picture(width() - 1, height()); 
		
		for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (x < seam[y])
                    newPic.setRGB(x, y, pic.getRGB(x, y));
                else if (x > seam[y])
                    newPic.setRGB(x-1, y, pic.getRGB(x, y));
            }
        }

        pic = newPic;
		
		
	}
	
	private int[] findSeam(View view)
	{
		int[][] previous = new int[view.height()][view.width()]; 
		
		double[] topEnergy = new double[view.height()]; 
		for (int i = 0; i < view.height(); i++)
		{
			topEnergy[i] = view.energy(0,  i); 
		}
		double[] next = new double[view.height()]; 
		
		// dijkstra
		for (int a = 0; a + 1 < view.width(); a++)
		{
			for (int i = 0; i < view.height(); i++)
			{
				next[i] = Double.POSITIVE_INFINITY; 
			}
			
			for (int i = 0; i < view.height(); i++)
			{
				if (i > 0)
				{
					relax(view, i, topEnergy, a + 1, i - 1, next, previous); 
				}
				relax(view, i, topEnergy, a + 1, i, next, previous); 
				if (i + 1 < view.height())
				{
					relax(view, i, topEnergy, a + 1, i + 1, next, previous); 
				}
			}
			
			double[] temp = topEnergy; 
			topEnergy = next; 
			next = temp; 
			
		}
		
		double energy = topEnergy[0]; 
		int counter = 0; 
		for (int i = 1; i < view.height(); i++)
		{
			if (topEnergy[i] < energy)
			{
				energy = topEnergy[i]; 
				counter = i; 
			}
		} 
		
		int[] seam = new int[view.width()]; 
		seam[view.width() - 1] = counter; 
		
		for (int x = view.width() - 1; x > 0; x--)
            seam[x - 1] = previous[seam[x]][x];
		
		/* if(seam.length > 1)
			seam[view.width() - 1] = seam[view.width() - 2]; */
		
        return seam;
		
	}
	private void relax(View view, int i, double[] topEnergy, int dx, int dy, double[] nextEnergy, int[][] previous)
	{
		double newE = topEnergy[i] + view.energy(dx, dy); 
		if (newE < nextEnergy[dy])
		{
			nextEnergy[dy] = newE; 
			previous[dy][dx] = i; 
		}
	}
	
	// Helper class 
	private interface View	
	{
		int height(); 
		int width(); 
		double energy(int x, int y); 
	}
	
	private class Horizontal implements View // normal view
	// Treat findSeam() method as a findHorizontalSeam problem 
	{
		public int height()
		{
			return pic.height(); 
		}
		public int width()
		{
			return pic.width(); 
		}
		public double energy(int x, int y)
		{
			return SeamCarver.this.energy(x, y); 
		}
	}
	private class Vertical implements View // a rotated view 
	{
		public int height()
		{
			return pic.width(); 
		}
		public int width()
		{
			return pic.height(); 
		}
		public double energy(int x, int y)
		{
			return SeamCarver.this.energy(y, x); 
		}
	}
	public static void main(String[] args)
	{
		// for testing purposes only
	}
}
