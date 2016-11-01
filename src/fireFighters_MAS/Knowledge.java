package fireFighters_MAS;

import java.util.ArrayList;
import java.util.HashMap;

public class Knowledge
{
	// Local variables declaration
	private HashMap<Position, Boolean> fireKnowledge;
	private HashMap<Position, Forest> forestKnowledge;
	private HashMap<Position, Forester> foresterKnowledge;
	/**
	 * Custom constructor
	 */
	public Knowledge()
	{
		this.fireKnowledge = new HashMap<Position, Boolean>();
		this.foresterKnowledge = new HashMap<Position, Forester>();
		this.forestKnowledge = new HashMap<Position, Forest>();
	}
	/**
	 * Get positions of all the fire objects from the current knowledge
	 * @return a set of positions of all the fire objects
	 */
	public ArrayList<Position> getAllFire()
	{
		ArrayList<Position> returnArray = new ArrayList<>();
		
		if (fireKnowledge != null)
		{
			for (Position p : fireKnowledge.keySet()) 
			{
				if(fireKnowledge.get(p) != null) { returnArray.add(p); }
			}
		}
		
		return returnArray;
	}
	/**
	 * Get all the forest objects from the current knowledge
	 * @return  a set of positions of all the forest objects
	 */
	public ArrayList<Forest> getAllForest()
	{
		ArrayList<Forest> returnArray = new ArrayList<>();
		
		for (Forest f : forestKnowledge.values()) { returnArray.add(f); }
		
		return returnArray;
	}
	/**
	 * Get all the forester objects from the current knowledge
	 * @return  a set of positions of all the forester objects
	 */
	public ArrayList<Forester> getAllForester()
	{
		ArrayList<Forester> returnArray = new ArrayList<>();
		
		for (Forester f : foresterKnowledge.values()) { returnArray.add(f); }
		
		return returnArray;
	}
	/**
	 * Add a position of a fire to the current knowledge
	 * @param pos - position to put a fire to
	 * @return 0 - if this fire is already known, 1 - if the fire was unknown and was added to the knowledge
	 */
	public boolean addFire(Position pos)
	{
		for(Position p : fireKnowledge.keySet())
		{
			if(pos.equals(p)) { return false; }
		}
		
		fireKnowledge.put(pos, true);
		return true;
	}
	/**
	 * Add a forest object to a given position in a current knowledge
	 * @param f - forest object to add
	 * @param p - position at which the forest object should be added
	 */
	public void addForest(Forest f, Position p) { forestKnowledge.put(p, f); }
	/**
	 * Add a forester object to a given position in a current knowledge
	 * @param f - forester object to add
	 * @param p - position at which the forester object should be added
	 */
	public void addForester(Forester f, Position p)	{ foresterKnowledge.put(p, f); }
	/**
	 * Remove fire at a given position from a current knowledge
	 * @param pos - position from which the fire object should be removed
	 */
	public void removeFire(Position pos)
	{
		for(Position p : fireKnowledge.keySet())
		{
			if(pos.equals(p))
			{
				fireKnowledge.put(p,null);
				return;
			}
		}
	}
	/**
	 * Remove forest at a given position from a current knowledge
	 * @param pos - position from which the forest object should be removed
	 */
	public void removeForest(Position pos) { forestKnowledge.remove(pos); }
	/**
	 * Remove forester at a given position from a current knowledge
	 * @param pos - position from which the forester object should be removed
	 */
	public void removeForester(Position pos) { foresterKnowledge.remove(pos); }
	/**
	 * Get a position of a forester located at a given position in a current knowledge
	 * @param f - a forester object to get a position of
	 * @return - position of the given forester
	 */
	public Position getForesterPosition(Forester f)
	{
		for (Position p : foresterKnowledge.keySet())
		{			
			if (f.equals(foresterKnowledge.get(p))) { return p; } // TODO May cause problems --> Need to implement equals method in Forester
		}
		
		return null;
	}
	// Local getters
	public Boolean getFire(Position p) { return fireKnowledge.get(p); }
	public Forest getForest(Position p)	{ return forestKnowledge.get(p); }
	public Forester getForester(Position p)	{ return foresterKnowledge.get(p); }
}
