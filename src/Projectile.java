
public abstract class Projectile extends Physicsable implements Drawable {
	public Weapon source;
	public double distance = 0;
	
	public void onHit(Physicsable source) {
		if(hitBy.contains(source)) return;
		hitBy.add(source);
		remove();
	}
	
	public void physics(double scale) {
		super.physics(scale);
		distance += velocity.distance(new Point(0, 0));
	}
	
	public void remove() {
		super.remove();
		Flatscape.drawables.remove(this);
	}
}
