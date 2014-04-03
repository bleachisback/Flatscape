
public class Point {
	public double x;
	public double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point add(Point point) {
		x += point.x;
		y += point.y;
		return this;
	}
	
	public Point clone() {
		return new Point(x, y);
	}
	
	public Point deviate(double deviation, double max) {
		x += Math.random() * (deviation * 2) - deviation;
		y += Math.random() * (deviation * 2) - deviation;
		
		return FMath.smallerHypot(x, y, max);
	}
	
	public double distance(Point point) {
		return Math.sqrt(FMath.sqr(point.x - x) + FMath.sqr(point.y - y));
	}
	
	public Point divide(double divide) {
		x /= divide;
		y /= divide;
		return this;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof Point) {
			return (((Point) object).x == x && ((Point) object).y == y);
		}
		return false;
	}
	
	public Point multiply(double multiple) {
		x *= multiple;
		y *= multiple;
		return this;
	}
	
	public Point subtract(Point point) {
		x -= point.x;
		y -= point.y;
		return this;		
	}
	
	@Override
	public String toString() {
		return x + ", " + y;
	}	
}
