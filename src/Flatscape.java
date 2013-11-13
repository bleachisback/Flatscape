import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Flatscape { 
	
	private static ArrayList<double[]> bp = new ArrayList<double[]>();     // position (bullets)
	private static ArrayList<double[]> bv = new ArrayList<double[]>();     // velocity (bullets)
	private static ArrayList<Color> mc = new  ArrayList<Color>();    // color (meteors);
	private static ArrayList<double[][]> mp = new ArrayList<double[][]>();    // position (meteors)
	private static ArrayList<double[]> mv = new ArrayList<double[]>();    // velocity (meteors)
	private static double rx  = .48, ry = .86;   // position (character)		
	private static double vx = 0.015, vy = 0.023;     // velocity (character)
	
	public static final double BULLET_SPEED = 0.0175;    //The distance the bullet travels, per frame
	public static final int BULLET_DELAY = 15;
	public static final Color[] METEOR_COLORS = {Color.PINK, Color.GREEN, Color.WHITE, Color.RED};
	public static final int METEOR_DELAY = 80;
	public static final double METEOR_MAX_SPEED = .0075;
	public static final double SHIP_SPEED = .015;
	
	public static void main(String[] args) {

		// set the scale of the coordinate system
		StdDraw.setXscale(-1.0, 1.0);
		StdDraw.setYscale(-1.0, 1.0);
		 
		int currentBulletDelay = 0;
		int currentMeteorDelay = METEOR_DELAY;

		// main animation loop		
		while (true)  {
			keyboard();
			drawCursor();			

			if(currentBulletDelay <= 0) {
				currentBulletDelay = 0;
				if(StdDraw.mousePressed()) {
					currentBulletDelay = BULLET_DELAY;
					addBullet();					
				}
			}			
			if(currentMeteorDelay <= 0) {
				currentMeteorDelay = METEOR_DELAY;
				addMeteor();
			}

			drawBullets();
			drawMeteors();
			currentBulletDelay--;
			currentMeteorDelay--;
			
			StdDraw.show(0); 	
			StdDraw.picture(0,0, "Background.png", 5, 5);
		} 
	}
	
	private static void addBullet() {
		double[] tempV = smallerHypot(StdDraw.mouseX() - rx, StdDraw.mouseY() - ry, BULLET_SPEED);    //velocity is equal to BULLET_SPEED in the direction of the mouse pointer in relation to the character
		double[] tempP = {rx, ry};    //initial position of bullet is equal to position of character
		bv.add(tempV);		
		bp.add(tempP);
	}
	
	private static void addMeteor() {
		double[] xArr = new double[5], yArr = new double[5];
		double size = .25;
		
		double xOffset = 0;
		double yOffset = 0;
		switch(((int) (Math.random() * 3))) {
			case 0:
				xOffset = -1.25;
				break;
			case 1:
				xOffset = 1.25;
				break;
			case 2:
				yOffset = 1.25;
				break;
			case 3:
				yOffset = -1.25;
				break;
		}
		xOffset += xOffset == 0 ? Math.random() * 3 - 1 : xOffset;
		yOffset += yOffset == 0 ? Math.random() * 3 - 1 : yOffset;
		
		xArr = new double[5];
		yArr = new double[5];
		double[] hitbox = {0, 0, 0, 0};
		for(int i=0; i<5; i++)
		{
			xArr[i] = Math.random() * size * 2 - size + xOffset;
			yArr[i] = Math.random() * size * 2 - size + yOffset;
			hitbox[0] = yArr[i] - yOffset > hitbox[0] ? yArr[i] - yOffset : hitbox[0];
		}
		double[][] coords = {xArr, yArr};
		mp.add(coords);
		double[] velocity = smallerHypot(xOffset*-1, yOffset*-1, METEOR_MAX_SPEED);
		velocity[0] += Math.random() * .01 - .005;
		velocity[1] += Math.random() * .01 - .005;
		mv.add(velocity);
		int random = (int) (Math.random() * METEOR_COLORS.length);
		mc.add(METEOR_COLORS[random]);
	}
	
	//Every frame, draw every bullet and advance their position
	private static void drawBullets() {
		StdDraw.setPenColor(StdDraw.YELLOW);
		
		double[][] posArray = bp.toArray(new double[0][0]);
		double[][] velArray = bv.toArray(new double[0][0]);
		bp.clear();
		bv.clear();
		for(int i = 0;i < posArray.length; i++) {
			double[] bPos = posArray[i];
			double[] bVelocity = velArray[i];
			if (Math.abs(bPos[0]) >= 1.075 || Math.abs(bPos[1]) >= 1.075) { 
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
	
	private static void drawMeteors() {
		double[][][] posArray = mp.toArray(new double[0][0][0]);
		double[][] velArray = mv.toArray(new double[0][0]);
		Color[] colorArray = mc.toArray(new Color[0]);
		mp.clear();
		mv.clear();
		mc.clear();
		for(int i = 0;i < posArray.length; i++) {
			double[][] mPos = posArray[i];
			double[] mVelocity = velArray[i];
			if (Math.abs(mPos[0][0]) >= 3 || Math.abs(mPos[1][0]) >= 3) { 
				continue;
			}
			StdDraw.setPenColor(colorArray[i]);
			for(int j = 0; j < mPos[0].length; j++) {
				mPos[0][j] = mPos[0][j] + mVelocity[0];
				mPos[1][j] = mPos[1][j] + mVelocity[1];
			}
			StdDraw.polygon(mPos[0], mPos[1]);
			mp.add(mPos);
			mv.add(mVelocity);
			mc.add(colorArray[i]);
		}		
	}
	
	private static void keyboard() {
		vx = vy = 0;
		if(StdDraw.isKeyPressed(KeyEvent.VK_W)) {
			vy += SHIP_SPEED;
		} if(StdDraw.isKeyPressed(KeyEvent.VK_A)) {
			vx -= SHIP_SPEED;
		} if(StdDraw.isKeyPressed(KeyEvent.VK_S)) {
			vy -= SHIP_SPEED;
		} if(StdDraw.isKeyPressed(KeyEvent.VK_D)) {
			vx += SHIP_SPEED;
		}
	}
	
	public static double[] smallerHypot(double adj, double opp, double targetHypot) {
		double angle = Math.atan(opp / adj);
		double[] returnee = {Math.cos(angle) * targetHypot * (adj / Math.abs(adj)), Math.sin(angle) * targetHypot * (adj / Math.abs(adj))};
		return returnee;
	}
} 