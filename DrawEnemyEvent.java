import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.*;

public class DrawEnemyEvent extends Event {
	private float fromx, fromz, dist, moved;
	private int dir;

	public DrawEnemyEvent(float x, float z, float w, float h, float fromx, float fromz, int dir, float dist){
		super(x,z,w,h);

		this.fromx = fromx;
		this.fromz = fromz;
		this.dir = dir;
		this.dist = dist;
		moved = 0.f;
	}

	public void update(float dt, Player pl, Map map, ArrayList<Ghost> ghosts){
		if(status == STATUS_ACTIVE){
			float toMove = dt*Ghost.MOVESPEED;
			moved += toMove;

			switch(dir){
				case 0: z -= toMove; break;
				case 1: x += toMove; break;
				case 2: z += toMove; break;
				case 3: x -= toMove; break;
			}

			if(moved > dist){
				status = STATUS_DEAD;
			}
		}
		else if(status == STATUS_IDLE){
			if(collide(pl)){
				status = STATUS_ACTIVE;
				x = fromx;
				z = fromz;
			}
		}
	}

	public void draw(float dirdeg){
		if(status == STATUS_ACTIVE){
			glPushMatrix();
			glTranslatef(x+0.5f,0,z+0.5f);
			glRotatef(-dirdeg,0,1,0);
			glBegin(GL_QUADS);
				glTexCoord2f(0.125f, 0.f); 		glVertex3f(-0.45f,0.9f,0.f);
				glTexCoord2f(0.25f, 0.f); 		glVertex3f(0.45f,0.9f,0.f);
				glTexCoord2f(0.25f, 1.f/8.f); 	glVertex3f(0.45f,0.f,0.f);
				glTexCoord2f(0.125f, 1.f/8.f); 	glVertex3f(-0.45f,0.f,0.f);
			glEnd();
			glPopMatrix();
		}
	}
}
