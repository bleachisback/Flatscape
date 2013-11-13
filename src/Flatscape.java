import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Flatscape { 
	
	private static ArrayList<double[]> bp = new ArrayList<double[]>();     // position (bullets)
	private static ArrayList<double[]> bv = new ArrayList<double[]>();     // velocity (bullets)
	private static double rx  = .48, ry = .86;   // position (character)		
	private static double vx = 0.015, vy = 0.023;     // velocity (character)
	
	public static final double BULLET_SPEED = 0.0175;    //The distance the bullet travels, per frame
	public static final int BULLET_DELAY = 15;
	
	public static void main(String[] args) {

		// set the scale of the coordinate system
		StdDraw.setXscale(-1.0, 1.0);
		StdDraw.setYscale(-1.0, 1.0);
		 
		 int curDelay = 0;//used to put [delay] loops in between each bullet spawning

		// main animation loop		
		while (true)  {

			keyboard();
			drawCursor();			

			if(curDelay <= 0) {
				curDelay = 0;
				if(StdDraw.mousePressed()) {
					curDelay = BULLET_DELAY;
					addBullet();					
				}
			}
			
			drawBullets();
			
			curDelay--;

			StdDraw.show(0); 	
			StdDraw.picture(0,0, "Background.png", 5, 5);
		} 
	}
	
	private static void addBullet() {
		double adj = StdDraw.mouseX() - rx;
		double opp = StdDraw.mouseY() - ry;
		double angle = Math.atan(opp / adj);
		double[] tempV = {Math.cos(angle) * BULLET_SPEED * (adj / Math.abs(adj)), Math.sin(angle) * BULLET_SPEED * (adj / Math.abs(adj))};    //velocity is equal to BULLET_SPEED in the direction of the mous pointer in relation to the character
		double[] tempP = {rx, ry};    //initial position of bullet is equal to position of character
		bv.add(tempV);		
		bp.add(tempP);
	}
	
	private static void drawBullets() {
		StdDraw.setPenColor(StdDraw.YELLOW);
		
		double[][] posArray = bp.toArray(new double[0][0]);
		double[][] velArray = bv.toArray(new double[0][0]);
		bp.clear();
		bv.clear();
		for(int i = 0;i < posArray.length; i++) {
			double[] bPos = posArray[i];
			double[] bVelocity = velArray[i];
			if (Math.abs(bPos[0]) >= 1 || Math.abs(bPos[1]) >= 1) { 
				bv.remove(bVelocity);
				bp.remove(bPos);
				continue;
			}				
			bPos[0] = bPos[0] + bVelocity[0];
			bPos[1] = bPos[1] + bVelocity[1];
			StdDraw.filledCircle(bPos[0], bPos[1], .009);
			bp.add(bPos);
			bv.add(bVelocity);
		}
	}
	
	private static void drawCursor() {
		// changes angle on character (Stolen from tim's program)
		double TriAngle = Math.toDegrees(Math.atan((StdDraw.mouseY() - ry)/(StdDraw.mouseX() - rx))) - 90;
		if (StdDraw.mouseX() < rx) TriAngle = TriAngle - 180;
		
		// updates position, moving character towards mouse
		/*if (StdDraw.mouseX() != rx && StdDraw.mouseY() != ry && StdDraw.mousePressed()) {
			vx = (StdDraw.mouseX() - rx) / 30;
			vy = (StdDraw.mouseY() - ry) / 30;
			 
		}*/
		
		rx = rx + vx;
		ry = ry + vy;
		rx = rx > 1 ? 1 : rx < -1 ? -1 : rx;
		ry = ry > 1 ? 1 : ry < -1 ? -1 : ry;

		// Target drawn
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.circle(StdDraw.mouseX(), StdDraw.mouseY(), .05);

		// draw character on the screen
		StdDraw.setPenColor(StdDraw.BLUE); 
		StdDraw.picture(rx, ry, "Triangle.png",.1,.1,TriAngle);
	}
	
	private static void keyboard() {
		vx = vy = 0;
		if(StdDraw.isKeyPressed(KeyEvent.VK_W)) {
			vy += .025;
		} if(StdDraw.isKeyPressed(KeyEvent.VK_A)) {
			vx -= .025;
		} if(StdDraw.isKeyPressed(KeyEvent.VK_S)) {
			vy -= .025;
		} if(StdDraw.isKeyPressed(KeyEvent.VK_D)) {
			vx += .025;
		}
	}
} 