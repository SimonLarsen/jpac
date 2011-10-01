public abstract class Particle implements Defines {
	protected float x,y,z,frame;
	protected int type;
	public boolean alive;

	public Particle(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		frame = 0.f;
		alive = true;
	}

	abstract void update(float dt);

	abstract void draw(float dirdeg);
}
