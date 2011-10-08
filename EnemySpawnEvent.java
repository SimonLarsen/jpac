import java.util.ArrayList;

public class EnemySpawnEvent extends Event {
	private float spawnx, spawnz;

	public EnemySpawnEvent(float x, float z, float w, float h, float spawnx, float spawnz){
		super(x,z,w,h);
		this.spawnx = spawnx;
		this.spawnz = spawnz;
	}

	public void update(float dt, Player pl, Map map, ArrayList<Ghost> ghosts){
		if(collide(pl)){
			ghosts.add(new Ghost(spawnx,spawnz,0));
			status = STATUS_DEAD;
		}
	}

	public void draw(){
	}
}
