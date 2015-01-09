package net.obviam.starassault.model;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import net.obviam.starassault.controller.LevelLoader;

public class World {

	/** Our player controlled hero **/
	Bob bob;
	/** A world has a level through which Bob needs to go through **/
	Level level;
	
	/** The collision boxes **/
	Array<Rectangle> collisionRects = new Array<Rectangle>();

	// Getters -----------
	
	public Array<Rectangle> getCollisionRects() {
		return collisionRects;
	}
	public Bob getBob() {
		return bob;
	}
	public Level getLevel() {
		return level;
	}

	private final List<Block> blocks = new ArrayList<Block>(20);

	/** Return only the blocks that need to be drawn **/
	public List<Block> getDrawableBlocks(int width, int height) {

		int x = Math.max(0, (int) bob.getPosition().x - width);
		int y = Math.max(0, (int) bob.getPosition().y - height);

		int x2 = Math.min(level.getWidth() - 1, (x + 2 * width));
		int y2 = Math.min(level.getHeight() - 1, (y + 2 * height));

		blocks.clear();
		for (int col = x; col <= x2; col++) {
			for (int row = y; row <= y2; row++) {
				Block block = level.getBlocks()[col][row];
				if (block != null) {
					blocks.add(block);
				}
			}
		}
		return blocks;
	}

	// --------------------
	public World() {
		createWorld();
	}

    private void createWorld() {
        level = LevelLoader.loadLevel(1);
        bob = new Bob(level.getSpanPosition());
    }

//    private void createDemoWorld() {
//		bob = new Bob(new Vector2(7, 2));
//		level = new Level();
//	}
}
