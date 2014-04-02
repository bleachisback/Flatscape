
public abstract class Projectile extends Physicsable implements Drawable {
	public Weapon source;
	
	public void onHit(Physicsable source) {
		if(hitBy.contains(source)) return;
		hitBy.add(source);
		remove();
	}
	
	public void remove() {
		super.remove();
		Flatscape.drawables.remove(this);
	}
}
