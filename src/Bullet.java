
public class Bullet extends Projectile {

	public Bullet(Point position, Point velocity, int damage) {
		this.acceleration = new Point(0, 0);
		this.position = position;
		this.velocity = velocity;
		this.damage = damage;
	}
	
	public void draw() {
		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.filledCircle(position.x, position.y, .9);
	}

	public void physics(double scale) {
		super.physics(scale);
		if (Math.abs(position.x) >= 107.5 || Math.abs(position.y) >= 107.5) { 
			Flatscape.removeBullet(this);
		}
	}

}
