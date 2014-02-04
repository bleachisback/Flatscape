
public abstract class Enemy implements MeteorHitable, Drawable, Physicsable {
	public Point position;
	public Point velocity;
	
	public abstract boolean detectHit(Point point);
	public abstract void physics(double scale);	
	
	public abstract void onHit();
	
	public void remove() {
		Flatscape.removeEnemy(this);
	}
}
