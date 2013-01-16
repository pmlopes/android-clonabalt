package com.jetdrone.clonabalt;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;

import com.jetdrone.engine.sprite.ISprite;

public class Building extends ISprite {
	
	private static Paint windows;
	private static Paint buildingTop;
	
	private static int buildingTopHeight;
	
	private Game game;
	private int gap;
	private int totalWidth;
	private boolean endReached;
	private boolean isIn;
	private boolean isOut;
	
	private static void staticInit(Game game) {
		if(windows == null) {
			windows = new Paint(Paint.FILTER_BITMAP_FLAG);
			windows.setColor(0xFFB0B0BF);
		
			Bitmap windowsBmp = game.loadBitmap(R.drawable.windows);
			windows.setShader(new BitmapShader(windowsBmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
		}
		
		if(buildingTop == null) {
			buildingTop = new Paint(Paint.FILTER_BITMAP_FLAG);
			buildingTop.setColor(0xFFB0B0BF);
			
			Bitmap buildingTopBmp = game.loadBitmap(R.drawable.building_top);
			buildingTop.setShader(new BitmapShader(buildingTopBmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
			
			buildingTopHeight = buildingTopBmp.getHeight();
		}
	}
	
	public Building(Game game) {
		super(300 + Game.RAND.nextInt(1000), 30 + Game.RAND.nextInt(100));
		
		staticInit(game);

		this.game = game;

		gap = Math.round(game.speed * 300);
		totalWidth = width + gap;

		left = game.canvasWidth;
		top = game.canvasHeight - height;

		endReached = false;

		isIn = false;
		isOut = false;
	}

	public void move(int distance) {
		left -= distance;

		// Check if this is now the current building
		if (isIn) {
			if (!isOut && left + width < game.x) {
				game.currentBuilding = null;
				game.airborne = true;
				isOut = true;
			}
		} else if (left <= game.x) {
			game.currentBuilding = this;
			isIn = true;
		}
		
		// Check if the end of the building + gap was reached and call
		// an appropriate action (spawn a new building?)
		if (!endReached && (left + totalWidth <= game.canvasWidth)) {
			game.addBuilding(new Building(game));
			endReached = true;
		}

		// If the building leaves the left side of the screen then
		// it has expired and has to be removed
		if (enabled && (totalWidth + left <= 0)) {
			game.removeFirstBuilding();
			enabled = false;
		}
	}

	@Override
	public void draw(Canvas canvas, Rect canvasDimensions) {
		if(enabled) {
			if (canvasDimensions.intersects(left, top, left + width, top + height + game.config.SHAKE_AMPLITUDE)) {
				canvas.save();
				canvas.clipRect(left, top, left + width, top + height + game.config.SHAKE_AMPLITUDE);
				canvas.translate(left, top);
				canvas.drawPaint(windows);
				canvas.restore();
				
				canvas.save();
				canvas.clipRect(left, top, left + width, top + buildingTopHeight);
				canvas.translate(left, top);
				canvas.drawPaint(buildingTop);
				canvas.restore();
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if(enabled) {
			canvas.save();
			canvas.clipRect(left, top, left + width, top + height + game.config.SHAKE_AMPLITUDE);
			canvas.translate(left, top);
			canvas.drawPaint(windows);
			canvas.restore();
			
			canvas.save();
			canvas.clipRect(left, top, left + width, top + buildingTopHeight);
			canvas.translate(left, top);
			canvas.drawPaint(buildingTop);
			canvas.restore();
		}
	}
}
