import static org.lwjgl.opengl.GL11.*;

public class SmallDot extends Pickup {

	public SmallDot(float x, float z){
		super(x,z);
	}

	public void draw(float dirdeg){
		glPushMatrix();
		glTranslatef(x,0,z);
		glRotatef(-dirdeg,0.f,1.f,0.f);

		glBegin(GL_QUADS);
			glTexCoord2f(7.f/8.f,0.f); 			glVertex3f(-0.125f,0.25f,0.f);
			glTexCoord2f(15.f/16.f,0f); 		glVertex3f(0.125f,0.25f,0.f);
			glTexCoord2f(15.f/16.f,1.f/16.f); 	glVertex3f(0.125f,0.f,0.f);
			glTexCoord2f(7.f/8.f,1.f/16.f); 	glVertex3f(-0.125f,0.f,0.f);
		glEnd();

		glPopMatrix();
	}
}
