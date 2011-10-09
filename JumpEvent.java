import java.util.ArrayList;

public class JumpEvent extends Event {
	private float offsetx, offsetz;

	public JumpEvent(float x, float z, float w, float h, float offsetx, float offsetz){
		super(x,z,w,h);
		this.offsetx = offsetx;
		this.offsetz = offsetz;
	}

	public void update(float dt, Player pl, Map map, ArrayList<Ghost> ghosts){
		if(collide(pl)){
			pl.x += offsetx;
			pl.z += offsetz;
		}
	}

	public void draw(float dirdeg){

	}
}
