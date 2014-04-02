
public class ProjectileBullet extends Projectile {	
	
	public ProjectileBullet(Point position, Point velocity, double damage, WeaponBullet source) {
		this.acceleration = new Point(0, 0);
		this.position = position;
		this.velocity = velocity;
		this.damage = damage;
		this.source = source;
	}
	
	@Override
	public boolean detectHit(Point point) {
		return point.distance(position) <= .9;
	}
	
	public void draw() {
		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.filledCircle(position.x, position.y, .9);
	}

	@Override
	public void onHit(double damage, Physicsable source) {
		Flatscape.drawables.remove(this);
		Flatscape.physicsAddition.put(this, false);
	}
	
	public void physics(double scale) {
		super.physics(scale);
		if (Math.abs(position.x) >= 107.5 || Math.abs(position.y) >= 107.5) { 
			Flatscape.drawables.remove(this);
			Flatscape.physicsAddition.put(this, false);
		}		
	}

}
