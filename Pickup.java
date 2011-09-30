import static org.lwjgl.opengl.GL11.*;

public class Pickup implements Defines {
	public float x,z;
	public int type;

	Pickup(float x, float z, int type){
		this.x = x;
		this.z = z;
		this.type = type;
	}

	public void draw(float dirdeg){
		glPushMatrix();
		glTranslatef(x,0,z);
		glRotatef(-dirdeg,0.f,1.f,0.f);
		switch(type){
			case PICKUP_SMALL:
				glBegin(GL_QUADS);
					glTexCoord2f(7.f/8.f,0.f); 			glVertex3f(-0.125f,0.25f,0.f);
					glTexCoord2f(15.f/16.f,0f); 		glVertex3f(0.125f,0.25f,0.f);
					glTexCoord2f(15.f/16.f,1.f/16.f); 	glVertex3f(0.125f,0.f,0.f);
					glTexCoord2f(7.f/8.f,1.f/16.f); 	glVertex3f(-0.125f,0.f,0.f);
				glEnd();
				break;
			case PICKUP_BIG:
				glBegin(GL_QUADS);
					glTexCoord2f(6.f/8.f,0.f); 			glVertex3f(-0.25f,0.5f,0.f);
					glTexCoord2f(7.f/8.f,0.f); 			glVertex3f(0.25f,0.5f,0.f);
					glTexCoord2f(7.f/8.f,1.f/8.f); 		glVertex3f(0.25f,0.f,0.f);
					glTexCoord2f(6.f/8.f,1.f/8.f); 		glVertex3f(-0.25f,0.f,0.f);
				glEnd();
				break;
		}
		glPopMatrix();
	}

	public static final int PICKUP_SMALL 	= 0;
	public static final int PICKUP_BIG 		= 1;
}
