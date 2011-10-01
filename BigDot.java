import static org.lwjgl.opengl.GL11.*;

public class BigDot extends Pickup {

	public BigDot(float x, float z){
		super(x,z);
	}

	public void draw(float dirdeg){
		glPushMatrix();
		glTranslatef(x,0,z);
		glRotatef(-dirdeg,0.f,1.f,0.f);

		glBegin(GL_QUADS);
			glTexCoord2f(6.f/8.f,0.f); 			glVertex3f(-0.25f,0.5f,0.f);
			glTexCoord2f(7.f/8.f,0.f); 			glVertex3f(0.25f,0.5f,0.f);
			glTexCoord2f(7.f/8.f,1.f/8.f); 		glVertex3f(0.25f,0.f,0.f);
			glTexCoord2f(6.f/8.f,1.f/8.f); 		glVertex3f(-0.25f,0.f,0.f);
		glEnd();

		glPopMatrix();
	}
}
