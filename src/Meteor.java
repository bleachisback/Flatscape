import java.awt.Color;


public class Meteor extends Enemy{
	public static final Color[] COLORS = {Color.PINK, Color.GREEN, Color.WHITE, Color.RED};
	public static final double MAX_SPEED = .0075;
	public static final int POINTS = 5;
	
	public Color color;	
	public double[] pointsX;
	public double[] pointsY;
	public double size;
	
	public Meteor() {
		this(COLORS[(int) (Math.random() * COLORS.length)]);
	}
	
	public Meteor(Color color) {
		this(color, Meteor.randomPos(true), Meteor.randomPos(false));
	}
	
	public Meteor(Color color, double posX, double posY) {
		this(color, posX, posY, Flatscape.smallerHypot(posX*-1, posY*-1, MAX_SPEED));
	}
	
	public Meteor(Color color, double posX, double posY, double[] velocity) {
		this(color, posX, posY, velocity, Math.random() * .25 + .05125);
	}
	public Meteor(Color color, double posX, double posY, double[] velocity, double size) {
		this.color = color;
		this.velocity = velocity;
		this.posX = posX;
		this.posY = posY;
		this.size = size;
		pointsX = new double[POINTS];
		pointsY = new double[POINTS];
		double degrees = 180 * (POINTS - 2);
		degrees -= degrees / 3;
		double tempDegrees = 0;
		double length = size + (size / 2);
		pointsX[0] = Math.random() * (size - 0.001) + 0.001;
		pointsY[0] = Math.random() * (size - 0.001) + 0.001;
		
		/*StdDraw.setPenColor(StdDraw.RED);
		StdDraw.filledCircle(pointsX[0], pointsY[0], .009);*/
		
		pointsX[1] = Math.random() * (size * 4) - size;
		pointsY[1] = Math.random() * (size - 0.001) - size - 0.001;
		
		/*StdDraw.setPenColor(StdDraw.BLUE);
		StdDraw.filledCircle(pointsX[1], pointsY[1], .009);
		StdDraw.setPenColor(StdDraw.YELLOW);*/
		
		for(int i = 2; i < POINTS; i++) {
			tempDegrees = Math.random() * 180;
			tempDegrees = tempDegrees > degrees - 30 ? degrees - 30 : tempDegrees;
			length = Math.random() * length;			
			pointsX[i] = Math.cos(tempDegrees) * length + pointsX[i-1];
			pointsY[i] = Math.sin(tempDegrees) * length + pointsY[i-1];
			pointsX[i] = pointsX[i] > size ? size : pointsX[i] < -size ? -size : pointsX[i];
			pointsY[i] = pointsY[i] > size ? size : pointsY[i] < -size ? -size : pointsY[i];
			degrees -= tempDegrees;
			
			/*StdDraw.setPenColor(COLORS[i-2]);
			System.out.println((i + 1) + ": " + pointsX[i] + ", " + pointsY[i] + ", Degrees: " + tempDegrees + ", Length: " + length);
			System.out.println(Math.sin(tempDegrees));
			StdDraw.filledCircle(pointsX[i], pointsY[i], .009);*/
		}
		/*System.out.println("- - - - - - - - - - -");
		StdDraw.polygon(pointsX, pointsY);*/
	}
	
	public void move() {
		this.posX += velocity[0];
		this.posY += velocity[1];
		if(Math.abs(posX) >= 3 || Math.abs(posY) >= 3) {
			remove();
		}
	}
	
	public void draw() {
		StdDraw.setPenColor(color);
		double[] pointsX = new double[Meteor.POINTS];
		double[] pointsY = new double[Meteor.POINTS];
		for(int j = 0; j < Meteor.POINTS; j++) {
			pointsX[j] = this.pointsX[j] + this.posX;
			pointsY[j] = this.pointsY[j] + this.posY;
		}
		StdDraw.filledPolygon(pointsX, pointsY);		
	}
	
	private static boolean greaterThanOne = false;
	private static double randomPos(boolean first) {
		double pos = 0.0;
		if(first) {
			greaterThanOne = Math.random() > .5;
			if(greaterThanOne) {
				pos = Math.random() > .5 ? 1.25 : -1.25;
			} else {
				pos = Math.random() * 2 - 1;
			}
		} else if(greaterThanOne) {
			pos = Math.random() * 2 - 1;
		} else {
			pos = Math.random() > .5 ? 1.25 : -1.25;
		}
		return pos;
	}
	
}
