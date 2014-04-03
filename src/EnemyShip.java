
public class EnemyShip extends Enemy {
	public double health = 20;
	
	public EnemyShip() {
		this(randomWeapon());
	}
	
	public EnemyShip(Weapon[] weapons) {
		this(weapons, FMath.randomPos());
	}
	
	public EnemyShip(Weapon[] weapons, Point position) {
		this.weapons = weapons;
		this.position = position;
		this.velocity = new Point(0, 0);
		this.acceleration = new Point(0, 0);
		this.rotation = 0;
	}

	@Override
	public boolean detectHit(Physicsable source) {
		if(source instanceof Projectile) {
			if(((Projectile) source).source.owner == this) {
				return false;
			}
		}
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
			FMath.playSound("Meteor_Destroy0");
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
		
		acceleration.x = acceleration.y = 0;
		Point point = FMath.smallerHypot(Flatscape.player.position.x - position.x, Flatscape.player.position.y - position.y, Flatscape.SHIP_SPEED / 2);		
		if(position.distance(Flatscape.player.position) >= 75) {
			acceleration.add(point);
		} else if(position.distance(Flatscape.player.position) <= 50) {
			acceleration.subtract(point);
			acceleration.subtract(point);
		} else {
			acceleration.divide(2);
			
			double angle = 0;
			angle = rotation - 90;
			if(angle < 0) angle += 360;
			Point _point = FMath.circlePoint(Flatscape.SHIP_SPEED, angle);
			
			if(velocity.clone().distance(_point) <= point.clone().distance(_point)) {
				acceleration.add(FMath.circlePoint(Flatscape.SHIP_SPEED / 2.5, angle));
			} else {
				acceleration.add(FMath.circlePoint(Flatscape.SHIP_SPEED / 2.5, angle + 180));
			}
		}
		if(velocity.distance(new Point(0, 0)) > Flatscape.SHIP_SPEED * 100) {
			acceleration.subtract(velocity.clone().divide(1.3));
		}
		if(acceleration.distance(new Point(0, 0)) > Flatscape.SHIP_SPEED * 3) {
			acceleration.divide(2);
		}
		
		for(Weapon weapon : weapons) {
			weapon.physics(scale);
			if(weapon.cooldown <= 0) {
				weapon.shoot(this);
			}
		}
	}
	
	public static Weapon[] randomWeapon() {
		Weapon[] weapons = new Weapon[1];
		weapons[0] = Math.random() > .5 ? new WeaponRocket(800) : new WeaponBullet(600);
		return weapons;
	}

}