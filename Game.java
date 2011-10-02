import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;
import org.lwjgl.Sys;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Game implements Defines {
	private boolean running,paused;
	private long lastUpdate;

	private static Player pl;
	private static Map map;
	private static ArrayList<Pickup> dots;
	private static ArrayList<Ghost> ghosts;
	private static ArrayList<Particle> particles;
	private static ArrayList<ScreenEffect> effects;

	/**
	 * Main game loop.
	 */
	private void loop() throws Exception {
		running = true;
		paused = false;

		dots = new ArrayList<Pickup>(32);
		ghosts = new ArrayList<Ghost>(4);
		particles = new ArrayList<Particle>();
		effects = new ArrayList<ScreenEffect>();
		effects.add(new FadeEffect(FadeEffect.FADE_BLACK,FadeEffect.FADE_UP,2.f));

		map = new Map();
		map.loadFromImage("res/levels/1.png",dots,ghosts);

		pl = new Player();
		pl.x = map.startx;
		pl.z = map.startz;

		getDelta(); // To calibrate

		while(running){
			// Get delta time. Call even when paused to calibrate.
			float dt = getDelta();
			// Close window eh?	
			if(Display.isCloseRequested()){
				running = false;
			}
			// Poll key events
			while(Keyboard.next()){
				if(Keyboard.getEventKeyState()){ // Key pressed
					int key = Keyboard.getEventKey();
					if(key == Keyboard.KEY_P || key == Keyboard.KEY_ESCAPE){
						paused = !paused;
					}
				}
			}
			// Don't update entities if paused
			if(!paused){
				// Update player
				pl.update(dt,map);
				if(pl.collideDots(dots) == 1){
					for(int i = 0; i < ghosts.size(); ++i){
						ghosts.get(i).setScared();
					}
				}
				pl.collideGhosts(ghosts);

				// Update map
				map.update(dt);

				// Update screen effects
				for(int i = effects.size()-1; i >= 0; --i){
					if(effects.get(i).alive){
						effects.get(i).update(dt);
					}
					else{
						System.out.println("EHG");
						effects.remove(i);
					}
				}

				// Update ghosts
				for(int i = 0; i < ghosts.size(); ++i){
					Ghost g = ghosts.get(i);
					if(g.alive){
						g.update(dt,map);
					}
					else{
						particles.add(new GhostDieParticle(g.x,g.z));
						g.respawn();
					}
				}
				// Update particles
				for(int i = particles.size()-1; i >= 0; --i){
					if(particles.get(i).alive){
						particles.get(i).update(dt);
					}
					else{
						particles.remove(i);
					}
				}
			} // if(!paused)

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			draw();
			draw2D();

			Display.update();
		}
	}

	/**
	 * Draw all 3D objects (walls, ghosts, dots...) to screen.
	 */
	private void draw(){
		glLoadIdentity();

		glRotatef(pl.ydirdeg,1,0,0);
		glRotatef(pl.xdirdeg,0,1,0);

		glColor4f(1.f,1.f,1.f,1.f);

		ResMgr.sprites.bind();
		glTranslatef(0,-pl.y,0);
		// Draw shadow
		glBegin(GL_QUADS);
			glTexCoord2f(0.f,6.f/8.f); 			glVertex3f(-0.25f,0.02f,-0.25f);
			glTexCoord2f(2.f/8.f, 6.f/8.f); 	glVertex3f(0.25f,0.02f,-0.25f);
			glTexCoord2f(2.f/8.f, 8.f/8.f); 	glVertex3f(0.25f,0.02f,0.25f);
			glTexCoord2f(0.f, 8.f/8.f); 		glVertex3f(-0.25f,0.02f,0.25f);
		glEnd();
		glTranslatef(-pl.x,0,-pl.z);
		
		ResMgr.tiles.bind();
		map.draw();

		ResMgr.sprites.bind();

		for(Pickup p : dots){
			p.draw(pl.xdirdeg);
		}
		for(Ghost g : ghosts){
			g.draw(pl.xdirdeg);
		}
		for(Particle p : particles){
			p.draw(pl.xdirdeg);
		}
	}

	/**
	 * Draw all 2D stuff to screen
	 */
	private void draw2D(){
		pushOrtho();
		if(paused){
			ResMgr.font.drawString(100,Defines.HEIGHT-100,"PAUSED");
		}
		for(ScreenEffect se : effects){
			se.draw();
		}
		popOrtho();
	}

	/**
	 * Push current model view matrix to stack and switch
	 * to orthogonal matrix for 2D drawing
	 */
	private void  pushOrtho(){
		glEnable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);
		glDisable(GL_DEPTH_TEST);

		glPushMatrix();
		glLoadIdentity();
			glMatrixMode(GL_PROJECTION);
			glPushMatrix();
			glLoadIdentity();
			glOrtho(0,Defines.WIDTH,Defines.HEIGHT,0,-1.f,1.f);
	}

	/**
	 * Pops the old model view matrix.
	 * Use to revert after calling pushOrtho().
	 */
	private void popOrtho(){
			glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();

		glDisable(GL_BLEND);
		glEnable(GL_ALPHA_TEST);
		glEnable(GL_DEPTH_TEST);
	}

	/**
	 * Gets the number of seconds passed since last call.
	 *
	 * @return Delta time as a floating point value
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
	public void execute() throws Exception {
		// Setup screen and OpenGL stuff
		init();

		// Load resources
		ResMgr.loadResources();

		// Start game loop
		loop();

		// Destroy display before returning
		Display.destroy();
	}

	public static void main (String[] args){
		Game game = new Game();
		try{
			game.execute();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
