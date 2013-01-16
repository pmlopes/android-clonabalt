package com.jetdrone.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class AbstractGame extends SurfaceView implements Runnable, SurfaceHolder.Callback, SensorEventListener {
	
	/**
	 * Update Game State
	 */
	public abstract void updateState();
	
	/**
	 * Reset game variables in preparation for a new game.
	 * Is also called when the screen dimensions change.
	 * 
	 * @param width
	 * @param height
	 */
	public abstract void init(int width, int height);

    /**
     * Called from the game loop to render every frame to the canvas
     */
	public abstract void updateCanvas(Canvas canvas);
	
	/**
	 * Called when the sensor data is updated
	 * 
	 * @param data [x, y, z] accelerometer
	 */
	public abstract void onSensor(float[] data);
	
	/**
	 * Called when the screen is touched.
	 * @param down
	 * @param x
	 * @param y
	 */
	public abstract void onTouch(boolean down, float x, float y);
	
	public abstract void onTimeout(int timeoutId);
	
	/**
	 * Called when the game is over
	 */
	public abstract void finish();

	
    // sensor manager used to control the accelerometer sensor.
    private SensorManager mSensorManager;
    
	// App main thread
	private final Thread gameLoopThread;
	
	private final SurfaceHolder surfaceHolder;
	
	public final int MILLIS_PER_FRAME;
	public final int MAX_FRAME_SKIP;
	
	public static final Random RAND = new Random();
	
	private final boolean enableSensors;
	
	private boolean run = false;
	private boolean needInit = false;
	
	private int width;
	private int height;
	
	private long[] timeout = new long[10];
	
	private static final Map<Integer, Bitmap> BMP_CACHE = new HashMap<Integer, Bitmap>();
	
	public AbstractGame(final Context context, final int fps, boolean enableSensors) {
		super(context);
		MILLIS_PER_FRAME = 1000 / fps;
		MAX_FRAME_SKIP = MILLIS_PER_FRAME / 5;
		// configure the surface holder
		surfaceHolder = getHolder();
		surfaceHolder.setFormat(PixelFormat.RGB_565);
		surfaceHolder.addCallback(this);
		
        setClickable(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setHapticFeedbackEnabled(false);
        setKeepScreenOn(true);
        setLongClickable(false);
        setDrawingCacheEnabled(false);
        setWillNotCacheDrawing(true);
        setWillNotDraw(true);
        
		this.enableSensors = enableSensors;
		
		if(enableSensors) {
			// setup accelerometer sensor manager.
	        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		}
		
		gameLoopThread = new Thread(this);
	}
	
	public final Bitmap loadBitmap(int id) {
		Bitmap bmp = BMP_CACHE.get(id);
		if(bmp == null) {
			bmp = BitmapFactory.decodeResource(getContext().getResources(), id);
			BMP_CACHE.put(id, bmp);
		}
		return bmp;
	}
	
	@Override
	public final void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(width > 0 && height > 0) {
			this.width = width;
			this.height = height;
        	needInit = true;
		}
	}

	@Override
	public final void surfaceCreated(SurfaceHolder holder) {
		run = true;
		gameLoopThread.start();
	}

	@Override
	public final void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		run = false;
		
		while (retry) {
			try {
				gameLoopThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}
		BMP_CACHE.clear();
		unregisterListener();
		finish();
	}
	
	@Override
	public final void run() {
		Canvas c = null;
		long t0, tDiff = 0, sleep;
		int framesSkipped;
		boolean initialized = false;

		while (run) {
			if(needInit) {
				init(width, height);
				needInit = false;
				initialized = true;
			}
			if(!initialized) {
				try {
					// save some batt.
					Thread.sleep(MILLIS_PER_FRAME);
				} catch(InterruptedException e) { /* ignore */ }
			} else {
				t0 = SystemClock.uptimeMillis();
				framesSkipped = 0;
				// timeouts
				for(int i=0; i<timeout.length; i++) {
					if(timeout[i] != 0 && t0 > timeout[i]) {
						onTimeout(i);
						timeout[i] = 0;
					}
				}
				// State
				updateState();
				try {
					c = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder) {
						// GFX
						updateCanvas(c);
					}
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						surfaceHolder.unlockCanvasAndPost(c);
					}
				}
				// Sleep
				tDiff = SystemClock.uptimeMillis() - t0;
				sleep = MILLIS_PER_FRAME - tDiff;
				if(sleep > 0) {
					try {
						// save some batt.
						Thread.sleep(sleep);
					} catch(InterruptedException e) { /* ignore */ }
				}
				while (sleep < 0 && framesSkipped < MAX_FRAME_SKIP) {
					// update without render
					updateState();
					// add frame period to check if in next frame
					sleep += MILLIS_PER_FRAME;
					framesSkipped++;
				}
			}
		}
	}
	
	@Override
    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // get new screen dimensions.
        if(w > 0 && h > 0 && oldw > 0 && oldh > 0 && w != oldw && h != oldh) {
    		this.width = w;
    		this.height = h;
        	needInit = true;
        }
    }


    @Override
    public final boolean onKeyDown(int keyCode, KeyEvent event) {
        // quit application if user presses the back key.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	unregisterListener();
        	finish();
        }
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    	if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	        // grab the values required to respond to user movement.
    		onSensor(event.values);
    	}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // currently not used
    }

    /**
     * Register the accelerometer sensor so we can use it in-game.
     */
    public void registerListener() {
    	if(enableSensors) {
    		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    	}
    }

    /**
     * Unregister the accelerometer sensor otherwise it will continue to operate
     * and report values.
     */
    public void unregisterListener() {
    	if(enableSensors) {
    		mSensorManager.unregisterListener(this);
    	}
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	switch(event.getAction()) {
    	case MotionEvent.ACTION_DOWN:
    		onTouch(true, event.getX(), event.getY());
    		break;
    	case MotionEvent.ACTION_UP:
    		onTouch(false, event.getX(), event.getY());
    		break;
    	}
    	return true;
    }

    /**
     * Schedules a call to the onTimeout function to be executed in wait milliseconds.
     * 
     * @param wait waiting milliseconds before execute the call with the id
     * 
     * @return timeoutId id of the timeout
     */
    public int setTimeout(long wait) {
    	for(int i=0; i<timeout.length; i++) {
    		if(timeout[i] == 0) {
    			timeout[i] = SystemClock.uptimeMillis() + wait;
    			return i;
    		}
    	}
    	return -1;
    }
    
    public void clearTimeout(int timeoutId) {
    	timeout[timeoutId] = 0;
    }
}
