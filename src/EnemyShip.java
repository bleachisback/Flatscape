
public class EnemyShip extends Enemy {
	public double health = 20;
	public Physicsable target;
	
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
		this.damage = 100;
		this.hasShield = (Flatscape.level > 2 || Flatscape.level == 0) && Math.random() > .5;
		if(hasShield) shield = 10;
		target = findTarget();
	}

	@Override
	public boolean detectHit(Physicsable source) {
		if(source instanceof Projectile) {
			if(((Projectile) source).source.owner == this) {
				return false;
			}
		}
		return source.position.distance(position) < shield ? true : source.position.distance(position) < 6;
	}
	
	@Override
	public void draw() {
		super.draw();
		
		StdDraw.picture(position.x, position.y, "Enemy.png", 10, 10, -rotation);		
	}
	
	public Physicsable findTarget() {		
		if(Flatscape.gameState != GameState.START_MENU) return Flatscape.player;
		if(Flatscape.enemies.isEmpty()) return null;
		Enemy _target = Flatscape.enemies.get(0);
		if(_target == this) {
			if(Flatscape.enemies.toArray().length > 1) {
				return Flatscape.enemies.get(1);
			} else {
				return null;
			}
		}
		return  _target;
	}

	@Override
	public void onHit(Physicsable source) {
		if(hitBy.contains(source)) return;
		hitBy.add(source);
		
		if(shield > 0) {
			shield = 0;
			shieldCooldown = SHIELD_COOLDOWN;
			return;
		}
		
		health -= source.damage;
		if(health <= 0) {
			FMath.playSound("Meteor_Destroy0");
			remove();
		}
	}
	
	@Override
	public void physics(double scale) {
		super.physics(scale);
		
		if(!Flatscape.enemies.contains(target) || target == null) {
			target = findTarget();
		}
		if(target == null) {
			acceleration = new Point(0, 0);
			return;
		}	
		rotation = Math.toDegrees(Math.atan((target.position.y - position.y) / (target.position.x - position.x))) - 90;
		if (target.position.x < position.x) rotation -= 180;
		rotation *= -1;
		
		if(Double.isNaN(rotation)) rotation = 0;
		
		acceleration.x = acceleration.y = 0;
		Point point = FMath.smallerHypot(target.position.x - position.x, target.position.y - position.y, Flatscape.SHIP_SPEED / 2);		
		if(position.distance(target.position) >= 75) {
			acceleration.add(point);
		} else if(position.distance(target.position) <= 50) {
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
		
		if(shieldCooldown > 0) shieldCooldown -= scale * 10;
		if(shieldCooldown <= 0) {
			shield = 10;
		}
	}
	
	public static Weapon[] randomWeapon() {
		Weapon[] weapons = new Weapon[1];
		weapons[0] = Math.random() > .5  && Flatscape.level != 1 ? new WeaponRocket(800) : new WeaponBullet(600);
		return weapons;
	}
	
	public String toString() {
		return "Enemy Ship";
	}

}