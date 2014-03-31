
public class Player extends Physicsable implements Drawable, MeteorHitable, Weaponable {
	private Weapon[] weapons = new Weapon[4];
	
	public Player(Point position) {
		this.acceleration = new Point(0, 0);
		this.position = position;
		this.velocity = new Point(0, 0);
		this.rotation = 0;
		
		weapons[0] = new WeaponBullet();
	}
	
	public void draw() {
		//Draw player ship
		StdDraw.picture(position.x, position.y, "Triangle.png", 10, 10, -rotation);
		
		//Drawing targeting reticule
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.circle(StdDraw.mouseX(), StdDraw.mouseY(), 5);
	}
	
	public Weapon[] getWeapons() {		
		return weapons;		
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
		
		for(Weapon weapon : weapons) {
			if(weapon == null) continue;
			weapon.physics(scale);
			if(weapon.cooldown <= 0 && StdDraw.mousePressed()) {
				weapon.shoot(this);
			}
		}
	}
}
