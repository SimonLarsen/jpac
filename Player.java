import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

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

	public void update(float dt, Map map){
		float nx = x;
		float nz = z;
		boolean moved = false;

		// Alive
		if(state == 0){
			// Check keyboard input
			if(Keyboard.isKeyDown(Keyboard.KEY_W)){
				nx += (float)(Math.sin(xdir)*WALKSPEED*dt);
				nz -= (float)(Math.cos(xdir)*WALKSPEED*dt);
				moved = true;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_S)){
				nx -= (float)(Math.sin(xdir)*WALKSPEED*dt);
				nz += (float)(Math.cos(xdir)*WALKSPEED*dt);
				moved = true;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_A)){
				nx -= (float)(Math.cos(xdir)*STRAFESPEED*dt);
				nz -= (float)(Math.sin(xdir)*STRAFESPEED*dt);
				moved = true;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_D)){
				nx += (float)(Math.cos(xdir)*STRAFESPEED*dt);
				nz += (float)(Math.sin(xdir)*STRAFESPEED*dt);
				moved = true;
			}
			
			// Check mouse movement
			xdir += (float)Mouse.getDX()*dt;
			xdirdeg = xdir * RADDEG;
			ydir -= (float)Mouse.getDY()*dt;
			if(ydir < -Math.PI/2.f){ ydir = (float)-Math.PI/2.f; }
			else if(ydir > Math.PI/2.f){ ydir = (float)Math.PI/2.f; }
			ydirdeg = ydir * RADDEG;

			if(moved){
				bop += 7.f*dt*WALKSPEED;
				if(bop > 2.f*Math.PI){
					// TODO Play step sound
					bop = (float)(bop % (2.f*Math.PI));
				}
				y = (float)(0.6+Math.sin(bop - 1.f)/35.f);

				// Check collision with walls
				// x axis
				if(map.canMove(nx-0.25f,z-0.25f) && map.canMove(nx+0.25f,z-0.25f) && map.canMove(nx-0.25f,z+0.25f) && map.canMove(nx+0.25f,z+0.25f)){
					x = nx;
				}
				// y axis
				if(map.canMove(x-0.25f,nz-0.25f) && map.canMove(x+0.25f,nz-0.25f) && map.canMove(x-0.25f,nz+0.25f) && map.canMove(x+0.25f,nz+0.25f)){
					z = nz;
				}
				// Wrap if through portal
				if(x < 0.3f){ x = map.w-0.5f; }
				else if(x > map.w-0.3f){ x = 0.5f; }
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

	public int collideDots(ArrayList<Pickup> dots){
		int ret = 0;
		for(int i = dots.size()-1; i >= 0; --i){
			float xdist = this.x - dots.get(i).x;
			float zdist = this.z - dots.get(i).z;
			float dist = xdist*xdist + zdist*zdist;
			if(dist < 0.1f){
				if(dots.get(i) instanceof BigDot){
					ret = 1;
					// TODO: Play bigdot sound
				}
				dots.remove(i);
			}
		}
		return ret;
	}

	public void collideGhosts(ArrayList<Ghost> ghosts){
		for(int i = 0; i < ghosts.size(); ++i){
			Ghost g = ghosts.get(i);
			float xdist = this.x - g.x;
			float zdist = this.z - g.z;
			float dist = xdist*xdist + zdist*zdist;
			if(dist < 0.25f){
				if(g.scaredTime > 0.f){
					g.alive = false;
					// TODO Play ghost kill sound
				}
				else{
					if(state != 1){
						// TODO Play player killed sound
						state = 1;
					}
				}
			}
		}
	}

	public void drawEffects(){

	}

	public static final float WALKSPEED = 2.0f;
	public static final float STRAFESPEED = 1.5f;
	public static final float TURNSPEED = 3.f;
}
