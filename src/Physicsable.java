import java.util.ArrayList;


public abstract class Physicsable {
	public Point acceleration;
	public Point position;
	public Point velocity;
	
	public double rotation;
	
	public double damage;
	public double health;
	public ArrayList<Physicsable> hitBy = new ArrayList<Physicsable>();
	
	public Weapon[] weapons;
	
	public abstract boolean detectHit(Physicsable source);
	
	
	public abstract void onHit(Physicsable source);
	
	public void physics(double scale) {
		velocity.x += acceleration.x * scale;
		velocity.y += acceleration.y * scale;
		position.x += velocity.x * scale;
		position.y += velocity.y * scale;
	}
	
	public void remove() {
		Flatscape.physicsAddition.put(this, false);
	}
}
