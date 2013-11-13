import java.util.ArrayList;

public class Flatscape { 
	public static void main(String[] args) {

		// set the scale of the coordinate system
		StdDraw.setXscale(-1.0, 1.0);
		StdDraw.setYscale(-1.0, 1.0);

		// initial values 
		double rx  = .48; double ry = .86;   // position (character)
		ArrayList<double[]> bp = new ArrayList<double[]>();     // position (bullets)
		ArrayList<double[]> bv = new ArrayList<double[]>();     // velocity (bullets)
		double vx = 0.015, vy = 0.023;     // velocity of ship		
		double TriAngle = 0;
		double velocity = 0.01; //The distance the bullet travels, per frame
		double angle = 0; double opp = 0; double adj = 0;//used for determining vector
		int delay = 20; int curDelay = 0;//used to put [delay] loops in between each bullet spawning
		


		//HELLO KUI CAN YOU SEE THIS????
		//HELLO KUI CAN YOU SEE THIS????
		//HELLO KUI CAN YOU SEE THIS????
		//HELLO KUI CAN YOU SEE THIS????
		//HELLO KUI CAN YOU SEE THIS????
		//HELLO KUI CAN YOU SEE THIS????
		
		
		// main animation loop
		while (true)  {

			// changes angle on character (Stolen from tim's program)
			TriAngle = Math.toDegrees(Math.atan((StdDraw.mouseY() - ry)/(StdDraw.mouseX() - rx))) - 90;
			if (StdDraw.mouseX() < rx) {TriAngle = TriAngle - 180;}
			// updates position, moving character towards mouse
			if (StdDraw.mouseX() != rx && StdDraw.mouseY() != ry && StdDraw.mousePressed()) {
				vx = (StdDraw.mouseX() - rx) / 10;
				vy = (StdDraw.mouseY() - ry) / 10;
				rx = rx + vx;
				ry = ry + vy; 
			}

			// Target drawn
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.circle(StdDraw.mouseX(), StdDraw.mouseY(), .05);

			// draw character on the screen
			StdDraw.setPenColor(StdDraw.BLUE); 
			StdDraw.picture(rx, ry, "Triangle.png",.1,.1,TriAngle);

			StdDraw.setPenColor(StdDraw.YELLOW);
			// Resets bullets
			if(curDelay <= 0)
			{
				curDelay = delay;
				//Changes the direction of the bullet, based on the direction of the character
				adj = StdDraw.mouseX() - rx;
				opp = StdDraw.mouseY() - ry;
				angle = Math.atan(opp / adj);
				double[] tempV = {Math.cos(angle) * velocity * (adj / Math.abs(adj)), Math.sin(angle) * velocity * (adj / Math.abs(adj))};
				bv.add(tempV);
				double[] tempP = {rx, ry};
				bp.add(tempP);
			}
			double[][] posArray = bp.toArray(new double[0][0]);
			double[][] velArray = bv.toArray(new double[0][0]);
			bp.clear();
			bv.clear();
			for(int i = 0;i < posArray.length; i++)
			{
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
			curDelay--;

			StdDraw.show(0); 
			StdDraw.picture(0,0, "Background.png", 5, 5);
		} 
	} 
} 