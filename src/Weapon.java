
public class Weapon {
	public final int baseCooldown;
	public int cooldown;
	public Physicsable owner;
	
	public Weapon(int baseCooldown) {
		this.baseCooldown = baseCooldown;
		cooldown = baseCooldown;
	}
	
	public void physics(double scale) {
		if(cooldown <= 0) {
			cooldown = 0;
			return;
		}
		cooldown -= scale * 10;		
	}
	
	public void shoot(Physicsable shooter) {
		cooldown = baseCooldown + cooldown;
	}
}
