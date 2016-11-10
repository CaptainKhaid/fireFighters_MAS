package fireFighters_MAS;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;

public class Tools
{
	/**
	 * Get coordinates of a grid cell located in a given direction from a given grid cell coordinates
	 * @param direction - direction to the other cell
	 * @param thisX - X coordinate of this cell
	 * @param thisY - Y coordinate of this cell
	 * @return set of coordinates of the other cell (X,Y)
	 */
	public final static int[] dirToCoord(int direction, int thisX, int thisY)
	{
		int x = thisX, y = thisY;
		
		switch (direction)
		{
	        case 0:  y++;
	                 break;
	        case 1:  x++; y++;
	                 break;
	        case 2:  x++;
	                 break;
	        case 3:  x++; y--;
	                 break;
	        case 4:  y--;
	                 break;
	        case 5:  x--; y--;
	                 break;
	        case 6:  x--;
	                 break;
	        case 7:  x--; y++;
		}
		
		return new int[] {x,y};
	}
	/**
	 * Check that given grid cell coordinates lay inside a given grid
	 * @param x - X coordinate
	 * @param y - Y coordinate
	 * @param grid - a grid to check against
	 * @return 0 - if out of borders, 1 - if within borders
	 */
	public static boolean isWithinBorders(int x, int y, Grid<Object> grid)
	{
		return (x >= 0 && y >= 0 && x < grid.getDimensions().getWidth() && y < grid.getDimensions().getHeight());
	}
	/**
	 * method to translate the degree to the field it moves to
	 * @param degree
	 * @return 0 to 7
	 */
	public static int translateDegreeToNumberOfField(Vector v)
	{
		double degree = v.getDegree();
		if(isBetween(degree,0,44)||isBetween(degree,316,360))
		{
			return 1;
		}else if(degree==45)
		{
			return 2;
		}else if(isBetween(degree,46,134))
		{
			return 3;
		}else if(degree==135)
		{
			return 4;
		}else if(isBetween(degree,136,224))
		{
			return 5;
		}else if(degree==225)
		{
			return 6;
		}else if(isBetween(degree,226,274))
		{
			return 7;
		}else if(degree==315)
		{
			return 0;
		}else{
			return -1;
		}
	}
	
	private static boolean isBetween(double x, double lower, double upper) {
		  return lower <= x && x <= upper;
	}
}
