
public abstract class Enemy implements MeteorHitable, Drawable {
	public Point position;
	public Point velocity;
	
	public abstract boolean detectHit(Point point);
	public abstract void move(double scale);	
	
	public abstract void onHit();
	
	public void remove() {
		Flatscape.removeEnemy(this);
	}
}
