import static org.lwjgl.opengl.GL11.*;
import java.util.ArrayList;

public class Map0 extends Map {
	public Map0(){
		level = 0;
	}

	public void draw(){
		ResMgr.tiles[0].bind();
		glPushMatrix();

		glColor4f(1.f,1.f,1.f,1.f);
		for(int iy = 0; iy < h; ++iy){
			for(int ix = 0; ix < w; ++ix){
				char tile = data[ix+iy*w];
				switch(tile){
					case TILE_FLOOR:
						drawFloor(); break;
					case TILE_WALL:
						drawWall(); break;
					case TILE_LAMP:
						drawLamp(); break;
					case TILE_PORTAL:
						drawPortal(); break;
					default:
				}
				glTranslatef(1,0,0);
			}
			glTranslatef(-w,0,1);
		}
		glPopMatrix();
	}
}
