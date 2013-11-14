import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Flatscape implements KeyListener{ 
	
	private static ArrayList<double[]> bp = new ArrayList<double[]>();     // position (bullets)
	private static ArrayList<double[]> bv = new ArrayList<double[]>();     // velocity (bullets)
	private static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private static ArrayList<Enemy> enemyRemoval = new ArrayList<Enemy>();
	private static double rx  = .48, ry = .86;   // position (character)		
	private static double vx = 0.015, vy = 0.023;     // velocity (character)	
	
	public static final double BULLET_SPEED = 0.0175;    //The distance the bullet travels, per frame
	public static final int BULLET_DELAY = 15;	
	public static final int METEOR_DELAY = 80;
	public static final double METEOR_MAX_SPEED = .0075;
	public static final double SHIP_SPEED = .015;
	
	public static boolean stop = false;
	
	public static void main(String[] args) {
		JFrame frame = null;
		try {
			Field frameField = StdDraw.class.getDeclaredField("frame");
			frameField.setAccessible(true);
			frame = (JFrame) frameField.get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {
			e1.printStackTrace();
		}
		frame.addKeyListener(new Flatscape());
		// set the scale of the coordinate system
		StdDraw.setXscale(-1.0, 1.0);
		StdDraw.setYscale(-1.0, 1.0);		
		
		int currentBulletDelay = 0;
		int currentMeteorDelay = METEOR_DELAY;

		// main animation loop		
		while (true)  {
			keyboard();
			drawCursor();
			removeEnemies();

			if(currentBulletDelay <= 0) {
				currentBulletDelay = 0;
				if(StdDraw.mousePressed()) {
					currentBulletDelay = BULLET_DELAY;
					addBullet();					
				}
			}			
			if(currentMeteorDelay <= 0) {
				currentMeteorDelay = METEOR_DELAY;
				enemies.add(new Meteor());
			}

			drawBullets();
			if(stop) continue;
			for(Enemy enemy : enemies) {
				enemy.draw();
				enemy.move();
			}			
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
	
	public static void removeEnemy(Enemy enemy) {
		enemyRemoval.add(enemy);
	}
	
	private static void removeEnemies() {
		for(Enemy enemy : enemyRemoval) {
			enemies.remove(enemy);
		}
		enemyRemoval.clear();
	}
	
	public static double[] smallerHypot(double adj, double opp, double targetHypot) {
		double angle = Math.atan(opp / adj);
		double[] returnee = {Math.cos(angle) * targetHypot * (adj / Math.abs(adj)), Math.sin(angle) * targetHypot * (adj / Math.abs(adj))};
		return returnee;
	}
	
	public Flatscape() {}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			stop = !stop;
		}		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
} 