package fireFighters_MAS;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

public class Forest
{
	// Local variables definition
	private Grid<Object> grid;
	private int health;
	private Context<Object> context;
	private Double randomRainProb;
	private Double randomFireProb;
	/**
	 * Custom constructor
	 * @param context - context to which the forest is added
	 * @param grid - a grid object to add the forest object to
	 */
	public Forest(Context<Object> context, Grid<Object> grid)
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		this.context = context;
		this.grid = grid;
		this.health = params.getInteger("forest_life");
		this.randomRainProb = params.getDouble("rain_randomRainProb");
		this.randomFireProb = params.getDouble("fire_randomFireProb");
	}
	@ScheduledMethod(start = 1, interval = 10000)
	public void regrowth()
	{
//		grow();
		addRandomRain();
		catchRandomFire();
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
	 * Add some Fire to the grid to a random place
	 */
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
	
	@ScheduledMethod(start = 1, interval = 1)
	public void checkup()
	{
		if (health <= 0) { context.remove(this); }
	}
	
	/**
	 * Spawn new forest objects at random on the grid and around the forest on empty cells
	 */
	public void grow()
	{
		GridPoint location = this.grid.getLocation(this);
		
		for (int i = 0; i < 4; i++)
		{
			Forest f = new Forest(context,grid);
			int x = location.getX(), y = location.getY();
			
			switch (i)
			{
				case 0:
					x++;			
					break;
				case 1:
					x--;					
					break;
				case 2:
					y++;					
					break;
				case 3:
					y--;					
					break;
			}
			
			if (Tools.isWithinBorders(x, y, grid) && grid.getObjectAt(x, y) == null)
			{
				context.add(f);
				grid.moveTo(f, x, y);
			}
		}		

		int randX = RandomHelper.nextIntFromTo(0, grid.getDimensions().getWidth());
		int randY = RandomHelper.nextIntFromTo(0, grid.getDimensions().getHeight());
		
		if (Tools.isWithinBorders(randX, randY, grid) && grid.getObjectAt(randX, randY) == null)
		{
			Forest fNew = new Forest(context,grid);
			context.add(fNew);
			grid.moveTo(fNew, randX, randY);
		}
	}
	/**
	 * Decrease health of the forest object by a given amount
	 * @param amount - amount of health loss
	 */
	public void decreaseHealth(int amount){	this.health -= amount; }
	// Local getters
	public int getHealth() { return health; }
	
	/**
	 * get grid of the forest
	 * @return Grid<Object>
	 */
	public Grid<Object> getGrid()
	{
		return this.grid;
	}
}
