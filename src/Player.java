
public class Player extends Physicsable implements Drawable, MeteorHitable {
	
	public double rotation = 0;//Degrees of current rotation
	
	public Player(Point position) {
		this.acceleration = new Point(0, 0);
		this.position = position;
		this.velocity = new Point(0, 0);
	}
	
	public void draw() {
		//Draw player ship
		StdDraw.picture(position.x, position.y, "Triangle.png", 10, 10, -rotation);
		
		//Drawing targeting reticule
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.circle(StdDraw.mouseX(), StdDraw.mouseY(), 5);
	}

	public void onMeteorHit(Meteor meteor) {
		meteor.onHit();
		Flatscape.gameOver = true;
	}
	
	public void physics(double scale) {
		super.physics(scale);
		
		//Figure out which direction the player is pointing, in degrees
		rotation = Math.toDegrees(Math.atan((StdDraw.mouseY() - position.y)/(StdDraw.mouseX() - position.x))) - 90;
		if (StdDraw.mouseX() < position.x) rotation -= 180;
		rotation *= -1;
		
		if(Double.isNaN(rotation)) rotation = 0;
				
		double x = position.x;
		double y = position.y;		
		if(x > Flatscape.SCALE * .9) {			
			for(Physicsable physics : Flatscape.physics) {
				physics.position.x -= x - Flatscape.SCALE * .9;
			}
		} else if(x < -(Flatscape.SCALE * .9)) {			
			for(Physicsable physics : Flatscape.physics) {
				physics.position.x -= x + Flatscape.SCALE * .9;
			}
		}
		if(y > Flatscape.SCALE * .9) {			
			for(Physicsable physics : Flatscape.physics) {
				physics.position.y -= y - Flatscape.SCALE * .9;
			}
		} else if(y < -(Flatscape.SCALE * .9)) {			
			for(Physicsable physics : Flatscape.physics) {
				physics.position.y -= y + Flatscape.SCALE * .9;
			}
		}
	}
}
