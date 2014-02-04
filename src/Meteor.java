import java.awt.Color;
import java.util.ArrayList;


public class Meteor extends Enemy{
	public static final Color[] COLORS = {Color.PINK, Color.GREEN, Color.WHITE, Color.RED};
	public static final double MAX_SPEED = 0.75;
	public static final int POINTS = 5;
	
	public Color color;
	public ArrayList<Meteor> noHitList = new ArrayList<Meteor>();
	public Point[] points;
	public double rotation;
	public double size;
	
	public Meteor() {
		this(COLORS[(int) (Math.random() * COLORS.length)]);
	}
	
	public Meteor(Color color) {
		this(color, Meteor.randomPos());
	}
	
	public Meteor(Color color, Point pos) {
		this(color, pos, FMath.smallerHypot(pos.x * -1, pos.y * -1, MAX_SPEED).deviate(0.25, MAX_SPEED));
	}
	
	public Meteor(Color color, Point pos, Point velocity) {
		this(color, pos, velocity, Math.random() * 20 + 7.5);
	}
	
	public Meteor(Color color, Point pos, Point velocity, double size) {
		this(color, pos, velocity, size, Math.random() * 5 - 2.5);
	}
	
	public Meteor(Color color, Point pos, Point velocity, double size, double rotation) {
		this.color = color;
		this.velocity = velocity;
		this.position = pos;
		this.size = size;
		this.rotation = rotation;
		initialise();
	}
	
	public boolean detectHit(Point point) {
		if(FMath.hitboxCheck(new Point(position.x + size, position.y + size), new Point(position.x - size, position.y - size), point)) {
			return FMath.insidePolygon(point, getRealPoints());
		}
		return false;
	}
	
	public Point[] getRealPoints() {
		Point[] points = new Point[POINTS];
		for(int i = 0; i < POINTS; i++) {
			points[i] = this.points[i].add(position);
		}
		return points;
	}
	
	public void draw() {
		StdDraw.setPenColor(color);
		double[] pointsX = new double[Meteor.POINTS];
		double[] pointsY = new double[Meteor.POINTS];
		for(int j = 0; j < Meteor.POINTS; j++) {
			pointsX[j] = points[j].x + position.x;
			pointsY[j] = points[j].y + position.y;
		}
		StdDraw.polygon(pointsX, pointsY);		
	}
	
	private void initialise() {
		points = new Point[POINTS];
		double degrees = 180 * (POINTS - 2);
		degrees -= degrees / 3;
		double tempDegrees = 0;
		double length = size + (size / 2);
		points[0] = new Point(Math.random() * (size - 0.1) + 0.1, Math.random() * (size - 0.1) + 0.1);
		
		/*StdDraw.setPenColor(StdDraw.RED);
		StdDraw.filledCircle(pointsX[0], pointsY[0], 0.9);*/
		
		points[1] = new Point(Math.random() * (size * 2) - size, Math.random() * (size - 0.1) - size - 0.1);
		
		/*StdDraw.setPenColor(StdDraw.BLUE);
		StdDraw.filledCircle(pointsX[1], pointsY[1], 0.9);
		StdDraw.setPenColor(StdDraw.YELLOW);*/
		
		for(int i = 2; i < POINTS; i++) {
			tempDegrees = Math.random() * Math.min(180, degrees - 30);
			length = Math.random() * (size) + (size / 2);
			points[i] = new Point(Math.cos(tempDegrees) * length + points[i-1].x, Math.sin(tempDegrees) * length + points[i-1].y);
			points[i].x = points[i].x > size ? size : points[i].x < -size ? -size : points[i].x;
			points[i].y = points[i].y > size ? size : points[i].y < -size ? -size : points[i].y;
			for(int j = i - 1; j >= 0 && i != POINTS - 1; j--) {
				if(points[j].equals(points[i])) {					
					initialise();
					return;
				}				
			}			
			
			degrees -= tempDegrees;
			
			/*StdDraw.setPenColor(COLORS[i-2]);
			System.out.println((i + 1) + ": " + pointsX[i] + ", " + pointsY[i] + ", Degrees: " + tempDegrees + ", Length: " + length);
			System.out.println(Math.sin(tempDegrees));
			StdDraw.filledCircle(pointsX[i], pointsY[i], 0.9);*/
		}
		//Check for any intersecting lines
		//If any lines intersect, remake meteor
		for(int i = 2; i < POINTS; i++) {
			for(int j = i - 1; j > 0 ; j--) {
				if(FMath.doIntersect(points[i], points[(i + 1) % POINTS], points[j], points[j - 1])) {
					initialise();
					return;
				}
			}
		}
		//Center the meteor
		int x = 0;
		int y = 0;
		for(Point point: points) {
			x += point.x;
			y += point.y;
		}
		x /= points.length;
		y /= points.length;
		for(Point point: points) {
			point.x -= x;
			point.y -= y;
		}
		/*System.out.println("- - - - - - - - - - -");
		StdDraw.polygon(pointsX, pointsY);*/
	}
	
	public void physics(double scale) {
		this.position.x += velocity.x * scale;
		this.position.y += velocity.y * scale;
		if(Math.abs(position.x) >= 300 || Math.abs(position.y) >= 300) {
			remove();
		}
		rotate(rotation * scale);
	}
	
	public void onHit() {
		FMath.playSound("Meteor_Destroy0");
		remove();
		if(size < 18) return;
		ArrayList<Meteor> meteors = new ArrayList<Meteor>();
		for (int i = 0; i < 4; i++) {
			Point newPos = randomRelativePos();
			Point newVelocity = FMath.smallerHypot(position.x - newPos.x, position.y - newPos.y, MAX_SPEED / 1.5);
			Meteor _meteor = new Meteor(color, newPos, newVelocity, size / 2);
			meteors.add(_meteor);
			Flatscape.addEnemy(_meteor);
		}
		for(Meteor _meteor : meteors) {
			for(Meteor $meteor : meteors) {
				if(_meteor == $meteor) continue;
				_meteor.noHitList.add($meteor);
			}
		}
	}
	
	public void onMeteorHit(Meteor meteor) {
		if(noHitList.contains(meteor)) return;
		meteor.onHit();
		onHit();		
	}
	
	private static Point randomPos() {
		Point point = new Point(0, 0);
		if(Math.random() > .5) {
			point.x = FMath.randomNegative(125);
			point.y = Math.random() * (Flatscape.SCALE * 2) - Flatscape.SCALE;
		} else {
			point.x = Math.random() * (Flatscape.SCALE * 2) - Flatscape.SCALE;
			point.y = FMath.randomNegative(125);
		}
		return point;
	}
	
	private Point randomRelativePos() {
		return new Point(position.x + FMath.randomNegative(Math.random() * 5 + 5), position.y + FMath.randomNegative(Math.random() * 5 + 5));
	}
	
	public void rotate(double rotation) {
		for(Point point : points) {
			double x = point.x * Math.cos(Math.toRadians(rotation)) + point.y * Math.sin(Math.toRadians(rotation));
			double y = -point.x * Math.sin(Math.toRadians(rotation)) + point.y * Math.cos(Math.toRadians(rotation));
			point.x = x;
			point.y = y;
		}
	}
	
	public String toString() {
		return FMath.getColorString(color) + "Meteor " + size + " large.";
	}
}
