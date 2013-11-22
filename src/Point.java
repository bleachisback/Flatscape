
public class Point {
	public double x;
	public double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point add(Point point) {
		return new Point(x + point.x, y + point.y);
	}
	
	public Point deviate(double deviation, double max) {
		x += Math.random() * (deviation * 2) - deviation;
		y += Math.random() * (deviation * 2) - deviation;
		
		return FMath.smallerHypot(x, y, max);
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof Point) {
			return (((Point) object).x == x && ((Point) object).y == y);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return x+", "+y;
	}
}
