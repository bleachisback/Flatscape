import java.util.ArrayList;


public abstract class Physicsable {
	public Point acceleration;
	public Point position;
	public Point velocity;
	
	public double rotation;
	
	public static final int SHIELD_COOLDOWN = 5000;
	
	public double damage;
	public double health;
	public boolean hasShield = false;
	public double shield = 0;
	public int shieldCooldown = 0;
	public ArrayList<Physicsable> hitBy = new ArrayList<Physicsable>();
	
	public Weapon[] weapons;
	
	public abstract boolean detectHit(Physicsable source);
	
	public void draw() {
		if(!hasShield) return;
		//Drawing shield
		StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
		StdDraw.circle(position.x, position.y, shield);
	}
	
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
