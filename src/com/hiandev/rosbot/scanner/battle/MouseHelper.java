package com.hiandev.rosbot.scanner.battle;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferedImage;
import com.hiandev.rosbot.scanner.Pixel;

public class MouseHelper {

	public static final int[][] ASSET_TARGETING = new int[][] { 
		{  10, 10, 10,  10,  10,  10,  10,  10,  10,  10,  10,  10 },
		{  10, 10, 10, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
		{  10, 10, 10, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
		{  10, 10, 10, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 }
	};
	
	public static final int[][] ASSET_ATTACKING = new int[][] { 
		{  10, 10, 10,  20,  20,  20,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  10, 10, 10, 150, 170, 210,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  10, 10, 10, 150, 170, 210,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  10, 10, 10, 160, 170, 210,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 }
	};
	
	public static final int[][] ASSET_PICKING   = new int[][] { 
		{  230, 240, 250,  230, 240, 250,  -1, -1, -1, -1, -1, -1 },
		{  230, 240, 250,  230, 240, 250,  -1, -1, -1, -1, -1, -1 },
		{  230, 240, 250,  230, 240, 250,  -1, -1, -1, -1, -1, -1 },
		{  230, 240, 250,  230, 240, 250,  -1, -1, -1, -1, -1, -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
	};

	public static final boolean isMatch(int[][] asset, int[] pixels, int cellSize, int matchThreshold) {
		boolean m = false;
		try {
			P : for (int x = 0; x <= pixels.length - asset[0].length; x += 3) {
				m = true;
				for (int i = 0; i < asset.length; i++) {
					int xProjection = x + (cellSize * 3 * i);
					for (int j = 0; j < asset[i].length; j++) {
						if (asset[i][j] == -1) {
							continue;
						}
						if (Math.abs(pixels[xProjection + j] - asset[i][j]) > matchThreshold) {
							m = false;
							break P;
						}
					}
				}
				if (m) {
					break;
				}
			}
		} catch (Exception e) {
			 m = false;
		}
		return m;
	}

	public static final int[] capturePixels(ItemScanner scanner, boolean newRaster, int floorThreshold) {
	  	Point point = MouseInfo.getPointerInfo().getLocation();
	  	int x = (int) point.getX() - scanner._x;
	  	int y = (int) point.getY() - scanner._y;
	  	if (x < 0 || y < 0 || x >= scanner._w || y >= scanner._h) {
	  		return null;
	  	}
	  	int   w =  4;
	  	int   h =  8;
	  	int   f =  1;
	  	int[] r = new int[0];
	  	try {
		  	if (newRaster) {
			  	BufferedImage bi = scanner.captureScreenImage((int) point.getX(), (int) point.getY(), w + f, h + f);
			  	int[]     pixels = bi.getRaster().getPixels(0 + f, 0 + f, w, h, new int[w * h * 3]);
				r = Pixel.floorPixels(pixels, floorThreshold);
			}
		  	else {
			  	BufferedImage bi = scanner.getScreenImage();
			  	int[]     pixels = bi.getRaster().getPixels(x + f, y + f, w, h, new int[w * h * 3]);
				r = Pixel.floorPixels(pixels, floorThreshold);
			}
	  	} catch (Exception e) {
	  		e.printStackTrace();
	  	}
	  	return r;
	}
	
}
