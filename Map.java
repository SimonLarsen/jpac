import static org.lwjgl.opengl.GL11.*;

public class Map implements Defines {
	public int w,h,numDots,numBigDots;
	float startx,startz;
	char[] data;

	public Map(){
		this.w = 7;
		this.h = 7;
		this.numDots = 0;
		this.numBigDots = 0;

		this.startx = 3.5f;
		this.startz = 3.5f;

		data = new char[w*h];
		for(int i = 0; i < w; ++i){
			data[0+i*w] = 1;
			data[6+i*w] = 1;
			data[i+0*w] = 1;
			data[i+6*w] = 1;
		}
		data[0+3*w] = 0;
		data[6+3*w] = 0;
	}

	public void drawWalls(){
		glPushMatrix();

		glColor4f(1.f,1.f,1.f,1.f);
		for(int iy = 0; iy < h; ++iy){
			for(int ix = 0; ix < w; ++ix){
				char tile = data[ix+iy*w];
				switch(tile){
					case 0: // Floor
						glBegin(GL_QUADS);
							glTexCoord2f(0.25f,0.f); 		glVertex3f(0.f,0.f,0.f);
							glTexCoord2f(0.5f,0.f); 		glVertex3f(1.f,0.f,0.f);
							glTexCoord2f(0.5f,0.25f); 		glVertex3f(1.f,0.f,1.f);
							glTexCoord2f(0.25f,0.25f); 		glVertex3f(0.f,0.f,1.f);
						glEnd();
						break;
					case 1: // Wall
						glBegin(GL_QUADS);
							glTexCoord2f(0.f,0.f); 			glVertex3f(1.f,1.f,0.f);
							glTexCoord2f(0.25f,0.f); 		glVertex3f(0.f,1.f,0.f);
							glTexCoord2f(0.25f,0.25f); 		glVertex3f(0.f,0.f,0.f);
							glTexCoord2f(0.f,0.25f); 		glVertex3f(1.f,0.f,0.f);

							glTexCoord2f(0.f,0.f); 			glVertex3f(0.f,1.f,0.f);
							glTexCoord2f(0.25f,0.f); 		glVertex3f(0.f,1.f,1.f);
							glTexCoord2f(0.25f,0.25f); 		glVertex3f(0.f,0.f,1.f);
							glTexCoord2f(0.f,0.25f); 		glVertex3f(0.f,0.f,0.f);

							glTexCoord2f(0.f,0.f); 			glVertex3f(1.f,1.f,1.f);
							glTexCoord2f(0.25f,0.f); 		glVertex3f(1.f,1.f,0.f);
							glTexCoord2f(0.25f,0.25f); 		glVertex3f(1.f,0.f,0.f);
							glTexCoord2f(0.f,0.25f); 		glVertex3f(1.f,0.f,1.f);

							glTexCoord2f(0.f,0.f); 			glVertex3f(0.f,1.f,1.f);
							glTexCoord2f(0.25f,0.f); 		glVertex3f(1.f,1.f,1.f);
							glTexCoord2f(0.25f,0.25f); 		glVertex3f(1.f,0.f,1.f);
							glTexCoord2f(0.f,0.25f); 		glVertex3f(0.f,0.f,1.f);
						glEnd();
						break;
				}
				glTranslatef(1,0,0);
			}
			glTranslatef(-w,0,1);
		}
		glPopMatrix();
	}

	public boolean canMove(float x, float y){
		return canMove((int)x,(int)y);
	}
	public boolean canMove(int x, int y){
		if(data[x+y*w] != 1)
			return true;
		else
			return false;
	}

	public boolean canMove(int i){
		if(data[i] != 1)
			return true;
		else
			return false;
	}
}
