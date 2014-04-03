import java.util.ArrayList;


public class ProjectileRocket extends Projectile {

	public Physicsable target;
	
	public ProjectileRocket(Point position, Point velocity, double damage, WeaponRocket source) {
		this.acceleration = new Point(0, 0);
		this.position = position;
		this.velocity = velocity;
		this.damage = damage;
		this.source = source;
		this.rotation = source.owner.rotation;
		
		Flatscape.drawables.add(this);
		Flatscape.physicsAddition.put(this, true);
		
		findTarget();
		
		if(target == null) return;
		this.velocity = FMath.smallerHypot(position, target.position, velocity.distance(new Point(0, 0)));
	}
	
	@Override
	public boolean detectHit(Physicsable source) {
		if(source == this.source.owner) return false;
		return source.position.distance(position) <= 1.5;
	}
	
	public void draw() {
		StdDraw.setPenColor(StdDraw.GREEN);
		StdDraw.filledCircle(position.x, position.y, 1.5);
	}
	
	public void findTarget() {
		if(Flatscape.physics.isEmpty()) return;
		ArrayList<Physicsable> targets = new ArrayList<Physicsable>();
		for(int i = 45; i <= 360; i += 45) {
			for(Physicsable _target : Flatscape.physics) {
				if(_target instanceof Projectile) continue;
				if(_target == source.owner) continue;
				
				double _rotation = Math.toDegrees(Math.atan((_target.position.y - position.y)/(_target.position.x - position.x))) - 90;
				if (_target.position.x < position.x) _rotation -= 180;
				_rotation *= -1;				
				if(Double.isNaN(_rotation)) _rotation = 0;
				
				if(Math.abs(rotation - _rotation) <= i) {
					targets.add(_target);
				}
			}			
			if(!targets.isEmpty()) break;			
		}
		target = null;
		if(!targets.isEmpty()) {
			for(Physicsable _target : targets) {
				if(target == null) {
					target = _target;
				} else {
					if(position.distance(_target.position) < position.distance(target.position)) {
						target = _target;
					}
				}
			}
		}
	}
	
	public void physics(double scale) {
		super.physics(scale);		
		if (Math.abs(position.x) >= 507.5 || Math.abs(position.y) >= 507.5) { 
			Flatscape.drawables.remove(this);
			Flatscape.physicsAddition.put(this, false);
			return;
		}
		if(!Flatscape.drawables.contains(this)) {
			return;			
		}
		if(!Flatscape.physics.contains(target)) findTarget();
		if(target == null) return;
		acceleration = FMath.smallerHypot(target.position.x - position.x, target.position.y - position.y, WeaponRocket.ROCKET_SPEED / 12750);
		
		if(velocity.distance(new Point(0, 0)) >= WeaponRocket.ROCKET_SPEED / 15) {
			acceleration.add(velocity.clone().divide(-4));
		}
	}
	
}
