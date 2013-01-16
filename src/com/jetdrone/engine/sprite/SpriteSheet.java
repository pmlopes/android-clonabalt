package com.jetdrone.engine.sprite;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class SpriteSheet extends ISprite {
	
	private final Bitmap bitmap;
	
	private final Rect src = new Rect();
	private final Rect dst = new Rect();
	
	/**
	 * Creates a bitmap backed sprite
	 * 
	 */
	public SpriteSheet(Bitmap bitmap) {
		super(bitmap.getWidth(), bitmap.getHeight());
		this.bitmap = bitmap;
	}

	/**
	 * Set active sprite
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void setFrame(int x, int y, int width, int height) {
		src.set(x * width, y * height, (x + 1) * width, (y + 1) * height);
		dst.set(left, top, left + width, top + height);
	}
	
	@Override
	public void draw(Canvas canvas, Rect canvasDimensions) {
		if(enabled) {
			if(canvasDimensions.intersects(dst.left, dst.top, dst.right, dst.bottom)) {
				canvas.drawBitmap(bitmap, src, dst, null);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if(enabled) {
			canvas.drawBitmap(bitmap, src, dst, null);
		}
	}
}
