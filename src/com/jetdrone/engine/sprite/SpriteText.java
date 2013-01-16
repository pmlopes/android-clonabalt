package com.jetdrone.engine.sprite;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class SpriteText extends ISprite {
	
	private final Paint paint;
	
	private String text;

	public SpriteText(Typeface font) {
		super(0, 0);
		this.paint = new Paint();
		paint.setTypeface(font);
	}
	
	public SpriteText(Typeface font, int textSize, int color) {
		super(0, 0);
		this.paint = new Paint();
		paint.setTypeface(font);
		paint.setTextSize(textSize);
//		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setColor(color);
//		paint.setStrokeWidth(2);
	}
	
	public SpriteText(Paint paint) {
		super(0, 0);
		this.paint = paint;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public void draw(Canvas canvas, Rect canvasDimensions) {
		if(enabled) {
			if(text != null && canvasDimensions.contains(left, top)) {
				canvas.drawText(text, left, top, paint);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if(enabled) {
			if(text != null) {
				canvas.drawText(text, left, top, paint);
			}
		}
	}
}
