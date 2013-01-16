package com.jetdrone.engine.sprite;

import android.graphics.Canvas;
import android.graphics.Rect;

public abstract class ISprite {
	
	public int left;
	public int top;
	
	public final int width;
	public final int height;
	
	public boolean enabled = true;
	
	public ISprite(int w, int h) {
		width = w;
		height = h;
	}
	
	abstract public void draw(Canvas canvas, Rect canvasDimensions);
	
	abstract public void draw(Canvas canvas);
}
