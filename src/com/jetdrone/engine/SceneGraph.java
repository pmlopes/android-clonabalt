package com.jetdrone.engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import com.jetdrone.engine.sprite.ISprite;
import com.jetdrone.engine.util.LList;
import com.jetdrone.engine.util.LList.Node;

public final class SceneGraph {

	private final LList<ISprite> container = new LList<ISprite>();
	private final Rect dimensions = new Rect();

	private int top;
	private int left;

	private int bgColor;

	public void init(int width, int height) {
		container.clear();
		bgColor = Color.BLACK;
		dimensions.set(0, 0, width, height);
	}

	public void clear() {
		container.clear();
		bgColor = Color.BLACK;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public void addFront(ISprite s) {
		// we paint backwards
		container.append(s);
	}

	public void addBack(ISprite s) {
		// we paint backwards
		container.prepend(s);
	}

	public void remove(ISprite s) {
		container.remove(s);
	}

	public void setBackground(int color) {
		bgColor = color;
	}

	public void draw(Canvas c) {
		c.drawColor(bgColor);
		c.save();
		c.translate(-left, -top);
		Node<ISprite> it = container.iterator();
		while(it != null) {
			it.data().draw(c, dimensions);
			it = it.next();
		}
		c.restore();
	}
}
