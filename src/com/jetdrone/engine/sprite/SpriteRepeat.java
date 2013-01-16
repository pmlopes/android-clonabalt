package com.jetdrone.engine.sprite;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;

public class SpriteRepeat extends ISprite {

	private final Paint paint;

	/**
	 * Creates a bitmap repeated sprite
	 * 
	 */
	public SpriteRepeat(Bitmap bitmap, int width, int height) {
		super(width, height);
		
		paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		paint.setShader(new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
	}
	
	@Override
	public void draw(Canvas canvas, Rect canvasDimensions) {
		if(enabled) {
			if (canvasDimensions.intersects(left, top, left + width, top + height)) {
				canvas.save();
				canvas.clipRect(left, top, left + width, top + height);
				canvas.translate(left, top);
				canvas.drawPaint(paint);
				canvas.restore();
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if(enabled) {
			canvas.save();
			canvas.clipRect(left, top, left + width, top + height);
			canvas.translate(left, top);
			canvas.drawPaint(paint);
			canvas.restore();
		}
	}
}
