
public class Player extends Physicsable implements Drawable{
	private Weapon[] weapons = new Weapon[4];
	
	public Player(Point position) {
		this.acceleration = new Point(0, 0);
		this.position = position;
		this.velocity = new Point(0, 0);
		this.rotation = 0;
		this.health = 100;
		this.damage = 100;
		
		weapons[0] = new WeaponBullet(300);
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
	
	public void draw() {
		super.draw();
		
		//Draw player ship
		StdDraw.picture(position.x, position.y, "Player.png", 10, 10, -rotation);
		
		//Drawing targeting reticule
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.circle(StdDraw.mouseX(), StdDraw.mouseY(), 5);
	}
	
	public Weapon[] getWeapons() {		
		return weapons;
	}
	
	public void nextLevel() {
		position.x = 0;
		position.y = 0;
		velocity.x = 0;
		velocity.y = 0;
		acceleration.x = 0;
		acceleration.y = 0;
		
		health = 100;
		
		Flatscape.drawables.add(this);
		Flatscape.physics.add(this);
		Flatscape.cameraTarget = this;
		System.out.println(Flatscape.level);
		switch(Flatscape.level) {
			case 2:
				weapons[0] = new WeaponBullet(150);
				break;
			case 3:
				weapons[1] = new WeaponRocket(750);
				break;
			case 4:
				hasShield = true;
				shield = 10;
				break;
			case 5:
				break;
		}
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
		
		if(health <= 0) Flatscape.startGameOver(source);
	}
	
	public void physics(double scale) {
		super.physics(scale);
		
		//Figure out which direction the player is pointing, in degrees
		rotation = Math.toDegrees(Math.atan((StdDraw.mouseY() - position.y)/(StdDraw.mouseX() - position.x))) - 90;
		if (StdDraw.mouseX() < position.x) rotation -= 180;
		rotation *= -1;
		
		if(Double.isNaN(rotation)) rotation = 0;
		
		if(Flatscape.gameState != GameState.GAME) return;
		for(Weapon weapon : weapons) {
			if(weapon == null) continue;
			weapon.physics(scale);
			if(weapon.cooldown <= 0 && StdDraw.mousePressed()) {
				weapon.shoot(this);
			}
		}
		
		if(shieldCooldown > 0) shieldCooldown -= scale * 10;
		if(shieldCooldown <= 0) {
			shield = 10;
		}
	}
	
	public void remove() {}
	
	public String toString() {
		return "The player";
	}
}
