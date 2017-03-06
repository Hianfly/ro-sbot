package com.hiandev.rosbot.scanner;

public class Cell {
	
	public static final int SIZE = 5;

	public final int _x;
	public final int _y;
	public final Integer distance;
	public final int[][] pixels;
	
	public Cell(Cell cell) {
		this._x     = cell._x;
		this._y     = cell._y;
		this.pixels = new int[SIZE][SIZE * 3];
		for (int r = 0; r < this.pixels.length; r++) {
			for (int c = 0; c < this.pixels[r].length; c++) {
				this.pixels[r][c] = cell.pixels[r][c];
			}
		}
		this.distance = new Integer(cell.distance.intValue());
	}
	
	public Cell(int _x, int _y, int[] samples, int distance) {
		this._x       = _x;
		this._y       = _y;
		this.distance = new Integer(distance);
		this.pixels   = new int[SIZE][SIZE * 3];
		int  index    = 0;
		for (int x = 0; x < pixels.length; x += 1) {
			for (int y = 0; y < pixels[x].length; y += 3) {
    			pixels[x][y + 0] = samples[index++];
    			pixels[x][y + 1] = samples[index++];
    			pixels[x][y + 2] = samples[index++];
    		}
		}
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
	
}
