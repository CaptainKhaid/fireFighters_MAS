package fireFighters_MAS;

public class Position
{	
	// Local variables declaration
	private int x, y;
	/**
	 * Custom constructor
	 * @param x - X coordinate
	 * @param y - Y coordinate
	 */
	public Position(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	@Override
	public boolean equals(Object obj)
	{
		Position p = (Position) obj;
		return (this.x == p.x && this.y == p.y);
	}
	@Override
	public String toString() { return "("+x+"|"+y+")"; }
	// Local getters
	public int getX() { return x; }
	public int getY() { return y; }
	// Public setters
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }	
}
