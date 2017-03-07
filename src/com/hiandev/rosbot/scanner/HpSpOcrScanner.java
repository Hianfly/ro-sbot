package com.hiandev.rosbot.scanner;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class HpSpOcrScanner extends Scanner {

    public HpSpOcrScanner(int _x, int _y) throws AWTException {
    	super (_x, _y, 200, 35);
    	setInterval(1000);
    }
    
    /*
     * 
     * 
     * 
     */

    public int[][] charMatrix = null;
    private int[][] pixels = null;
    
    
    @Override
	protected void onExecute() {
		super.onExecute();
		try {
			/*
			 * 
			 * 
			 */
			pixels = new int[_h][_w * 3];
			for (int y = 0; y < _h; y++) {
				pixels[y] = floorPixels(getScreenImage().getRaster().getPixels(0, y, _w, 1, (int[]) null), 10);
			}
			/*
			 * 
			 * 
			 */
			int[] color = new int[] { 0, 0, 0 };
			for (int y = 0; y < pixels.length; y++) {
				boolean line = true;
				L : for (int x = 0; x < pixels[y].length; x += 3) {
					if (Math.abs(pixels[y][x + 0] - color[0]) <= 10 && 
						Math.abs(pixels[y][x + 1] - color[1]) <= 10 && 
						Math.abs(pixels[y][x + 2] - color[2]) <= 10) {
						line = false;
						break L;
					}
				}
				if (line) {
					for (int x = 0; x < pixels[y].length; x += 3) {
						pixels[y][x + 0] = 255;
						pixels[y][x + 1] =   0;
						pixels[y][x + 2] =   0;
					}
				}
			}
			
			for (int z = 0; z < pixels.length; z++) {
				if (Pixel.isMatch(pixels[z], 0, 255, 0, 0)) {
					continue;
				}
				for (int x = 0; x < pixels[z].length; x += 3) {
					boolean line = true;
					int     rows = 0;
					for (int y = z; y < pixels.length; y += 1) {
						if (Pixel.isMatch(pixels[y], x, 255, 0, 0)) {
							break;
						}
						rows++;
						if (Math.abs(pixels[y][x + 0] - color[0]) <= 10 && 
							Math.abs(pixels[y][x + 1] - color[1]) <= 10 && 
							Math.abs(pixels[y][x + 2] - color[2]) <= 10) {
							line = false;
							break ;
						}
					}
					if (line) {
						for (int y = z; y < x + rows; y += 1) {
							pixels[y][x + 0] = 255;
							pixels[y][x + 1] =   0;
							pixels[y][x + 2] =   0;
						}
					}	
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private int mHpPercentage = 100;
    public final int getHpPercentage() {
    	return mHpPercentage;
    }
    
    
    
    public BufferedImage createImage() {
    	return toBufferedImage(pixels);
    }
    
}
