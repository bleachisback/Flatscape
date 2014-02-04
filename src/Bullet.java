
public class Bullet extends Projectile {

	public Bullet(Point position, Point velocity, int damage) {
		this.position = position;
		this.velocity = velocity;
		this.damage = damage;
	}
	
	public void draw() {
		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.filledCircle(position.x, position.y, .9);
	}

	public void physics(double scale) {
		if (Math.abs(position.x) >= 107.5 || Math.abs(position.y) >= 107.5) { 
			Flatscape.removeBullet(this);
		}				
		position.x = position.x + velocity.x * scale;
		position.y = position.y + velocity.y * scale;
	}

}
