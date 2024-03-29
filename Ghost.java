import static org.lwjgl.opengl.GL11.*;
public class Ghost implements Defines {
	public float x,z,scaredTime;
	public float color;
	public boolean alive;

	private float startx,startz,moved;
	private int dir;

	public Ghost(float x, float z, int color){
		startx = x+0.5f;
		startz = z+0.5f;
		this.color = color*(1.f/8.f); // IS offset on tilemap, not just number
		respawn();
	}

	public void respawn(){
		x = startx;
		z = startz;
		alive = true;
		scaredTime = 0.f;
		moved = 1.f;
		dir = 5;
	}

	public void update(float dt, Map map){
		boolean newDir = false;
		float toMove;

		if(scaredTime > 0.f){
			scaredTime -= dt;
			toMove = dt*SCAREDSPEED;
		}
		else{
			toMove = dt*MOVESPEED;
		}

		if(moved + toMove > 1.0f){
			toMove = moved + toMove - 1.0f;
			moved = 0.f;
			newDir = true;
		}
		moved += toMove;

		switch(dir){
			case 0: z -= toMove; break;
			case 1: x += toMove; break;
			case 2: z += toMove; break;
			case 3: x -= toMove; break;
		}

		if(newDir){
			x = (float)Math.floor(x)+0.5f;
			z = (float)Math.floor(z)+0.5f;

			int badDir;
			if(map.canMoveGhost(xmask((int)x,dir),zmask((int)z,dir))){
				badDir = (dir+2)%4;
			}
			else{
				badDir = dir;
			}

			//dir = rand() % 4;
			dir = (int)(Math.random() * Integer.MAX_VALUE) % 4;
			for(int i = 0; i < 4; ++i) {
				if(dir != badDir && map.canMoveGhost(xmask((int)x,dir),zmask((int)z,dir))){
					return;
				}
				dir = (dir+1)%4;
			}
		}
	}

	public void setScared(){
		scaredTime = SCAREDTIME;
	}

	public void draw(float dirdeg){
		glPushMatrix();
		glTranslatef(x,0,z);
		glRotatef(-dirdeg,0,1,0);

		boolean drawScared = false;
		if(scaredTime > 0.f){
			if(scaredTime > 3.f || (int)(scaredTime*10.f)%2 == 0){
				drawScared = true;
			}
		}

		if(drawScared){
			glBegin(GL_QUADS);
				glTexCoord2f(4.f/8.f, 0.f); 		glVertex3f(-0.45f,0.9f,0.f);
				glTexCoord2f(5.f/8.f, 0.f); 		glVertex3f(0.45f,0.9f,0.f);
				glTexCoord2f(5.f/8.f, 1.f/8.f);		glVertex3f(0.45f,0.f,0.f);
				glTexCoord2f(4.f/8.f, 1.f/8.f); 	glVertex3f(-0.45f,0.f,0.f);
			glEnd();
		}
		else{
			glBegin(GL_QUADS);
				glTexCoord2f(color, 0.f); 				glVertex3f(-0.45f,0.9f,0.f);
				glTexCoord2f(color+1.f/8.f, 0.f); 		glVertex3f(0.45f,0.9f,0.f);
				glTexCoord2f(color+1.f/8.f, 1.f/8.f); 	glVertex3f(0.45f,0.f,0.f);
				glTexCoord2f(color, 1.f/8.f); 			glVertex3f(-0.45f,0.f,0.f);
			glEnd();
		}
		glPopMatrix();
	}

	private int xmask(int nx, int ndir){
		if(ndir == 1)
			return nx+1;
		if(ndir == 3)
			return nx-1;
		return nx;
	}

	private int zmask(int nz, int ndir){
		if(ndir == 0)
			return nz-1;
		if(dir == 2)
			return nz+1;
		return nz;
	}

	public static final float MOVESPEED = 2.0f;
	public static final float SCAREDSPEED = 1.25f;
}
