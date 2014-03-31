
public abstract class Enemy extends Physicsable implements MeteorHitable, Drawable {	
	public abstract boolean detectHit(Point point);
	
	public abstract void onHit();
	
	public void remove() {
		Flatscape.enemyAddition.put(this, false);
	}
}
