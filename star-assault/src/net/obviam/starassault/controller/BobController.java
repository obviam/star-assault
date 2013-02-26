package net.obviam.starassault.controller;

import java.util.HashMap;
import java.util.Map;

import net.obviam.starassault.model.Block;
import net.obviam.starassault.model.Bob;
import net.obviam.starassault.model.Bob.State;
import net.obviam.starassault.model.World;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

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
	private boolean grounded = false;

	// This is the rectangle pool used in collision detection 
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	
	static Map<Keys, Boolean> keys = new HashMap<BobController.Keys, Boolean>();
	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.JUMP, false);
		keys.put(Keys.FIRE, false);
	};

	// Blocks that Bob can collide with
	private Array<Block> collidable = new Array<Block>();
	
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
		
		if (grounded && bob.getState().equals(State.JUMPING)) {
			bob.setState(State.IDLE);
		}
		
		bob.getAcceleration().y = GRAVITY;
		bob.getAcceleration().mul(delta);
		bob.getVelocity().add(bob.getAcceleration().x, bob.getAcceleration().y);
		bob.getVelocity().x *= DAMP;
		
		if (bob.getVelocity().x > MAX_VEL) {
			bob.getVelocity().x = MAX_VEL;
		}
		if (bob.getVelocity().x < -MAX_VEL) {
			bob.getVelocity().x = -MAX_VEL;
		}
		
		// at this stage we commented out Bob's update method for position
		// and we will work with the bounds and update position later
		checkCollisionWithBlocks(delta);
		
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
		// set velocity to this particular frame
		bob.getVelocity().mul(delta);
		
		// Obtaining the rectangle from the pool instead of instantiating it
		Rectangle bobRect = rectPool.obtain();
		bobRect.set(bob.getBounds().x, bob.getBounds().y, bob.getBounds().width, bob.getBounds().height);
		
		// we first check the movement on the horizontal
		int startX, endX;
		int startY = (int) bob.getBounds().y;
		int endY = (int) (bob.getBounds().y + bob.getBounds().height);
		if (bob.getVelocity().x < 0) {
			startX = endX = (int) Math.floor(bob.getBounds().x + bob.getVelocity().x);
		} else {
			startX = endX = (int) Math.floor(bob.getBounds().x + bob.getBounds().width + bob.getVelocity().x);
		}
		
		populateCollidableBlocks(startX, startY, endX, endY);

		bobRect.x += bob.getVelocity().x;
		
		for (Block block : collidable) {
			if (block == null) continue;
			if (bobRect.overlaps(block.getBounds())) {
				bob.getVelocity().x = 0;
				break;
			}
		}

		// resetting the collision box's x position
		bobRect.x = bob.getPosition().x;
		
		startX = (int) bob.getBounds().x;
		endX = (int) (bob.getBounds().x + bob.getBounds().width);
		if (bob.getVelocity().y < 0) {
			startY = endY = (int) Math.floor(bob.getBounds().y + bob.getVelocity().y);
		} else {
			startY = endY = (int) Math.floor(bob.getBounds().y + bob.getBounds().height + bob.getVelocity().y);
		}
		
		populateCollidableBlocks(startX, startY, endX, endY);
		
		bobRect.y += bob.getVelocity().y;
		
		for (Block block : collidable) {
			if (block == null) continue;
			if (bobRect.overlaps(block.getBounds())) {
				if (bob.getVelocity().y < 0) {
					grounded = true;
				}
				bob.getVelocity().y = 0;
				break;
			}
		}
		// reset the collision box's position on Y
		bobRect.y = bob.getPosition().y;
		
		// update Bob's position
		bob.getPosition().add(bob.getVelocity());
		bob.getBounds().x = bob.getPosition().x;
		bob.getBounds().y = bob.getPosition().y;
		
		// un-scale velocity (not in frame time)
		bob.getVelocity().mul(1 / delta);
		
	}

	private void populateCollidableBlocks(int startX, int startY, int endX, int endY) {
		collidable.clear();
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				if (x >= 0 && x < world.getLevel().getWidth() && y >=0 && y < world.getLevel().getHeight()) {
					collidable.add(world.getLevel().get(x, y));
				}
			}
		}
	}

	/** Change Bob's state and parameters based on input controls **/
	private boolean processInput() {
		if (keys.get(Keys.JUMP)) {
			if (!bob.getState().equals(State.JUMPING)) {
				jumpingPressed = true;
				jumpPressedTime = System.currentTimeMillis();
				bob.setState(State.JUMPING);
				bob.getVelocity().y = MAX_JUMP_SPEED; 
				grounded = false;
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
