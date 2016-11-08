package fireFighters_MAS;

public class Vector {
	private Point start;
	private Point direction;

	public Vector(Point start, Point direction) {
		super();
		this.start = start;
		this.direction = direction;
	}

	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public Point getDirection() {
		return direction;
	}

	public void setDirection(Point direction) {
		this.direction = direction;
	}
}
