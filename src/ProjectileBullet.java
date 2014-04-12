
public class ProjectileBullet extends Projectile {	
	
	public ProjectileBullet(Point position, Point velocity, double damage, WeaponBullet source) {
		this.acceleration = new Point(0, 0);
		this.position = position;
		this.velocity = velocity;
		this.damage = damage;
		this.source = source;
		
		Flatscape.drawables.add(this);
		Flatscape.physicsAddition.put(this, true);
	}
	
	@Override
	public boolean detectHit(Physicsable source) {
		if(source == this.source.owner) return false;
		if(source instanceof Projectile) {
			if(((Projectile) source).source.owner == this.source.owner) return false;
		}
		if(source == null) return false;
		return source.position.distance(position) <= .9;
	}
	
	public void draw() {
		StdDraw.setPenColor(StdDraw.YELLOW);
		StdDraw.filledCircle(position.x, position.y, .9);
	}
	
	public void physics(double scale) {
		super.physics(scale);
		if (distance >= 300) { 
			Flatscape.drawables.remove(this);
			Flatscape.physicsAddition.put(this, false);
		}
	}
	
	public String toString() {
		return "Machine gun bullet";
	}

}