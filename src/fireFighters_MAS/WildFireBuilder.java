package fireFighters_MAS;

import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;

public class WildFireBuilder implements ContextBuilder<Object>
{
	@Override
	public Context<Object> build(Context<Object> context) // Build a context of the simulation
	{
		context.setId("fireFighters_MAS");
		context = initRandom(context);
		return context;
	}
	/**
	 * Initialize simulation with random positioning of fires and foresters
	 * @param context - a context to build
	 * @return - a built context
	 */
	public Context<Object> initRandom(Context<Object> context)
	{		
		// Get access to the user accessible parameters
		Parameters params = RunEnvironment.getInstance().getParameters();
		// Create a grid for the simulation
		int gridX = params.getInteger("gridWidth");
		int gridY = params.getInteger("gridHeight");
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new repast.simphony.space.grid.BouncyBorders(), new SimpleGridAdder<Object>(), true, gridX, gridY));
		// Create fire instances, and add them to the context and to the grid
		int fireCount = params.getInteger("fire_count");
		
		for(int i = 0; i < fireCount; i++) { context.add(new Fire(context,grid)); }
		// Create rain instances, and add them to the context and to the grid
		int rainCount = params.getInteger("rain_count");
		
		for (int i = 0; i < rainCount; i++) { context.add(new Rain(context,grid)); }
		// Create forester instances, and add them to the context and to the grid
		int foresterCount = params.getInteger("forester_amount");
		
		for (int i = 0; i < foresterCount; i++) { context.add(new Forester(context, grid, i)); }		
		// Put the objects on the grid in random locations
		for (Object obj : context)
		{
			int x = RandomHelper.nextIntFromTo(0, (gridX-1));
			int y = RandomHelper.nextIntFromTo(0, (gridY-1));
			grid.moveTo(obj, x, y);
		}
		// Create forest instances, and add them to the context and to the grid		
		for(int i = 0; i < gridX; i++)
		{
			for(int j = 0; j < gridY; j++)
			{
				Forest f = new Forest(context,grid);
				context.add(f);
				grid.moveTo(f, i, j);
			}
		}
		//
		context.add(new Wind());
		//
		return context;
	}
}
