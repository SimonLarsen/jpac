import static org.lwjgl.opengl.GL11.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;

public class Map implements Defines {
	public int w,h,numDots,numBigDots;
	public float startx,startz;
	public char[] data;
	private float lampTime,particleTime;
	private boolean lampOn;

	public Map(){

	}

	public void update(float dt){
		if(lampTime < 0){
			if(lampOn){
				lampTime = (float)(Math.random()%0.1f + 0.05f);
			}
			else{
				lampTime = (float)(Math.random()%1.0f + 0.1f);
			}
			lampOn = !lampOn;
		}
		lampTime -= dt;

		particleTime = (particleTime+dt)%1.f;
	}

	public void loadFromImage(String filename, ArrayList<Pickup> dots, ArrayList<Ghost> ghosts) throws Exception {
		File file = new File(filename);
		BufferedImage img = ImageIO.read(file);

		w = img.getWidth();
		h = img.getHeight();
		data = new char[w*h];
		int ghostnum = 0;

		for(int iy = 0; iy < h; ++iy){
			for(int ix = 0; ix < w; ++ix){
				switch(img.getRGB(ix,iy)){
					case COL_WALL:
						data[ix+iy*w] = 1;
						break;
					case COL_START:
						startx = ix+0.5f; startz = iy+0.5f;
						break;
					case COL_LAMP:
						data[ix+iy*w] = 2;
						dots.add(new SmallDot(ix+0.5f,iy+0.5f));
						break;
					case COL_PORTAL:
						data[ix+iy*w] = 3;
						break;
					case COL_SMALL:
						dots.add(new SmallDot(ix+0.5f,iy+0.5f));
						break;
					case COL_BIG:
						dots.add(new BigDot(ix+0.5f,iy+0.5f));
						break;
					case COL_GHOST:
						ghosts.add(new Ghost(ix,iy,ghostnum));
						ghostnum = (ghostnum+1)%4;
						break;
					default:
				}
			}
		}
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

	public void drawWalls(){
		glPushMatrix();

		glColor4f(1.f,1.f,1.f,1.f);
		for(int iy = 0; iy < h; ++iy){
			for(int ix = 0; ix < w; ++ix){
				char tile = data[ix+iy*w];
				switch(tile){
					case TILE_FLOOR:
						glBegin(GL_QUADS);
							glTexCoord2f(0.25f,0.f); 		glVertex3f(0.f,0.f,0.f);
							glTexCoord2f(0.5f,0.f); 		glVertex3f(1.f,0.f,0.f);
							glTexCoord2f(0.5f,0.25f); 		glVertex3f(1.f,0.f,1.f);
							glTexCoord2f(0.25f,0.25f); 		glVertex3f(0.f,0.f,1.f);

							glTexCoord2f(0.5f,0.f); 		glVertex3f(0.f,1.f,0.f);
							glTexCoord2f(0.75f,0.f); 		glVertex3f(1.f,1.f,0.f);
							glTexCoord2f(0.75f,0.25f); 		glVertex3f(1.f,1.f,1.f);
							glTexCoord2f(0.5f,0.25f); 		glVertex3f(0.f,1.f,1.f);
						glEnd();
						break;
					case TILE_WALL:
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
					case TILE_LAMP:
						if(lampOn){
							glBegin(GL_QUADS);
								glTexCoord2f(0.5f,0.25f); 		glVertex3f(0.f,1.f,0.f);
								glTexCoord2f(0.75f,0.25f); 		glVertex3f(1.f,1.f,0.f);
								glTexCoord2f(0.75f,0.5f); 		glVertex3f(1.f,1.f,1.f);
								glTexCoord2f(0.5f,0.5f); 		glVertex3f(0.f,1.f,1.f);
								glTexCoord2f(0.25f,0.25f); 		glVertex3f(0.f,0.f,0.f);
								glTexCoord2f(0.5f,0.25f); 		glVertex3f(1.f,0.f,0.f);
								glTexCoord2f(0.5f,0.5f); 		glVertex3f(1.f,0.f,1.f);
								glTexCoord2f(0.25f,0.5f); 		glVertex3f(0.f,0.f,1.f);
							glEnd();
						}
						else{
							glBegin(GL_QUADS);
								glTexCoord2f(0.75f,0.25f); 		glVertex3f(0.f,1.f,0.f);
								glTexCoord2f(1.f,0.25f); 		glVertex3f(1.f,1.f,0.f);
								glTexCoord2f(1.f,0.5f); 		glVertex3f(1.f,1.f,1.f);
								glTexCoord2f(0.75f,0.5f); 		glVertex3f(0.f,1.f,1.f);
								glTexCoord2f(0.25f,0.f); 		glVertex3f(0.f,0.f,0.f);
								glTexCoord2f(0.5f,0.f); 		glVertex3f(1.f,0.f,0.f);
								glTexCoord2f(0.5f,0.25f); 		glVertex3f(1.f,0.f,1.f);
								glTexCoord2f(0.25f,0.25f); 		glVertex3f(0.f,0.f,1.f);
							glEnd();
						}
						break;
					case TILE_PORTAL:
						glPushMatrix();
							glTranslatef(0.f,-particleTime,0.f);
							drawPortalParticles();
							glTranslatef(0.f,1.f,0.f);
							drawPortalParticles();
						glPopMatrix();
						glBegin(GL_QUADS);
							glTexCoord2f(0.f,0.25f); 		glVertex3f(0.f,0.f,0.f);
							glTexCoord2f(0.25f,0.25f); 		glVertex3f(1.f,0.f,0.f);
							glTexCoord2f(0.25f,0.5f); 		glVertex3f(1.f,0.f,1.f);
							glTexCoord2f(0.f,0.5f); 		glVertex3f(0.f,0.f,1.f);
							glTexCoord2f(0.f,0.25f); 		glVertex3f(0.f,1.f,0.f);
							glTexCoord2f(0.25f,0.25f); 		glVertex3f(1.f,1.f,0.f);
							glTexCoord2f(0.25f,0.5f); 		glVertex3f(1.f,1.f,1.f);
							glTexCoord2f(0.f,0.5f); 		glVertex3f(0.f,1.f,1.f);
						glEnd();
						break;
					default:
				}
				glTranslatef(1,0,0);
			}
			glTranslatef(-w,0,1);
		}
		glPopMatrix();
	}

	private void drawPortalParticles(){
		glBegin(GL_QUADS);
			glTexCoord2f(0.75f,0.f); 		glVertex3f(0.2f,1.f,0.2f);
			glTexCoord2f(1.f,0.f); 			glVertex3f(0.2f,1.f,0.8f);
			glTexCoord2f(1.f,0.25f); 		glVertex3f(0.2f,0.f,0.8f);
			glTexCoord2f(0.75f,0.25f); 		glVertex3f(0.2f,0.f,0.2f);

			glTexCoord2f(1.f,0.f); 			glVertex3f(0.4f,1.f,0.f);
			glTexCoord2f(0.75f,0.f); 		glVertex3f(0.4f,1.f,1.f);
			glTexCoord2f(0.75f,0.25f); 		glVertex3f(0.4f,0.f,1.f);
			glTexCoord2f(1.f,0.25f); 		glVertex3f(0.4f,0.f,0.f);

			glTexCoord2f(0.75f,0.f); 		glVertex3f(0.6f,1.f,0.f);
			glTexCoord2f(1.f,0.f); 			glVertex3f(0.6f,1.f,1.f);
			glTexCoord2f(1.f,0.25f); 		glVertex3f(0.6f,0.f,1.f);
			glTexCoord2f(0.75f,0.25f); 		glVertex3f(0.6f,0.f,0.f);

			glTexCoord2f(1.f,0.f); 			glVertex3f(0.8f,1.f,0.8f);
			glTexCoord2f(0.75f,0.f); 		glVertex3f(0.8f,1.f,0.2f);
			glTexCoord2f(0.75f,0.25f); 		glVertex3f(0.8f,0.f,0.2f);
			glTexCoord2f(1.f,0.25f); 		glVertex3f(0.8f,0.f,0.8f);
		glEnd();
	}

	// Image color values
	private static final int COL_FLOOR 	= 255 << 24 | 255 << 16 | 255 << 8 | 255;
	private static final int COL_WALL 	= 255 << 24 | 255;
	private static final int COL_START	= 255 << 24 | 255 << 16;
	private static final int COL_LAMP  	= 255 << 24 | 255 << 16 | 255 << 8;
	private static final int COL_PORTAL	= 255 << 24 | 255 << 16 | 255;
	private static final int COL_SMALL  = 255 << 24 | 255 << 16 | 255 << 8 | 255;
	private static final int COL_BIG 	= 255 << 24 | 255 << 8;
	private static final int COL_GHOST 	= 255 << 24 | 255 << 8 | 255;

	// Tile data values
	public static final int TILE_FLOOR 	= 0;
	public static final int TILE_WALL 		= 1;
	public static final int TILE_LAMP	 	= 2;
	public static final int TILE_PORTAL 	= 3;
}
