import java.util.ArrayList;

public abstract class Event {
	protected float x,z,w,h;
	public int status;

	public Event(float x, float z, float w, float h){
		this.x = x;
		this.z = z;
		this.w = w;
		this.h = h;
		this.status = Event.STATUS_IDLE;
	}
	
	protected boolean collide(Player pl){
		if(pl.x > x && pl.x < x+w && pl.z > z && pl.z < z+h){
			return true;
		}
		else{
			return false;
		}
	}

	public abstract void update(float dt, Player pl, Map map, ArrayList<Ghost> ghosts);

	public abstract void draw();

	public static final int STATUS_IDLE 	= 0;
	public static final int STATUS_ACTIVE 	= 1;
	public static final int STATUS_DEAD 	= 2;
}
