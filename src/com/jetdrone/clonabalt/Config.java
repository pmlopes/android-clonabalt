package com.jetdrone.clonabalt;

import android.content.res.Resources;

public class Config {

	public final float DISTANCE_TO_METERS_COEFFICIENT = 0.055f;

	public final int PARALAX_BG_1_TOP_OFFSET;
	public final float PARALAX_BG_1_SPEED = 0.3f;

	public final int PARALAX_BG_2_TOP_OFFSET;
	public final float PARALAX_BG_2_SPEED = 0.2f;

	public final int PARALAX_FG_SPEED = 3;
	public final int PARALAX_FG_INITIAL_WAIT = 3000;

	public final int SHAKE_START = 3000;
	public final int SHAKE_AMPLITUDE;

	public final int RUNNER_WIDTH;
	public final int RUNNER_HEIGHT;

	public final int RUNNER_JUMPING_WIDTH;
	public final int RUNNER_FALLING_ANIMATION_FREQ = 6;

	public final int RUNNER_X_OFFSET_COEFFICIENT = 100;
	public final int RUNNER_RUNNING_FRAMECOUNT = 16;
	public final int RUNNER_RUNNING_CHANGE_FRAME_DISTANCE;
	
	public Config(Resources res) {
		PARALAX_BG_1_TOP_OFFSET = res.getInteger(R.integer.PARALAX_BG_1_TOP_OFFSET);
		PARALAX_BG_2_TOP_OFFSET = res.getInteger(R.integer.PARALAX_BG_2_TOP_OFFSET);
		SHAKE_AMPLITUDE = res.getInteger(R.integer.SHAKE_AMPLITUDE);
		RUNNER_WIDTH = res.getInteger(R.integer.RUNNER_WIDTH);
		RUNNER_HEIGHT = res.getInteger(R.integer.RUNNER_HEIGHT);
		RUNNER_JUMPING_WIDTH = res.getInteger(R.integer.RUNNER_JUMPING_WIDTH);
		RUNNER_RUNNING_CHANGE_FRAME_DISTANCE = res.getInteger(R.integer.RUNNER_RUNNING_CHANGE_FRAME_DISTANCE);
	}
	
}
