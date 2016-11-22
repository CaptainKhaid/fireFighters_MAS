package fireFighters_MAS;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import java.util.*;

public class Fire
{
	// Local variables definition
	private Grid<Object> grid;
	private int lifetime;	
	private int category;
	private double randomFireProb;
	private Context<Object> context;
	/**
	 * Custom constructor
	 * @param context - context to which the fire is added
	 * @param grid - a grid to add the fire to
	 */
	public Fire(Context<Object> context, Grid<Object> grid)
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		this.context = context;
		this.grid = grid;
		this.lifetime = params.getInteger("fire_lifetime");
		this.category = params.getInteger("fire_strength");
		this.randomFireProb = params.getDouble("fire_randomFireProb");
	}
	@ScheduledMethod(start = 1, interval = 20)
	public void step() 
	{		
		GridPoint myLocation = grid.getLocation(this);
		int x = (int) myLocation.getX(), y = (int) myLocation.getY();
		
		Forest f = getForest(x, y);
		
		if (f != null) { f.decreaseHealth(category); }
		int windDirection = Tools.translateDegreeToNumberOfField(((Wind) context.getObjects(Wind.class).get(0)).getWindDirection2());
		//int windDirection = ((Wind) context.getObjects(Wind.class).get(0)).getWindDirection();
		boolean hasRain = false;
		
		Iterable<Object> objects = grid.getObjectsAt(x, y);
		
		for (Object obj : objects)
		{
			if (obj.getClass() == Rain.class)
			{
				hasRain = true;
				break;
			}
		}
		
		moveFire(windDirection, hasRain);
		//		
		decreaseLifetime();
		catchRandomFire();
		
	}	
	@ScheduledMethod(start = 1, interval = 20)
	public void checkup() 
	{		
		if (lifetime <= 0) { context.remove(this); }
	}
	
	private void catchRandomFire()
	{
		if (RandomHelper.nextDoubleFromTo(0, 1) < randomFireProb)
		{
			int gridWidth = grid.getDimensions().getWidth();
			int gridHeight = grid.getDimensions().getHeight();
			int targetX = RandomHelper.nextIntFromTo(0, gridWidth - 1), targetY = RandomHelper.nextIntFromTo(0, gridHeight - 1);
			Iterable<Object> objects = grid.getObjectsAt(targetX, targetY);
			for (Object obj : objects)
			{
				if (obj.getClass() == Rain.class)
				{
					return;
				}
			}
			Fire fire = new Fire(context,grid);
			context.add(fire);
			grid.moveTo(fire, targetX, targetY);
		}

	}
	
	/**
	 *  Move the fire
	 * @param direction - direction to move to
	 * @param rain - flag if there is rain
	 */
	private void moveFire(int direction, boolean rain)
	{
		List<Double> probs = new ArrayList<Double>(); // Directional probabilities
		
		for (int i = 0; i < 8; i++) { probs.add(1.0/8); }
		
		for (int i = 0; i < probs.size(); i++)
		{
			probs.set(i, probs.get(i) * ((rain ? 0.5 : 1) - (double)Math.abs(i - direction)/8));
			
			if (RandomHelper.nextDoubleFromTo(0, 1) < probs.get(i))
			{
				int[] result = Tools.dirToCoord(i, 0, 0);
				
				igniteTowards(result[0], result[1]);
				break;
			}
		}
	}
	/**
	 * Spawn the fire given linear translations
	 * @param xDif - translation on X
	 * @param yDif - translation on Y
	 */
	public void igniteTowards(int xDif, int yDif) 
	{
		GridPoint myLocation = grid.getLocation(this);
		int nextX = myLocation.getX() + xDif, nextY = myLocation.getY() + yDif;

		if (Tools.isWithinBorders(nextX, nextY, grid) && grid.getObjectAt(nextX, nextY) != null)
		{
			Iterable<Object> objects = grid.getObjectsAt(nextX, nextY);
			boolean isFire = false, isForest = false;
			
			for (Object obj : objects)
			{
				if (obj.getClass() == Fire.class) {	isFire = true; }
				else if (obj.getClass() == Forest.class) { isForest = true; }
			}
			
			if (isForest & !isFire)
			{
				Fire fNew = new Fire(context,grid);
				context.add(fNew);
				grid.moveTo(fNew, nextX, nextY);
			}
		}
	}
	/**
	 * Get forest object at a given location
	 * @param x - X coordinate
	 * @param y - Y coordinate
	 * @return null - if none found at that location, forest object - if found
	 */
	public Forest getForest(int x, int y)
	{
		Iterable<Object> objects = grid.getObjectsAt(x, y);
		
		for (Object obj : objects)
		{
			if (obj.getClass() == Forest.class) { return (Forest) obj; }
		}
		
		return null;
	}
	/**
	 * Decrease the lifetime of the fire
	 */
	public void decreaseLifetime() { lifetime--; }
	/**
	 * Decrease the lifetime of the fire by a given amount
	 * @param amount - an amount to decrease by
	 */
	public void decreaseLifetime(int amount) { lifetime -= amount; }
	// Local getters
	public int getLifetime() { return lifetime; }

}
