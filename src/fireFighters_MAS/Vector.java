package fireFighters_MAS;

public class Vector {
	private Point direction;
	private double degree;

	public Vector(Point direction) {
		this.direction = direction;
		this.setDegree(getDegreeFromVector(direction));// -TODO implement this
	}

	public Vector(double degree) {
		this.direction = calcVectorFromDegree(degree);
		this.setDegree(degree);
	}

	public Point getDirection() {
		return direction;
	}

	public void setDirection(Point direction) {
		this.direction = direction;
	}

	public double getDegree() {
		return degree;
	}

	public void setDegree(double degree) {
		this.degree = degree;
	}

	private Point calcVectorFromDegree(double alpha) {
		Point p = new Point(0, 1);
		p.setX(p.getX() * Math.cos(alpha) - p.getY() * Math.sin(alpha));
		p.setY(p.getX() * Math.sin(alpha) + p.getY() * Math.cos(alpha));
		return p;
	}

	private double getDegreeFromVector(Point p) {
		double degree=0;
		if(p.getX()!=0 && p.getX()!=0)
			Math.toDegrees(Math.atan2(0-p.getX(), 0-p.getY()));
		return degree;
	}
}
