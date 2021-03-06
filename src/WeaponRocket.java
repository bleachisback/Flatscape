
public class WeaponRocket extends Weapon {
	
	public static final double ROCKET_SPEED = .75;
	public static final double DAMAGE = 8;
	
	public WeaponRocket(int baseCooldown) {
		super(baseCooldown);
	}
	
	@Override
	public void shoot(Physicsable shooter) {		
		super.shoot(shooter);
		//FMath.playSound("Laser_Shoot0");
		ProjectileRocket rocket = new ProjectileRocket(shooter.position.clone(), FMath.circlePoint(ROCKET_SPEED / 10, shooter.rotation), DAMAGE, this);
		if(rocket.target == null) rocket.remove();
	}

}
