import java.awt.Color;
import static org.lwjgl.opengl.GL11.*;

public class FadeEffect extends ScreenEffect implements Defines {
	private int color;
	private int direction;
	private float frame,time;

	public FadeEffect(int color, int direction, float time){
		this.alive = true;
		this.color = color;
		this.direction = direction;
		this.time = time;
		this.frame = 0.f;
	}

	public void update(float dt){
		frame += dt;
		if(frame >= time){
			frame = time;
			alive = false;
		}
	}

	public void draw(){
		float alpha = frame/time;
		if(direction == FADE_UP){
			alpha = 1.f-alpha;
		}

		switch(color){
			case FADE_BLACK:
				glColor4f(0.f,0.f,0.f,alpha);
				break;
			case FADE_WHITE:
				glColor4f(1.f,1.f,1.f,alpha);
				break;
		}
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
			glVertex2f(0,0);
			glVertex2f(WIDTH,0);
			glVertex2f(WIDTH,HEIGHT);
			glVertex2f(0,HEIGHT);
		glEnd();
		glEnable(GL_TEXTURE_2D);
	}

	public static final int FADE_BLACK	= 0;
	public static final int FADE_WHITE	= 1;

	public static final int FADE_UP		= 0;
	public static final int FADE_DOWN	= 1;
}
