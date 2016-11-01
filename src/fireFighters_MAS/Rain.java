package fireFighters_MAS;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Rain
{
	// Local variables declaration
	private Grid<Object> grid;
	private int rainfall;
	private Context<Object> context;
	private double moveRandomness;
	private double spawnProb;
	private double vaporProb;
	private double randomRainProb;
	
	/**
	 * Custom constructor
	 * @param context - context to which the object is added
	 * @param grid - a grid to add the object to
	 */
	public Rain(Context<Object> context, Grid<Object> grid)
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		this.context = context;
		this.grid = grid;		
		this.rainfall = params.getInteger("rain_strength");
		this.moveRandomness = params.getDouble("rain_moveRandomness");
		this.spawnProb = params.getDouble("rain_spawnProb");
		this.vaporProb = params.getDouble("rain_vaporProb");
		this.randomRainProb = params.getDouble("rain_randomRainProb");
	}
	/**
	 * Rain step method
	 */
	@ScheduledMethod(start = 1, interval = 80)
	public void step()
	{		
		GridPoint myLocation = getLocation();
		
		for (Object obj : grid.getObjectsAt(myLocation.getX(),myLocation.getY()))
		{
			if (obj.getClass() == Fire.class)
			{
				Fire f = (Fire) obj;
				f.decreaseLifetime(rainfall);
				break;
			}
		}
		
		spawn();
		move();
		vapor();
		
		addRandomRain();
	}
	/**
	 * Rain movement function
	 */
	private void move()
	{
		int windDirection = -1;
		
		if (context != null) { windDirection = ((Wind) context.getObjects(Wind.class).get(0)).getWindDirection(); }
		
		double randomnessDegree = 0.2;
		int moveDirection = -1;
		
		if (windDirection == -1) { moveDirection = RandomHelper.nextIntFromTo(0, 7); }
		else { moveDirection = ((int)(windDirection + randomnessDegree * RandomHelper.nextIntFromTo(-4, 4)) + 8) % 8; }
		
		GridPoint myLocation = getLocation();
		int[] targetCoords = Tools.dirToCoord(moveDirection, myLocation.getX(), myLocation.getY());
		int targetX = targetCoords[0], targetY = targetCoords[1];
		
		if (Tools.isWithinBorders(targetX, targetY, grid)) { grid.moveTo(this, targetX, targetY); }
		else { context.remove(this); }
	}
	/**
	 * Rain spawn method
	 */
	private void spawn()
	{		
		if (RandomHelper.nextDoubleFromTo(0, 1) < spawnProb)
		{		
			int windDirection = -1;
			
			if (context != null) { windDirection = ((Wind) context.getObjects(Wind.class).get(0)).getWindDirection(); }
			
			int spawnDirection = -1;
			
			if (windDirection == -1) { spawnDirection = RandomHelper.nextIntFromTo(0, 7); }
			else { spawnDirection = ((int)(windDirection + moveRandomness * RandomHelper.nextIntFromTo(-4, 4)) + 8) % 8; }
			
			GridPoint myLocation = getLocation();
			int[] targetCoords = Tools.dirToCoord(spawnDirection, myLocation.getX(), myLocation.getY());
			int targetX = targetCoords[0], targetY = targetCoords[1];
			
			if (Tools.isWithinBorders(targetX, targetY, grid))
			{
				Rain rain = new Rain(context,grid);
				context.add(rain); 
				grid.moveTo(rain, targetX, targetY);
			}
		}
	}
	/**
	 * Rain evaporation method
	 */
	private void vapor()
	{		
		if (RandomHelper.nextDoubleFromTo(0, 1) < vaporProb) { context.remove(this); }
	}
	/**
	 * Add some rain to the grid to a random place
	 */
	private void addRandomRain()
	{		
		if (RandomHelper.nextDoubleFromTo(0, 1) < randomRainProb)
		{
			int gridWidth = grid.getDimensions().getWidth();
			int gridHeight = grid.getDimensions().getHeight();
			int targetX = RandomHelper.nextIntFromTo(0, gridWidth - 1), targetY = RandomHelper.nextIntFromTo(0, gridHeight - 1);			
			Rain rain = new Rain(context,grid);
			context.add(rain);
			grid.moveTo(rain, targetX, targetY);
		}
	}
	/**
	 * Get location of this rain object
	 * @return grid cell location of the object
	 */
	private GridPoint getLocation() { return grid.getLocation(this); };
}
