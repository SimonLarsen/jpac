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

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;

public class Game extends Applet implements Defines {
	private boolean paused, running;
	private long lastUpdate;

	private static Player pl;
	private static Map map;
	private static ArrayList<Pickup> dots;
	private static ArrayList<Ghost> ghosts;
	private static ArrayList<Particle> particles;
	private static ArrayList<ScreenEffect> effects;
	private static ArrayList<Event> events;

	public static int state, level;

	// Applet stuff 
	private Canvas display_parent;
	Thread gameThread;

	/**
	 * Main game loop.
	 */
	private void loop() throws Exception {
		Mouse.setGrabbed(true);
		running = true;
		float clearWait = CLEARWAIT_TIME;
		paused = false;
		effects.add(new FadeEffect(FadeEffect.FADE_WHITE,FadeEffect.FADE_UP,2.0f*CLEARWAIT_TIME));

		dots.clear();
		ghosts.clear();
		events.clear();
		particles.clear();
		map.load(level,dots,ghosts,events);

		pl = new Player();
		pl.respawn(map.startx, map.startz);

		getDelta(); // To calibrate

		while(running){
			// Get delta time. Call even when paused to calibrate.
			float dt = getDelta();
			// Close window eh?	
			if(Display.isCloseRequested()){
				running = false;
				state = STATE_QUIT;
			}
			// Poll key events
			while(Keyboard.next()){
				if(Keyboard.getEventKeyState()){ // Key pressed
					int key = Keyboard.getEventKey();
					if(key == Keyboard.KEY_P || key == Keyboard.KEY_ESCAPE){
						paused = !paused;
						Mouse.setGrabbed(!paused);
					}
					else if(key == Keyboard.KEY_Q && paused){
						running = false;
						state = STATE_MENU;
					}
				}
			}
			// Don't update if paused
			if(!paused){
				// Update player
				pl.update(dt,map);
				if(pl.collideDots(dots) == 1){
					for(int i = 0; i < ghosts.size(); ++i){
						ghosts.get(i).setScared();
					}
				}
				pl.collideGhosts(ghosts);

				// Update events
				for(int i = events.size()-1; i >= 0; --i){
					if(events.get(i).status == Event.STATUS_DEAD){
						events.remove(i);
					}
					else{
						events.get(i).update(dt,pl,map,ghosts);
					}
				}

				// Update map
				map.update(dt);

				// Update screen effects
				for(int i = effects.size()-1; i >= 0; --i){
					if(effects.get(i).alive){
						effects.get(i).update(dt);
					}
					else{
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

				// Check if all dots have been eaten
				if(dots.size() == 0){
					if(clearWait == CLEARWAIT_TIME){
						effects.add(new FadeEffect(FadeEffect.FADE_WHITE,FadeEffect.FADE_DOWN,clearWait));
					}
					else if(clearWait <= 0.f){
						running = false;
						level++;
					}
					clearWait -= dt;
				}
			} // if(!paused)

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			draw();
			draw2D();

			Display.update();
			Display.sync(60);
		}
	}

	/**
	 * Main menu loop
	 */
	private void menuLoop() throws Exception {
		Mouse.setGrabbed(false);
		dots.clear();
		ghosts.clear();
		events.clear();
		particles.clear();
		map.load(0,dots,ghosts,events);

		int selection = 0;
		boolean[] hover = new boolean[3];
		while(state == STATE_MENU){
			float dt = getDelta();
			if(Display.isCloseRequested()){
				state = STATE_QUIT;
			}
			while(Keyboard.next()){
				if(Keyboard.getEventKeyState()){ // Key pressed
					int key = Keyboard.getEventKey();
					if(key == Keyboard.KEY_ESCAPE){
						state = STATE_QUIT;
					}
				}
			}
			
			map.update(dt);

			int mx = Mouse.getX();
			int my = Mouse.getY();
			hover[0] = (my > 328 && my < 365 && mx > 225 && mx < 800-225);
			hover[1] = (my > 253 && my < 290 && mx > 225 && mx < 800-225);
			hover[2] = (my > 178 && my < 215 && mx > 225 && mx < 800-225);

			if(Mouse.isButtonDown(0)){
				if(hover[0]){ state = STATE_GAME; }
				if(hover[2]){ state = STATE_QUIT; }
			}

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glLoadIdentity();

			glTranslatef(-4.5f,-0.6f,-15.5f);
			map.draw();

			pushOrtho();

			int xoff = 3 - (int)Math.floor(Math.random()*7.0);
			int yoff = 3 - (int)Math.floor(Math.random()*7.0);
			if(hover[0]){
				ResMgr.font.drawString((Defines.WIDTH-ResMgr.font.getWidth("START GAME"))/2+xoff,230+yoff,"START GAME");
			} else {
				ResMgr.font.drawString((Defines.WIDTH-ResMgr.font.getWidth("START GAME"))/2,230,"START GAME");
			}

			if(hover[1]){
				ResMgr.font.drawString((Defines.WIDTH-ResMgr.font.getWidth("LEVELS"))/2+xoff,305+yoff,"LEVELS");
			} else {
				ResMgr.font.drawString((Defines.WIDTH-ResMgr.font.getWidth("LEVELS"))/2,305,"LEVELS");
			}

			if(hover[2]){
				ResMgr.font.drawString((Defines.WIDTH-ResMgr.font.getWidth("QUIT"))/2+xoff,380+yoff,"QUIT");
			} else {
				ResMgr.font.drawString((Defines.WIDTH-ResMgr.font.getWidth("QUIT"))/2,380,"QUIT");
			}

			popOrtho();
			Display.update();
			Display.sync(60);
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
		for(Event e : events){
			e.draw(pl.xdirdeg);
		}
	}

	/**
	 * Draw all 2D stuff to screen
	 */
	private void draw2D(){
		pushOrtho();
		if(paused){
			int xoff = 2 - (int)Math.floor(Math.random()*5.0);
			int yoff = 2 - (int)Math.floor(Math.random()*5.0);
			xoff = 2 - (int)Math.floor(Math.random()*5.0);
			yoff = 2 - (int)Math.floor(Math.random()*5.0);
			ResMgr.font.drawString(50+xoff,Defines.HEIGHT-110+yoff,"PAUSED");
			ResMgr.font.drawString(50+xoff,Defines.HEIGHT-70+yoff,"PRESS Q TO QUIT");
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
	private void initGL() throws Exception {
		// Create display
		Display.setDisplayMode(new DisplayMode(Defines.WIDTH,Defines.HEIGHT));
		Display.create();

		// Setup view matrix
		glViewport(0,0,Defines.WIDTH,Defines.HEIGHT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(80.f,(float)Defines.WIDTH/(float)Defines.HEIGHT,0.01f,100.f);
		glMatrixMode(GL_MODELVIEW);

		glLoadIdentity();
		// Background color
		glClearColor(0.f,0.f,0.f,0.f);

		// Depth buffer/testing
		glClearDepth(1.f);
		glDepthFunc(GL_LESS);
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

		// Setup game variables
		// and create objects
		map = new Map();
		dots = new ArrayList<Pickup>(32);
		ghosts = new ArrayList<Ghost>(4);
		particles = new ArrayList<Particle>();
		effects = new ArrayList<ScreenEffect>();
		events = new ArrayList<Event>();
	}

	/**
	 * Entry point for starting a game.
	 * Makes sure display is set up,
	 * calls game loop and destroy display on exit.
	 *
	 * @throws Exception If something goes bad. :(
	 */
	public void execute() throws Exception {
		// Setup screen and OpenGL stuff
		initGL();

		// Load resources
		ResMgr.loadResources();

		// Start game loop
		level = 0;
		state = STATE_MENU;
		while(state != STATE_QUIT){
			switch(state){
				case STATE_GAME: loop(); break;
				case STATE_MENU: menuLoop(); break;
				default: state = STATE_QUIT; break;
			}
		}

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

	// Applet methods
	public void init(){
		setLayout(new BorderLayout());
		try{
			display_parent = new Canvas(){
				public final void addNotify(){
					super.addNotify();
					executeApplet();
				}
				public final void removeNotify(){
					stopApplet();
					super.removeNotify();
				}
			};
			display_parent.setSize(Defines.WIDTH,Defines.HEIGHT);
			add(display_parent);
			display_parent.setFocusable(true);
			display_parent.requestFocus();
			display_parent.setIgnoreRepaint(true);
			setVisible(true);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void executeApplet(){
		gameThread = new Thread(){
			public void run(){
				try{
					Display.setParent(display_parent);
					execute();
				} catch (Exception e){
					e.printStackTrace();
				};
			}
		};
		gameThread.start();
	}

	public void stopApplet(){
		running = false;
		try{
			gameThread.join();
		} catch (InterruptedException ie){
			ie.printStackTrace();
		}
	}

	public void start() {}
	public void stop() {}

	public void destroy(){
		remove(display_parent);
		super.destroy();
	}

	public static final float CLEARWAIT_TIME = 1.0f;

	public static final int STATE_GAME	= 0;
	public static final int STATE_MENU 	= 1;
	public static final int STATE_QUIT	= 2;
}
