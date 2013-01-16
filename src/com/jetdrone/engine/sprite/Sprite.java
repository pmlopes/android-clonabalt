package com.jetdrone.engine.sprite;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Sprite extends ISprite {
	
	private final Bitmap bitmap;
	
	/**
	 * Creates a bitmap backed sprite
	 * 
	 */
	public Sprite(Bitmap bitmap) {
		super(bitmap.getWidth(), bitmap.getHeight());
		this.bitmap = bitmap;
	}

	@Override
	public void draw(Canvas canvas, Rect canvasDimensions) {
		if(enabled) {
			if (canvasDimensions.intersects(left, top, left + width, top + height)) {
				canvas.drawBitmap(bitmap, left, top, null);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if(enabled) {
			canvas.drawBitmap(bitmap, left, top, null);
		}
	}
}
