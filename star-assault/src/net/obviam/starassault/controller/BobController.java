package net.obviam.starassault.controller;

import java.util.HashMap;
import java.util.Map;

import net.obviam.starassault.model.Block;
import net.obviam.starassault.model.Bob;
import net.obviam.starassault.model.Bob.State;
import net.obviam.starassault.model.World;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BobController {

	enum Keys {
		LEFT, RIGHT, JUMP, FIRE
	}

	private static final long LONG_JUMP_PRESS 	= 150l;
	private static final float ACCELERATION 	= 20f;
	private static final float GRAVITY 			= -20f;
	private static final float MAX_JUMP_SPEED	= 7f;
	private static final float DAMP 			= 0.90f;
	private static final float MAX_VEL 			= 4f;
	
	// these are temporary
	private static final float WIDTH = 10f;

	private World 	world;
	private Bob 	bob;
	private long	jumpPressedTime;
	private boolean jumpingPressed;
	private boolean grounded;
	
	static Map<Keys, Boolean> keys = new HashMap<BobController.Keys, Boolean>();
	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.JUMP, false);
		keys.put(Keys.FIRE, false);
	};

	// Blocks that Bob can collide with
	private Block[] collidable = {null, null, null, null};
	
	public BobController(World world) {
		this.world = world;
		this.bob = world.getBob();
	}

	// ** Key presses and touches **************** //
	
	public void leftPressed() {
		keys.get(keys.put(Keys.LEFT, true));
	}
	
	public void rightPressed() {
		keys.get(keys.put(Keys.RIGHT, true));
	}
	
	public void jumpPressed() {
		keys.get(keys.put(Keys.JUMP, true));
	}
	
	public void firePressed() {
		keys.get(keys.put(Keys.FIRE, false));
	}
	
	public void leftReleased() {
		keys.get(keys.put(Keys.LEFT, false));
	}
	
	public void rightReleased() {
		keys.get(keys.put(Keys.RIGHT, false));
	}
	
	public void jumpReleased() {
		keys.get(keys.put(Keys.JUMP, false));
		jumpingPressed = false;
	}
	
	public void fireReleased() {
		keys.get(keys.put(Keys.FIRE, false));
	}
	
	/** The main update method **/
	public void update(float delta) {
		processInput();
		
		bob.getAcceleration().y = GRAVITY;
		bob.getAcceleration().mul(delta);
		bob.getVelocity().add(bob.getAcceleration().x, bob.getAcceleration().y);
		if (Math.abs(bob.getAcceleration().x) >= 0.1f) bob.getVelocity().x *= DAMP;
		if (bob.getVelocity().x > MAX_VEL) {
			bob.getVelocity().x = MAX_VEL;
		}
		if (bob.getVelocity().x < -MAX_VEL) {
			bob.getVelocity().x = -MAX_VEL;
		}
		
		// at this stage we commented out Bob's update method for position
		// and we will work with the bounds and update position later
		checkCollisionWithBlocks(delta);
		
		// update bob's position according to his bounding box
		bob.getPosition().x = bob.getBounds().x;
		bob.getPosition().y = bob.getBounds().y;

		// simply updates the state time
		bob.update(delta);

/*		
		if (bob.getPosition().y < 0) {
			bob.getPosition().y = 0f;
			bob.setPosition(bob.getPosition());
			if (bob.getState().equals(State.JUMPING)) {
					bob.setState(State.IDLE);
			}
		}
		if (bob.getPosition().x < 0) {
			bob.getPosition().x = 0;
			bob.setPosition(bob.getPosition());
			if (!bob.getState().equals(State.JUMPING)) {
				bob.setState(State.IDLE);
			}
		}
		if (bob.getPosition().x > WIDTH - bob.getBounds().width ) {
			bob.getPosition().x = WIDTH - bob.getBounds().width;
			bob.setPosition(bob.getPosition());
			if (!bob.getState().equals(State.JUMPING)) {
				bob.setState(State.IDLE);
			}
		}
*/
	}

	private void checkCollisionWithBlocks(float delta) {
		// we are moving bob on the x axis and fetching the blocks
		bob.getBounds().x += bob.getVelocity().x * delta;
		populateCollidableBlocks();
		Rectangle blockRect;
		for (int i = 0; i < collidable.length; i++) {
			Block block= collidable[i];
			if (block == null) continue;
			blockRect = block.getBounds();
			if (bob.getBounds().overlaps(blockRect)) {
				if (bob.getVelocity().x < 0)
					bob.getBounds().x = blockRect.x + blockRect.width + 0.01f;
				else if (bob.getVelocity().x > 0)
					bob.getBounds().x = blockRect.x - bob.getBounds().width - 0.01f;
				bob.getVelocity().x = 0;
			}
		}

		// we are moving bob on the Y axis and fetching the blocks
		bob.getBounds().y += bob.getVelocity().y * delta;
		populateCollidableBlocks();
		for (int i = 0; i < collidable.length; i++) {
			Block block= collidable[i];
			if (block == null) continue;
			blockRect = block.getBounds();
			if (bob.getBounds().overlaps(blockRect)) {
				if (bob.getVelocity().y < 0) {
					bob.getBounds().y = blockRect.y + blockRect.height + 0.01f;
					grounded = true;
					if (!bob.getState().equals(State.DYING)) {
						bob.setState(Math.abs(bob.getAcceleration().x) > 0.1f ? State.WALKING : State.IDLE);
					}
				} else if (bob.getVelocity().y > 0)
					bob.getBounds().y = blockRect.y - bob.getBounds().height - 0.01f;
				bob.getVelocity().y = 0;
			}
		}
	}

	private void populateCollidableBlocks() {
		Vector2 pos = bob.getPosition();
		// position of lower left
		int p1x = (int)Math.floor(pos.x);
		int p1y = (int)Math.floor(pos.y);
		
		// lower right
		int p2x = (int)(p1x + Block.SIZE);
		int p2y = p1y;
		
		// upper left
		int p3x = p2x;
		int p3y = (int)(p1y + Block.SIZE);
		
		// upper right
		int p4x = p1x;
		int p4y = p3y;

		// getting blocks
		collidable[0] = world.getLevel().getBlocks()[p1x][p1y];
		collidable[1] = world.getLevel().getBlocks()[p2x][p2y];
		collidable[2] = world.getLevel().getBlocks()[p3x][p3y];
		collidable[3] = world.getLevel().getBlocks()[p4x][p4y];
		
	}

	/** Change Bob's state and parameters based on input controls **/
	private boolean processInput() {
		if (keys.get(Keys.JUMP)) {
			if (!bob.getState().equals(State.JUMPING)) {
				jumpingPressed = true;
				jumpPressedTime = System.currentTimeMillis();
				bob.setState(State.JUMPING);
				bob.getVelocity().y = MAX_JUMP_SPEED; 
			} else {
				if (jumpingPressed && ((System.currentTimeMillis() - jumpPressedTime) >= LONG_JUMP_PRESS)) {
					jumpingPressed = false;
				} else {
					if (jumpingPressed) {
						bob.getVelocity().y = MAX_JUMP_SPEED;
					}
				}
			}
		}
		if (keys.get(Keys.LEFT)) {
			// left is pressed
			bob.setFacingLeft(true);
			if (!bob.getState().equals(State.JUMPING)) {
				bob.setState(State.WALKING);
			}
			bob.getAcceleration().x = -ACCELERATION;
		} else if (keys.get(Keys.RIGHT)) {
			// left is pressed
			bob.setFacingLeft(false);
			if (!bob.getState().equals(State.JUMPING)) {
				bob.setState(State.WALKING);
			}
			bob.getAcceleration().x = ACCELERATION;
		} else {
			if (!bob.getState().equals(State.JUMPING)) {
				bob.setState(State.IDLE);
			}
			bob.getAcceleration().x = 0;
			
		}
		return false;
	}

}
