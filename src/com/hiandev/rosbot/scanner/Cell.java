package com.hiandev.rosbot.scanner;

public class Cell {
	
	public static final int SIZE = 5;

	public final int _cx;
	public final int _cy;
	public final Integer distance;
	public final int[][] pixels;
	
	public Cell(Cell cell) {
		this._cx      = cell._cx;
		this._cy      = cell._cy;
		this.pixels   = new int[SIZE][SIZE * 3];
		this.distance = new Integer(cell.distance.intValue());
		for (int y = 0; y < this.pixels.length; y++) {
			for (int x = 0; x < this.pixels[y].length; x++) {
				this.pixels[y][x] = cell.pixels[y][x];
			}
		}
	}
	
	public Cell(int _cx, int _cy, int[] samples, int distance) {
		this._cx      = _cx;
		this._cy      = _cy;
		this.distance = new Integer(distance);
		this.pixels   = new int[SIZE][SIZE * 3];
		int  index    = 0;
		for (int y = 0; y < pixels.length; y += 1) {
			for (int x = 0; x < pixels[y].length; x += 3) {
    			pixels[y][x + 0] = samples[index++];
    			pixels[y][x + 1] = samples[index++];
    			pixels[y][x + 2] = samples[index++];
    		}
		}
	}
	
	public final int[] toInt1D(int y) {
		int[] int1d = new int[Cell.SIZE * 3];
		int index = 0;
		for (int x = 0; x < pixels[y].length; x += 3) {
			int1d[index++] = pixels[y][x + 0];
			int1d[index++] = pixels[y][x + 1];
			int1d[index++] = pixels[y][x + 2];
		}
		return int1d; 
	}
	
}
