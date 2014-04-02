
public abstract class Enemy extends Physicsable implements Drawable {	
	public void remove() {
		Flatscape.enemyAddition.put(this, false);
	}
}
