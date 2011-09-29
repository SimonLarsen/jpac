import org.lwjgl.input.Keyboard;

public class Player implements Defines {
	public float x,y,z;
	public float xdir,xdirdeg;
	public float ydir,ydirdeg;
	float ghostDist;
	int state; // 0 = alive, 1 = dead/dying
	float bop;

	public Player(){
		state = 0;
		x = 0.f;
		y = 0.6f;
		z = 0.f;
		xdir = xdirdeg = 0.f;
		ydir = ydirdeg = 0.f;
		bop = 0.f;
	}

	public void update(float dt){
		float nx = x;
		float nz = z;
		boolean moved = false;

		// Alive
		if(state == 0){
			if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
				nx += (float)(Math.sin(xdir)*WALKSPEED*dt);
				nz -= (float)(Math.cos(xdir)*WALKSPEED*dt);
				moved = true;
			}

			// TODO: Check all controls, inclusive mouse aim
			//
			if(moved){
				bop += 7.f*dt*WALKSPEED;
				if(bop > 2.f*Math.PI){
					// TODO Play step sound
					bop = (float)(bop % (2.f*Math.PI));
				}
				y = (float)(0.6+Math.sin(bop - 1.f)/35.f);

				// TODO Do collision checking
				x = nx;
				z = nz;
			}
		}
		// Dying/dead
		else if(state == 1){
			if(y > 0.2f){
				y -= 0.8f*dt;
			}
			if(ydir > -Math.PI/3.5f){
				ydir -= 2.5f*dt;
				ydirdeg = ydir * RADDEG;
			}
		}
	}

	public void collideGhosts(){

	}

	public void drawEffects(){

	}

	public static final float WALKSPEED = 2.0f;
	public static final float STRAFESPEED = 1.5f;
	public static final float TURNSPEED = 3.f;
}
