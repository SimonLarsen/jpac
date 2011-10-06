import java.io.IOException;
import java.io.InputStream;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class ResMgr implements Defines {
	static public Texture[] tiles;
	static public Texture sprites;
	static public BufferedImage[] levels;
	public static TrueTypeFont font;

	public static void loadTextures() throws Exception {
		sprites = TextureLoader.getTexture("PNG",ResourceLoader.getResourceAsStream("res/sprites.png"),GL11.GL_NEAREST);

		tiles = new Texture[NUM_TILEMAPS];
		for(int i = 0; i < NUM_TILEMAPS; ++i){
			tiles[i] = TextureLoader.getTexture("PNG",ResourceLoader.getResourceAsStream("res/tiles/"+i+".png"),GL11.GL_NEAREST);
		}

		levels = new BufferedImage[NUM_LEVELS];
		for(int i = 0; i < NUM_LEVELS; ++i){
			levels[i] = ImageIO.read(ResourceLoader.getResourceAsStream("res/levels/"+i+".png"));
		}
	}

	public static void loadResources() throws Exception {
		loadTextures();

		InputStream is = ResourceLoader.getResourceAsStream("res/font.ttf");
		Font awtFont = Font.createFont(Font.TRUETYPE_FONT,is);
		awtFont = awtFont.deriveFont(48f);
		font = new TrueTypeFont(awtFont,false);
	}

	private static final int NUM_TILEMAPS = 2;
	private static final int NUM_LEVELS = 1;
}
