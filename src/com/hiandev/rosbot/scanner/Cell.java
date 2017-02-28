package com.hiandev.rosbot.scanner;

public class Cell {
	
	public static final int SIZE = 5;

	public final int _x;
	public final int _y;
	public final int[]   averagePixels = new int[] { -1, -1, -1 };
	public final int[][] pixels;

	public long averageKey = 0;
	
	public Cell(int _x, int _y, int[] samples) {
		this._x = _x;
		this._y = _y;
		this.pixels = new int[SIZE][SIZE * 3];
		int[] buffer = new int[3];
		int   index  = 0;
		for (int x = 0; x < pixels.length; x += 1) {
			for (int y = 0; y < pixels[x].length; y += 3) {
    			buffer[0] += (pixels[x][y + 0] = samples[index++]);
    			buffer[1] += (pixels[x][y + 1] = samples[index++]);
    			buffer[2] += (pixels[x][y + 2] = samples[index++]);
    		}
		}
		int divider = Cell.SIZE * Cell.SIZE;
		averagePixels[0] = buffer[0] / divider;
		averagePixels[1] = buffer[1] / divider;
		averagePixels[2] = buffer[2] / divider;
	}
	
	public final void createWhitePixels(boolean average) {
		for (int x = 0; x < pixels.length; x += 1) {
			for (int y = 0; y < pixels[x].length; y += 3) {
    			pixels[x][y + 0] = 255;
    			pixels[x][y + 1] = 255;
    			pixels[x][y + 2] = 255;
    		}
		}
		if (average) {
			averagePixels[0] = 255;
			averagePixels[1] = 255;
			averagePixels[2] = 255;
		}
	}
	
	public final void createAveragePixels(boolean changePixels, int prmFloorThreshold) {
		int divider = Cell.SIZE * Cell.SIZE;
		int[] rgb = new int[3];
		for (int i = 0; i < pixels.length; i += 1) {
			for (int j = 0; j < pixels[i].length; j += 3) {
				rgb[0] += pixels[i][j + 0];
				rgb[1] += pixels[i][j + 1];
				rgb[2] += pixels[i][j + 2];
			}
		}
		rgb[0] = ((rgb[0] / divider) / prmFloorThreshold) * prmFloorThreshold;
		rgb[1] = ((rgb[1] / divider) / prmFloorThreshold) * prmFloorThreshold;
		rgb[2] = ((rgb[2] / divider) / prmFloorThreshold) * prmFloorThreshold;
		if (changePixels) {
    		for (int i = 0; i < pixels.length; i += 1) {
    			for (int j = 0; j < pixels[i].length; j += 3) {
    				pixels[i][j + 0] = rgb[0];
    				pixels[i][j + 1] = rgb[1];
    				pixels[i][j + 2] = rgb[2];
    			}
    		}
		}
		averagePixels[0] = rgb[0];
		averagePixels[1] = rgb[1];
		averagePixels[2] = rgb[2];
    }
	
	public final void createAveragePixels(int prmFloorThreshold) {
		int[] buffer = new int[3];
		for (int x = 0; x < pixels.length; x += 1) {
			for (int y = 0; y < pixels[x].length; y += 3) {
				buffer[0] += pixels[x][y + 0];
				buffer[1] += pixels[x][y + 1];
				buffer[2] += pixels[x][y + 2];
    		}
		}
		int divider = Cell.SIZE * Cell.SIZE;
		averagePixels[0] = ((buffer[0] / divider) / prmFloorThreshold) * prmFloorThreshold;
		averagePixels[1] = ((buffer[1] / divider) / prmFloorThreshold) * prmFloorThreshold;
		averagePixels[2] = ((buffer[2] / divider) / prmFloorThreshold) * prmFloorThreshold;
	}
	
	public final int[] toInt1D(int x) {
		int[] int1d = new int[Cell.SIZE * 3];
		int index = 0;
		for (int y = 0; y < pixels[x].length; y += 3) {
			int1d[index++] = pixels[x][y + 0];
			int1d[index++] = pixels[x][y + 1];
			int1d[index++] = pixels[x][y + 2];
		}
		return int1d; 
	}
	
	public static final long createAveragePixelsKey(int[] pixels) {
    	return ((pixels[0] * 1000000l) +  1000000000l) + 
			   ((pixels[1] *    1000l) +     1000000l) +
			   ((pixels[2] *       1l) +        1000l);
    }
}
