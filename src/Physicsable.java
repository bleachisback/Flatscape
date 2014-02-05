
public class Physicsable {
	public Point acceleration;
	public Point position;
	public Point velocity;
	public void physics(double scale) {
		velocity.x += acceleration.x * scale;
		velocity.y += acceleration.y * scale;
		position.x += velocity.x * scale;
		position.y += velocity.y * scale;
	}
}
