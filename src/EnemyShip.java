
public class EnemyShip extends Enemy implements Weaponable {
	public double health = 20;
	public Weapon weapon;
	
	public EnemyShip(Weapon weapon, Point position) {
		this.weapon = weapon;
		this.position = position;
		this.velocity = new Point(0, 0);
		this.acceleration = new Point(0, 0);
		this.rotation = 0;
	}

	@Override
	public boolean detectHit(Point point) {
		return point.distance(position) < 10 ;
	}
	
	@Override
	public void draw() {
		StdDraw.picture(position.x, position.y, "Enemy.png", 10, 10, -rotation);		
	}

	@Override
	public Weapon[] getWeapons() {
		Weapon[] weapons = new Weapon[1];
		weapons[0] = weapon;
		return weapons;
	}

	@Override
	public void onHit(double damage, Physicsable source) {
		health -= damage;
		
		if(health <= 0) {
			remove();
		}
	}

}
