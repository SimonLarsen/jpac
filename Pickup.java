public abstract class Pickup implements Defines {
	public float x,z;

	Pickup(float x, float z){
		this.x = x;
		this.z = z;
	}

	abstract void draw(float dirdeg);

}
