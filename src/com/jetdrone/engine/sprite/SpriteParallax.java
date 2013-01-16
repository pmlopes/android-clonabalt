package com.jetdrone.engine.sprite;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class SpriteParallax extends ISprite {
	
	private final Bitmap bitmap;
	private final int repeat;

	/**
	 * Creates a bitmap backed sprite
	 * 
	 */
	public SpriteParallax(Bitmap bitmap, int bgWidth) {
		super(bitmap.getWidth(), bitmap.getHeight());
		this.bitmap = bitmap;
		if(width >= bgWidth) {
			repeat = 2;
		} else {
			repeat = (int) (0.5f + ((float) width / bgWidth)) + 1;
		}
	}

	@Override
	public void draw(Canvas canvas, Rect canvasDimensions) {
		if(enabled) {
			// normalize left
			left = (left % width);
			for(int i=0; i<repeat; i++) {
				final int tLeft = left + i * width;
				if (canvasDimensions.intersects(tLeft, top, tLeft + width, top + height)) {
					canvas.drawBitmap(bitmap, tLeft, top, null);
				}
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if(enabled) {
			// normalize left
			left = (left % width);
			for(int i=0; i<repeat; i++) {
				final int tLeft = left + i * width;
				canvas.drawBitmap(bitmap, tLeft, top, null);
			}
		}
	}
}
