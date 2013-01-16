package com.jetdrone.engine.sprite;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class SpritePaint extends ISprite {

	private final Paint paint;
	
	/**
	 * Creates a Paint backed sprite with a specific dimension
	 * 
	 * @param paint
	 * @param width
	 * @param height
	 */
	public SpritePaint(Paint paint, int width, int height) {
		super(width, height);
		this.paint = paint;
	}
	
	@Override
	public void draw(Canvas canvas, Rect canvasDimensions) {
		if(enabled) {
			if(canvasDimensions.intersects(left, top, left + width, top + height)) {
				canvas.drawRect(left, top, left + width, top + height, paint);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if(enabled) {
			canvas.drawRect(left, top, left + width, top + height, paint);
		}
	}
}
