import static org.lwjgl.opengl.GL11.*;

public class GhostDieParticle extends Particle {
	public GhostDieParticle(float x, float z){
		super(x,0,z);
	}

	public void update(float dt){
		if(frame < 128){
			frame += 8.f*dt;
		}
	}

	public void draw(float dirdeg){
		glPushMatrix();
		glTranslatef(x,y,z);
		glRotatef(-dirdeg,0,1,0);

		float tex1,tex2;
		if(frame < 7.f){
			tex1 = (float)Math.floor(frame)/8.f;
			tex2 = tex1 + 1/8.f;
		}
		else{
			tex1 = 7.f/8.f;
			tex2 = 1.f;
		}
		glBegin(GL_QUADS);	
			glTexCoord2f(tex1, 1.f/8.f); 	glVertex3f(-0.45f,0.9f,0.f);
			glTexCoord2f(tex2, 1.f/8.f); 	glVertex3f(0.45f,0.9f,0.f);
			glTexCoord2f(tex2, 2.f/8.f); 	glVertex3f(0.45f,0.f,0.f);
			glTexCoord2f(tex1, 2.f/8.f); 	glVertex3f(-0.45f,0.f,0.f);
		glEnd();

		glRotatef(dirdeg,0,1,0);
		if(frame >= 5.f){
			if(frame < 20.f){
				float scale = 1.f - (20.f-(int)frame)/22.f;
				glScalef(scale,1,scale);
			}
			glBegin(GL_QUADS);	
				glTexCoord2f(5.f/8.f,0.f); 		glVertex3f(-0.5f,0.01f,-0.5f);
				glTexCoord2f(6.f/8.f,0.f); 		glVertex3f(0.5f,0.01f,-0.5f);
				glTexCoord2f(6.f/8.f,1.f/8.f); 	glVertex3f(0.5f,0.01f,0.5f);
				glTexCoord2f(5.f/8.f,1.f/8.f); 	glVertex3f(-0.5f,0.01f,0.5f);
			glEnd();
			glPopMatrix();
		}
		
		glPopMatrix();
	}
}
