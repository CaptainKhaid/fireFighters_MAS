package fireFighters_MAS;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Forester
{
	// Local variables definition
	private Grid<Object> grid;
	private int id;
	private int lifePoints;
	private int strength;
	private Context<Object> context;
	private int extinguishingDistance;
	// Local variables initialization
	private int windDirection = -1;
	private int genDirection = -1;
	private boolean newInfo = false;
	private Knowledge knowledge = new Knowledge();
	private ArrayList<Forester> contacts = new ArrayList<Forester>();	
	/**
	 * Custom constructor
	 * @param context - context to which the forester is added
	 * @param grid - grid to which the forester is added
	 * @param id - an ID
	 */
	public Forester(Context<Object> context, Grid<Object> grid, int id)
	{
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		this.context = context;
		this.grid = grid;
		this.id = id;
		this.lifePoints = params.getInteger("forester_life");
		this.strength = params.getInteger("forester_strength");
		this.extinguishingDistance = params.getInteger("forester_extinguishingDistance");
	}
	/**
	 * A step method of the forester
	 */
	@ScheduledMethod(start = 1, interval = 10)
	public void step()
	{
		if (contacts.isEmpty())	{ addAllContacts(); } // Assumes that all of the foresters know each other		
		
		int x = grid.getLocation(this).getX();
		int y = grid.getLocation(this).getY();
		// Action part
		checkWindDirection(); // Check wind direction
		
		if (checkCell(x, y)) // If there is fire in the cell in which the forester is located
		{
			standingInTheFire();
		}
		else
		{
			checkAreaForFire(x, y);
			moveOrExtinguish();
		}
		// Communication part
		if (newInfo) { sendRadioMessage(); } // If some new info of interest was obtained, send it around
	}
	@ScheduledMethod(start = 1, interval = 1)
	public void checkup()
	{
		if (lifePoints <= 0) { context.remove(this); }
	}
	/**
	 * Movement routine of a forester
	 */
	private void moveOrExtinguish()
	{
		int result[] = findDirection2NearestFire();
		int direction = result[0];
		int oppositeDirection = (direction + 4) % 8;
		int distance = result[1];
				
		if (direction >= 0) 
		{
			if (distance == 0) // If fire is adjacent to or on the cell of the forester
			{
				tryToMove(oppositeDirection);
				return;		
			}
			else if (distance == extinguishingDistance) // If fire is exactly at the extinguishingDistance
			{
				extinguishFire(direction,extinguishingDistance);
				return;
			}
			else if (distance > extinguishingDistance) // If fire is more than extinguishingDistance away
			{
				tryToMove(direction);
				return;
			}			
		}		
		// Otherwise explore randomly
		genDirection = (genDirection + RandomHelper.nextIntFromTo(-1, 1) + 8) % 8;
		move(genDirection);
	}
	/**
	 * Given a possible movement direction, generate a set of others, and try to move in one of them
	 * @param pDir - direction to try to move to
	 * @return 0 - movement failed, 1 - movement succeeded
	 */
	private boolean tryToMove(int pDir)
	{
		int[] directions = {pDir,(pDir + 1)%8,(pDir - 1)%8,(pDir + 2)%8,(pDir - 2)%8,(pDir + 3)%8,(pDir - 3)%8,(pDir + 4)%8};		
		
		for (int direction : directions)
		{
			if (move(direction) == 0) {	return true; }
		}
		
		return false;
	}
	/** Forester's reaction on being in the fire */
	private void standingInTheFire()
	{
		lifePoints--;
		int direction = getOppositeWindDirection(); // Check there the wind blows from
		
		if (direction > -1) { tryToMove(direction); } // Try to move in a direction off-the-wind to escape from fire
	}
	/** Search the whole grid for foresters, and all of them as contacts */
	private void addAllContacts()
	{
		Iterable<Object> objects = this.grid.getObjects();
		
		for (Object object : objects)
		{
			if (object.getClass() == Forester.class) { contacts.add((Forester) object); }
		}
	}
	/**
	 * Check a 3x3 area around a given coordinate for fires
	 * @param x - X coordinate
	 * @param y - Y coordinate
	 */
	private void checkAreaForFire(int x, int y)
	{
		int[] areaXs = { 3, 2, 1, 0, -1, -2, -3 };
		int[] areaYs = { 3, 2, 1, 0, -1, -2, -3 };
		
		for (int areaX : areaXs)
		{
			for (int areaY : areaYs) { checkCell(x + areaX, y + areaY); }
		}
	}
	/**
	 * Extinguish fire at a given distance in a given direction
	 * @param dir - direction to extinguish fire in
	 * @param dist - distance at which to extinguish
	 */
	private void extinguishFire(int dir, int dist)
	{
		int x = grid.getLocation(this).getX();
		int y = grid.getLocation(this).getY();		
		int[] result;
		int otherX = -1, otherY = -1;
		
		for (int i = 0; i < dist; i++)
		{
			if (i == 0) { result = Tools.dirToCoord(dir,x,y); }
			else { result = Tools.dirToCoord(dir,otherX,otherY); }
			
			otherX = result[0];
			otherY = result[1];
		}
		
		if (Tools.isWithinBorders(otherX, otherY, grid))
		{		
			Fire fire = returnFire(otherX, otherY);
			
			if (fire != null) { fire.decreaseLifetime(strength); }
		}
	}	
	/**
	 * Return fire object located at a grid cell with given coordinates
	 * @param x - X coordinate of the fire
	 * @param y - Y coordinate of the fire
	 * @return null - if no fire is at the given cell, obj - if this Fire obj was found
	 */
	private Fire returnFire(int x, int y)
	{
		Iterable<Object> objects = grid.getObjectsAt(x, y);
		
		for (Object obj : objects)
		{
			if (obj.getClass() == Fire.class) { return (Fire) obj; }
		}
		
		return null;
	}
	/**
	 * Move in a given direction 
	 * @param dir - a direction to move to
	 * @return -1 - if move was unsuccessful, 0 - if move was successful, 1 - if couldn't move because another forester already took this place
	 */
	private int move(int dir)
	{
		int x = this.grid.getLocation(this).getX();
		int y = this.grid.getLocation(this).getY();
		int[] result = Tools.dirToCoord(dir,x,y);
		int otherX = result[0], otherY = result[1];

		if (!checkCell(otherX, otherY) && !checkForester(otherX, otherY) && !fireAdjacent(otherX,otherY))
		{
			if (Tools.isWithinBorders(otherX, otherY, grid))
			{
				grid.moveTo(this, otherX, otherY);
				return 0;
			}
		}
		else if (checkForester(otherX, otherY)) { return 1; }
		
		return -1;
	}
	/**
	 * Check if fire is immediately next to the cell with the given coordinates 
	 * @param x - X coordinate
	 * @param y - Y coordinate
	 * @return 0 - not too close, 1 - too close 
	 */
	private boolean fireAdjacent(int x, int y)
	{
		boolean fire = false;
		int[] areaXs = {1, 0, -1};
		int[] areaYs = {1, 0, -1};
		
		for (int areaX : areaXs)
		{
			for (int areaY : areaYs)
			{
				if(checkCell(x + areaX, y + areaY))
				{
					fire = true;
					break;
				}
			}
		}
		
		return fire;
	}
	/**
	 * Method used to find the direction and distance to the nearest fire
	 * @return a tuple (direction, distance) to the nearest fire
	 */
	private int[] findDirection2NearestFire()
	{
		int x = grid.getLocation(this).getX(), y = grid.getLocation(this).getY();
		int distance = Integer.MAX_VALUE;
		int direction = -1;
		
		for (Position p : knowledge.getAllFire()) // For all the fires in the forester's knowledge
		{
			// Fire position
			int xF = p.getX(), yF = p.getY();
			// Absolute linear distances to the fire
			int fXDistance = Math.abs(xF - x), fYDistance = Math.abs(yF - y);
			//
			int chebyshevDist = Math.max(fXDistance,fYDistance);
			// Determine if the fire is closest. If so, update distance and direction accordingly
			if (chebyshevDist < distance)
			{
				distance = chebyshevDist;
				
				if (x - xF < 0) // It's somewhere to the East
				{
					if (y - yF == 0) { direction = 2; } // East
					else if (y - yF < 0) { direction = 1; } // North-East
					else { direction = 3; } // South-East
				}
				else if (x - xF == 0)  // It's either to the North or to the South
				{
					if (y - yF < 0)	{ direction = 0; } // North
					else if (y - yF == 0) { direction = -1; } // Fire is here!!!
					else { direction = 4; } // South
				}
				else // It's somewhere to the West
				{
					if (y - yF == 0) { direction = 6; } // West
					else if (y - yF < 0) { direction = 7; } // North-West
					else { direction = 5; } // South-West
				}
			}
		}
		
		int[] result = { direction, distance };
		return result;
	}
	/**
	 * Check if a given location contains a fire object, update own knowledge
	 * @param x - X coordinate
	 * @param y - Y coordinate
	 * @return 0 - there is fire, 1 - no fire
	 */
	public boolean checkCell(int x, int y)
	{
		boolean fire = false;
		
		if (Tools.isWithinBorders(x, y, grid))
		{
			Iterable<Object> objects = grid.getObjectsAt(x, y);
			
			for (Object obj : objects)
			{
				if (obj.getClass() == Fire.class)
				{
					fire = true;
					break;
				}
			}			
		}

		if (fire) { addFire(x,y,true); }
		else { removeFire(x,y); }
		
		return fire;
	}		
	/**
	 * Check if there is a forester at a given location on the grid
	 * @param x - X coordinate
	 * @param y - Y coordinate
	 * @return 0 - no forester is there, 1 - there is a forester
	 */
	public boolean checkForester(int x, int y)
	{
		if (Tools.isWithinBorders(x, y, grid))
		{
			Iterable<Object> objects = grid.getObjectsAt(x, y);
			
			for (Object obj : objects)
			{
				if (obj.getClass() == Forester.class) {	return true; }
			}
		}
		
		return false;
	}
	/**
	 * Send message using radio transmitter
	 */
	public void sendRadioMessage()
	{
		Message message = new Message();
		message.setKnowledge(knowledge);
		
		for (Forester contact : contacts) { contact.recieveMessage(message); }
		
		newInfo = false; // All the new information was sent, over now
	}
	/**
	 * Receive message
	 * @param message - a message
	 */
	public void recieveMessage(Message message) { compareKnowledge(message.getKnowledge()); }
	/**
	 * Update the knowledge of the forester by taking info from a given knowledge
	 * @param k - knowledge to compare to
	 */
	public void compareKnowledge(Knowledge k)
	{
		for (Position f : k.getAllFire()) {	knowledge.addFire(f); }

		//Add foresters into the comparison
		for(Forester f : k.getAllForester())
		{
			Position p = k.getForesterPosition(f);
			knowledge.addForester(f, p);
		}
		//Add forests into the comparison
		for(Forest f : k.getAllForest())
		{
			GridPoint grid = f.getGrid().getLocation(f);
			Position p = new Position(grid.getX(), grid.getY());
			knowledge.addForest(f, p);
		}
	}	
	/**
	 * Add a new fire to the forester's knowledge
	 * Additional specification is done for the fire discovered himself
	 * @param x - X coordinate
	 * @param y - Y coordinate
	 * @param foundHimself - flag if the fire was found by the forester himself
	 */
	public void addFire(int x, int y, boolean foundHimself)
	{
		newInfo = true;
		knowledge.addFire(new Position(x, y));
	}
	/**
	 * Remove fire at a given location from the forester's knowledge
	 * @param x - X coordinate
	 * @param y - Y coordinate
	 */
	private void removeFire(int x, int y)
	{
		knowledge.removeFire(new Position(x, y));
		newInfo = true;
	}
	/** Get a direction of wind within the current context */
	private void checkWindDirection()
	{
		if (context != null) {
			int windDirection = Tools.translateDegreeToNumberOfField(((Wind) context.getObjects(Wind.class).get(0)).getWindDirection2());
			//int windDirection = ((Wind) context.getObjects(Wind.class).get(0)).getWindDirection();
		}
	}	
	/**
	 * Get a direction opposite to the known direction of the wind
	 * @return
	 */
	private int getOppositeWindDirection() { return (windDirection + 4) % 8; }
}
