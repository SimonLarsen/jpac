import java.io.IOException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import org.lwjgl.opengl.GL11;

public class ResMgr implements Defines {
	static Texture tiles,sprites;

	public static void loadTextures() throws IOException {
		sprites = TextureLoader.getTexture("PNG",ResourceLoader.getResourceAsStream("res/sprites.png"),GL11.GL_NEAREST);
		tiles = TextureLoader.getTexture("PNG",ResourceLoader.getResourceAsStream("res/tiles.png"),GL11.GL_NEAREST);
	}
}
