package com.jetdrone.clonabalt;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.FloatMath;

import com.jetdrone.engine.AbstractGame;
import com.jetdrone.engine.SceneGraph;
import com.jetdrone.engine.sprite.ISprite;
import com.jetdrone.engine.sprite.SpritePaint;
import com.jetdrone.engine.sprite.SpriteParallax;
import com.jetdrone.engine.sprite.SpriteRepeat;
import com.jetdrone.engine.sprite.SpriteSheet;
import com.jetdrone.engine.sprite.SpriteText;
import com.jetdrone.engine.util.FastMath;
import com.jetdrone.engine.util.LList;

public class Game extends AbstractGame {


	private final Activity activity;
	public final Config config;
	private final SceneGraph container;

	int canvasWidth;
	int canvasHeight;

	private int cycles;
	float speed;
	private int distance;
	private int shakeDuration;
	boolean airborne;
	private boolean jumping;
	private float ySpeed;
	float y;
	float x;
	private float acceleration;
	private float jumpImpulse;
	private float gravity;
	Building currentBuilding;
	private SpriteSheet runner;
	private int runnerFrame;
	private int runnerRunAnimationDistance;
	private SpriteParallax paralaxBg1;
	private SpriteParallax paralaxBg2;
	private SpriteText distanceCounter;
	private LList<Building> buildings = new LList<Building>();
	private ISprite paralaxBeam;
	private int paralaxBeamTimeout;
	private boolean falling;
	
	public Game(Activity activity) {
		super(activity, 20, false);
		this.activity = activity;
		container = new SceneGraph();
		config = new Config(activity.getResources());
	}
		
	void addBuilding(Building building) {
		buildings.append(building);
		container.addFront(building);
	}

	void removeFirstBuilding() {
		container.remove(buildings.removeFirst());
	}

	private void startJump() {
		if (!airborne && !jumping) {
			airborne = true;
			jumping = true;
			ySpeed = jumpImpulse;
		}
	}

	private void endJump() {
		if (airborne && jumping) {
			jumping = false;
			if (ySpeed > 0)
				ySpeed = 0;
		} else if (jumping) {
			jumping = false;
		}
	}

	private void scheduleParalaxBeam(int wait) {
		paralaxBeamTimeout = setTimeout(wait);
	}

	private void spawnParalaxBeam() {
		// Choose one of two possible beam styles
		if(RAND.nextBoolean()) {
			Bitmap bmp = loadBitmap(R.drawable.paralax_fg_crossbeams);
			paralaxBeam = new SpriteRepeat(bmp, bmp.getWidth(), canvasHeight);
		} else {
			Paint paint = new Paint();
			paint.setColor(0xFF35353D);
			paralaxBeam = new SpritePaint(paint, 40, canvasHeight);
		}
		
		paralaxBeam.left = canvasWidth;
		// Insert paralax element
		container.addFront(paralaxBeam);
	}

	private void removeParalaxBeam() {
		if (paralaxBeam != null) {
			container.remove(paralaxBeam);
		}

		if (paralaxBeamTimeout != -1) {
			clearTimeout(paralaxBeamTimeout);
		}

		this.paralaxBeam = null;
		this.paralaxBeamTimeout = -1;
	}

	/**
	 * Init Objects that live the whole lifecycle of the app
	 */
	@Override
	public void init(int width, int height) {
		canvasWidth = width;
		canvasHeight = height;
		container.init(width, height);
		container.setBackground(0xFFB0B0BF);
		
		// Reset cycle counter
		cycles = 0;

		// Reset speed and traveled distance
		speed = 0.2f;
		distance = 0;

		shakeDuration = config.SHAKE_START;

		// Runner variables
		airborne = false;
		jumping = false;
		ySpeed = 0;

		// Copy some options to object space for quicker access
		acceleration = 0.0001f;
		jumpImpulse = 5.5f;
		gravity = 0.15f;

		// Pointer to the building the runner is currently "stepping" on
		currentBuilding = null;

		// Create runner DIV
		runner = new SpriteSheet(loadBitmap(R.drawable.runner));
		runner.top = canvasHeight - config.RUNNER_HEIGHT;
		container.addFront(runner);

		runnerFrame = 0;
		runnerRunAnimationDistance = 0;

		// First paralax background
		paralaxBg1 = new SpriteParallax(loadBitmap(R.drawable.paralax_bg_1), canvasWidth);
		paralaxBg1.top = canvasHeight - config.PARALAX_BG_1_TOP_OFFSET;
		container.addBack(paralaxBg1);
		
		// Second paralax background
		paralaxBg2 = new SpriteParallax(loadBitmap(R.drawable.paralax_bg_2), canvasWidth);
		paralaxBg2.top = canvasHeight - config.PARALAX_BG_2_TOP_OFFSET; 
		container.addBack(paralaxBg2);
		
		removeParalaxBeam();
		scheduleParalaxBeam(config.PARALAX_FG_INITIAL_WAIT);

		// Distance counter
		if (distanceCounter == null) {
			distanceCounter = new SpriteText(Typeface.SANS_SERIF, 15, Color.WHITE);
			distanceCounter.left = canvasWidth - 60;
			distanceCounter.top = 20;
		}

		// Remove all buildings
		buildings.clear();

		// Place the first building
		Building b = new Building(this);
		// first building should start at 0
		b.left = 0;
		y = b.height;
		
		addBuilding(b);
	}

	/**
	 * This is where most the game logic happens
	 */
	@Override
	public void updateState() {
		
		// Increment cycles counter
		cycles++;

		// Calculate how much we moved this cycle
		int cycleDistance = FastMath.round(MILLIS_PER_FRAME * speed);
		// Increment the total distance ran
		distance += cycleDistance;

		// Increase speed
		speed += acceleration;

		// Runner's x offset is square root of the speed times a multiplier
		x = (int) (FloatMath.sqrt(speed) * config.RUNNER_X_OFFSET_COEFFICIENT);

		// Check jump
		if (airborne) {
			// Calculate which jumping frame to display based on
			// vertical speed
			if (ySpeed > jumpImpulse * 0.66) {
				runnerFrame = 0;
			} else if (ySpeed > jumpImpulse * 0.33) {
				runnerFrame = 1;
			} else if (ySpeed > jumpImpulse * 0.1) {
				runnerFrame = 2;
			} else if (!falling && ySpeed <= 0) {
				falling = true;
				runnerFrame = 3;
			} else if (cycles % config.RUNNER_FALLING_ANIMATION_FREQ == 0) {
				runnerFrame++;
				if (runnerFrame == 11)
					runnerFrame = 3;
			}

			y += ySpeed;
			ySpeed -= gravity;

			int h = currentBuilding != null ? currentBuilding.height : 0;

			if (y < h) {
				y = h;
				ySpeed = 0;
				airborne = false;
				falling = false;
			}
		} else {

			runnerRunAnimationDistance += cycleDistance;

			// Set runner animation frame
			if (runnerRunAnimationDistance > config.RUNNER_RUNNING_CHANGE_FRAME_DISTANCE) {
				runnerRunAnimationDistance = 0;
				if(++runnerFrame == config.RUNNER_RUNNING_FRAMECOUNT) {
					runnerFrame = 0;
				}
			}
		}

		// Move buildings
		LList.Node<Building> it = buildings.iterator();
		
		while(it != null) {
			it.data().move(cycleDistance);
			// next
			it = it.next();
		}

		// Move paralax
		paralaxBg1.left -= FastMath.round(cycleDistance * config.PARALAX_BG_1_SPEED);
		paralaxBg2.left -= FastMath.round(cycleDistance * config.PARALAX_BG_2_SPEED);

		if (paralaxBeam != null) {
			paralaxBeam.left -= FastMath.round(cycleDistance * config.PARALAX_FG_SPEED);
			if (paralaxBeam.left <= -paralaxBeam.width) {
				removeParalaxBeam();
				scheduleParalaxBeam(RAND.nextInt((int) (5000.0f / speed)));
			}
		}

		// Shake it baby
		if (shakeDuration > 0) {
			shakeDuration -= MILLIS_PER_FRAME;
			if (shakeDuration <= 0) {
				shakeDuration = 0;
				container.setTop(0);
				container.setLeft(0);
			} else {
				// Since shaking the screen is mostly a random process that doesn't
				// affect gameplay, calculate the shaking offset when drawing a
				// frame instead of each cycle
				container.setTop(RAND.nextInt(config.SHAKE_AMPLITUDE));
			}
		}

		if (airborne) {
			runner.setFrame(runnerFrame, 1, config.RUNNER_JUMPING_WIDTH, config.RUNNER_HEIGHT);
		} else {
			runner.setFrame(runnerFrame, 0, config.RUNNER_WIDTH, config.RUNNER_HEIGHT);
		}
		
		// place the runner
		runner.top = canvasHeight - config.RUNNER_HEIGHT - FastMath.round(y);
		runner.left = FastMath.round(x);

		// Update distance counter
		distanceCounter.setText(FastMath.round(distance * config.DISTANCE_TO_METERS_COEFFICIENT) + "m");
	}

	/**
	 * Called from the game loop to render every frame to the canvas
	 */
	@Override
	public void updateCanvas(Canvas canvas) {
		container.draw(canvas);
		if (distanceCounter != null) {
			distanceCounter.draw(canvas);
		}
	}

	/**
	 * Called when the game is over
	 */
	@Override
	public void finish() {
		activity.finish();
	}

	@Override
	public void onSensor(float[] data) {
	}

	@Override
	public void onTouch(boolean down, float x, float y) {
		if (down) {
			startJump();
		} else {
			endJump();
		}
	}

	@Override
	public void onTimeout(int timeoutId) {
		if (timeoutId == paralaxBeamTimeout) {
			spawnParalaxBeam();
		}
	}
}
