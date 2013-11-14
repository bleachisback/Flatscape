
public abstract class Enemy {
	public double[] hitbox;
	public double posX;
	public double posY;
	public double[] velocity;
	
	public abstract void move();
	public abstract void draw();
	
	public void remove() {
		Flatscape.removeEnemy(this);
	}
}
