package com.hiandev.rosbot.scanner;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class HpSpOcrScanner extends Scanner {

    public HpSpOcrScanner(int _x, int _y) throws AWTException {
    	super (_x, _y, 170, 30);
    	setInterval(5000);
    }
    
    /*
     * 
     * 
     * 
     */

    public int[][] charMatrix = null;
    private int[][] pixels = null;
    private ArrayList<int[][]> rowsInfo = new ArrayList<>();
    
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
			int[] color = new int[] { 10,10, 10 };
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
						Pixel.setPixel(pixels[y], x, 255, 0, 0);
					}
				}
			}
			/*
			 * 
			 * 
			 */
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
						for (int y = z; y < z + rows; y += 1) {
							Pixel.setPixel(pixels[y], x, 255, 0, 0);
						}
					}	
				}
			}
			/*
			 * 
			 * 
			 */
			for (int y = 0; y < pixels.length; y++) {
				int h = 0;
				int w = 0;
				for (int x = 0; x < pixels[y].length; x += 3) {
					if (Pixel.isMatch(pixels[y], x, 255, 0, 0)) {
						continue;
					}
					w = getCharWidth (x, y);
					h = getCharHeight(x, y);
//					System.out.println("YX: " + y + " " + x + " WH:" + w + " " + h);
					rowsInfo.add(getCharMatrix(x, y, w, h));
					x += (w * 3) - 3;
				}
				rowsInfo.add(new int[][] { { 0, 0, 0 } });
				y += h > 0 ? h - 1 : y;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private int[][] getCharMatrix(int x, int y, int w, int h) {
    	int[][] charMatrix = new int[h][w];
		int ih = 0;
		int iw = 0;
		for (int j = y; j < h; j += 1) {
			iw = 0;
			for (int i = x; i < w * 3; i += 3) {
				if (Pixel.isMatch(pixels[j], i, 255, 0, 0)) {
					break;
				}
				charMatrix[ih][iw + 0] = pixels[j][i + 0];
				charMatrix[ih][iw + 1] = pixels[j][i + 1];
				charMatrix[ih][iw + 2] = pixels[j][i + 2];
				iw++;
			}
			ih++;
		}
		return charMatrix;
    }
    
    private int getCharWidth(int x, int y) {
    	int w = 0;
    	for (int i = x; i < pixels[y].length; i += 3) {
			if (Pixel.isMatch(pixels[y], i, 255, 0, 0)) {
				break;
			}
			w++;
		}
    	return w;
    }
    private int getCharHeight(int x, int y) {
    	int h = 0;
    	for (int j = y; j < pixels.length; j += 1) {
			if (Pixel.isMatch(pixels[j], x, 255, 0, 0)) {
				break;
			}
			h++;
		}
    	return  h;
    }
    
    
    private int mHpPercentage = 100;
    public final int getHpPercentage() {
    	return mHpPercentage;
    }
    
    
    
    public BufferedImage createImage() {
    	return toBufferedImage(pixels);
    }
    
}
