package net.obviam.starassault.view;

import net.obviam.starassault.model.Block;
import net.obviam.starassault.model.Bob;
import net.obviam.starassault.model.World;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Rectangle;

public class WorldRenderer {

	private World world;
	private OrthographicCamera cam;

	
	/** for debug rendering **/
	ImmediateModeRenderer20 debugRenderer = new ImmediateModeRenderer20(1024, false, true, 0);
	
	public WorldRenderer(World world) {
		this.world = world;
		this.cam = new OrthographicCamera(10, 7);
		this.cam.position.set(5, 3.5f, 0);
		this.cam.update();

	}
	
	public void render() {
		debugRenderer.begin(cam.combined, GL10.GL_LINES);
		// render blocks
		for (Block block : world.getBlocks()) {
			Rectangle rect = block.getBounds();
			float x1 = block.getPosition().x + rect.x;
			float y1 = block.getPosition().y + rect.y;
			float x2 = x1 + rect.width;
			float y2 = y1 + rect.height;
			debugRenderer.color(1, 0, 0, 1);
			debugRenderer.vertex(x1, y1, 0);
			debugRenderer.color(1, 0, 0, 1);
			debugRenderer.vertex(x1, y2, 0);

			debugRenderer.color(1, 0, 0, 1);
			debugRenderer.vertex(x1, y2, 0);
			debugRenderer.color(1, 0, 0, 1);
			debugRenderer.vertex(x2, y2, 0);

			debugRenderer.color(1, 0, 0, 1);
			debugRenderer.vertex(x2, y2, 0);
			debugRenderer.color(1, 0, 0, 1);
			debugRenderer.vertex(x2, y1, 0);

			debugRenderer.color(1, 0, 0, 1);
			debugRenderer.vertex(x2, y1, 0);
			debugRenderer.color(1, 0, 0, 1);
			debugRenderer.vertex(x1, y1, 0);
		}
		// render Bob
		Bob bob = world.getBob();
		Rectangle rect = bob.getBounds();
		float x1 = bob.getPosition().x + rect.x;
		float y1 = bob.getPosition().y + rect.y;
		float x2 = x1 + rect.width;
		float y2 = y1 + rect.height;
		debugRenderer.color(0, 1, 0, 1);
		debugRenderer.vertex(x1, y1, 0);
		debugRenderer.color(0, 1, 0, 1);
		debugRenderer.vertex(x1, y2, 0);

		debugRenderer.color(0, 1, 0, 1);
		debugRenderer.vertex(x1, y2, 0);
		debugRenderer.color(0, 1, 0, 1);
		debugRenderer.vertex(x2, y2, 0);

		debugRenderer.color(0, 1, 0, 1);
		debugRenderer.vertex(x2, y2, 0);
		debugRenderer.color(0, 1, 0, 1);
		debugRenderer.vertex(x2, y1, 0);

		debugRenderer.color(0, 1, 0, 1);
		debugRenderer.vertex(x2, y1, 0);
		debugRenderer.color(0, 1, 0, 1);
		debugRenderer.vertex(x1, y1, 0);
		
		debugRenderer.end();
	}
}
