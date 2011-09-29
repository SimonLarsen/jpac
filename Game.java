import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;
import org.lwjgl.Sys;
import java.nio.FloatBuffer;

public class Game implements Defines {
	private boolean running;
	private long lastUpdate;

	public static Player pl;
	public static Map map;

	/**
	 * Main game loop.
	 */
	private void loop(){
		running = true;

		map = new Map();
		pl = new Player();
		pl.x = map.startx;
		pl.z = map.startz;

		Mouse.setCursorPosition(WIDTH/2,HEIGHT/2);
		while(running){
			float dt = getDelta();
			if(Display.isCloseRequested()){
				running = false;
			}

			pl.update(dt,map);

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			draw();

			Display.update();
		}
	}

	private void draw(){
		glLoadIdentity();

		glRotatef(pl.ydirdeg,1,0,0);
		glRotatef(pl.xdirdeg,0,1,0);

		glTranslatef(-pl.x,-pl.y,-pl.z);
		// TODO Draw shadow
		
		map.drawWalls();

		// TODO Draw dots
		// TODO Draw ghosts
		// TODO Draw particles
	}

	/**
	 * Gets the number of seconds passed since last call.
	 *
	 * @return Delta time in as a floating point value
	 */
	private float getDelta(){
		long time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
		float delta = (float)(time - lastUpdate)/1000.f;
		lastUpdate = time;
		return delta;
	}

	/**
	 * Sets up display and initializes OpenGL settings
	 *
	 * @throws LWJGLException if display couldn't be created.
	 */
	private void init() throws Exception {
		Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
		Display.create();
		// Setup view matrix
		glViewport(0,0,WIDTH,HEIGHT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(80.f,(float)WIDTH/(float)HEIGHT,0.01f,100.f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		// Background color
		glClearColor(0.f,0.f,0.f,0.f);
		// Depth buffer/testing
		glClearDepth(1.f);
		glDepthFunc(GL_LEQUAL);
		glEnable(GL_DEPTH_TEST);
		// Enable textures
		glEnable(GL_TEXTURE_2D);
		// Setup blending (only used for screen effect as of now)
		glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_BLEND);
		// Use alpha test, for billboard sprites with transparency
		glAlphaFunc(GL_GREATER,0.9f);
		glEnable(GL_ALPHA_TEST);
		// Setup fog
		FloatBuffer fogColor = org.lwjgl.BufferUtils.createFloatBuffer(4).put(new float[]
			{0.f,0.f,0.f,1.f}); fogColor.flip();
		glFogi(GL_FOG_MODE, GL_EXP2);
		glFog(GL_FOG_COLOR, fogColor);
		glFogf(GL_FOG_DENSITY, 0.65f);
		glHint(GL_FOG_HINT, GL_FASTEST);
		glFogf(GL_FOG_START, 0.5f);
		glFogf(GL_FOG_END, 6.0f);
		glEnable(GL_FOG);
		Mouse.setGrabbed(true);
	}

	/**
	 * Entry point for starting a game.
	 * Makes sure display is set up,
	 * calls game loop and destroy display on exit.
	 *
	 * @throws Exception If init() fails
	 */
	public void start() throws Exception {
		// Setup screen and OpenGL stuff
		init();

		// Load resources
		ResMgr.loadTextures();

		// Start game loop
		loop();

		// Destroy display before returning
		Display.destroy();
	}

	public static void main (String[] args){
		Game game = new Game();
		try{
			game.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
