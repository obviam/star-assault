package net.obviam.starassault.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import net.obviam.starassault.model.Block;
import net.obviam.starassault.model.Level;

/**
 * Created with IntelliJ IDEA.
 * User: tamas
 * Date: 26/03/2013
 * Time: 15:30
 * To change this template use File | Settings | File Templates.
 */
public class LevelLoader {

    private static final String LEVEL_PREFIX    = "levels/level-";

    private static final int    BLOCK           = 0x000000; // black
    private static final int    EMPTY           = 0xffffff; // white
    private static final int    START_POS       = 0x0000ff; // blue

    public static Level loadLevel(int number) {
        Level level = new Level();

        // Loading the png into a Pixmap
        Pixmap pixmap = new Pixmap(Gdx.files.internal(LEVEL_PREFIX + number + ".png"));

        // setting the size of the level based on the size of the pixmap
        level.setWidth(pixmap.getWidth());
        level.setHeight(pixmap.getHeight());

        // creating the backing blocks array
        Block[][] blocks = new Block[level.getWidth()][level.getHeight()];
        for (int col = 0; col < level.getWidth(); col++) {
            for (int row = 0; row < level.getHeight(); row++) {
                blocks[col][row] = null;
            }
        }


        for (int row = 0; row < level.getHeight(); row++) {
            for (int col = 0; col < level.getWidth(); col++) {
                int pixel = (pixmap.getPixel(col, row) >>> 8) & 0xffffff;
                int iRow = level.getHeight() - 1 - row;
                if (pixel == BLOCK) {
                    // adding a block
                    blocks[col][iRow] = new Block(new Vector2(col, iRow));
                } else if (pixel == START_POS) {
                    level.setSpanPosition(new Vector2(col, iRow));
                }
            }
        }

        // setting the blocks
        level.setBlocks(blocks);
        return level;
    }

}
