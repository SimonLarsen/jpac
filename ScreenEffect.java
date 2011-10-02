public abstract class ScreenEffect implements Defines {
	public boolean alive;	

	abstract void update(float dt);

	// Note: All ScreenEffects expect to be in Ortho2D mode when drawn
	abstract void draw();
}
