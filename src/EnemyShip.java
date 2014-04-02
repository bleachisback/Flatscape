
public class EnemyShip extends Enemy {
	public double health = 20;
	
	public EnemyShip(Weapon[] weapons, Point position) {
		this.weapons = weapons;
		this.position = position;
		this.velocity = new Point(0, 0);
		this.acceleration = new Point(0, 0);
		this.rotation = 0;
	}

	@Override
	public boolean detectHit(Physicsable source) {
		return source.position.distance(position) < 10 ;
	}
	
	@Override
	public void draw() {
		StdDraw.picture(position.x, position.y, "Enemy.png", 10, 10, -rotation);		
	}

	@Override
	public void onHit(Physicsable source) {
		if(hitBy.contains(source)) return;
		hitBy.add(source);
		health -= source.damage;
		
		if(health <= 0) {
			remove();
		}
	}
	
	@Override
	public void physics(double scale) {
		super.physics(scale);
		
		rotation = Math.toDegrees(Math.atan((Flatscape.player.position.y - position.y)/(Flatscape.player.position.x - position.x))) - 90;
		if (Flatscape.player.position.x < position.x) rotation -= 180;
		rotation *= -1;
		
		if(Double.isNaN(rotation)) rotation = 0;
	}

}