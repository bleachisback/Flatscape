
public class WeaponBullet extends Weapon {
	
	public static final double BULLET_SPEED = 1;
	public static final double DAMAGE = 15;
	
	public WeaponBullet(int baseCooldown) {
		super(baseCooldown);
	}
	
	@Override
	public void shoot(Physicsable shooter) {		
		super.shoot(shooter);
		FMath.playSound("Laser_Shoot0");
		new ProjectileBullet(shooter.position.clone(), FMath.circlePoint(BULLET_SPEED / 10, shooter.rotation), DAMAGE, this);
	}

}