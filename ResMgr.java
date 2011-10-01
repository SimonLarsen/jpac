import java.io.IOException;
import java.io.InputStream;
import java.awt.Font;

import org.lwjgl.opengl.GL11;

import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class ResMgr implements Defines {
	static Texture tiles,sprites;
	public static TrueTypeFont font;

	public static void loadTextures() throws Exception {
		sprites = TextureLoader.getTexture("PNG",ResourceLoader.getResourceAsStream("res/sprites.png"),GL11.GL_NEAREST);
		tiles = TextureLoader.getTexture("PNG",ResourceLoader.getResourceAsStream("res/tiles.png"),GL11.GL_NEAREST);
	}

	public static void loadResources() throws Exception {
		loadTextures();

		InputStream is = ResourceLoader.getResourceAsStream("res/font.ttf");
		Font awtFont = Font.createFont(Font.TRUETYPE_FONT,is);
		awtFont = awtFont.deriveFont(48f);
		font = new TrueTypeFont(awtFont,false);
	}
}
