package com.hiandev.rosbot;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.HashMap;

public class Scanner {

    public final int _x;
    public final int _y;
    public final int _w;
    public final int _h;
    
    public Scanner(int x, int y, int w, int h) throws AWTException {
    	this._x = x;
    	this._y = y;
    	this._w = w;
    	this._h = h;
    	this.robot = new Robot();
		this.cellCols = w / cellSize;
		this.cellRows = h / cellSize;
		this.cellData = new int[cellCols][cellRows][];
		this.cellPixelSize = cellSize * cellSize * 3;
		this.cellDead = new int[cellCols][cellRows];
    }

    /*
     * 
     * 
     * 
     */

	public long interval = 25;
	public boolean running = false;
	public void start() {
		preapare();
		mListener.onStart();
		running = true;
		while (running) {
	    	cellDiff = new ArrayList<>();
			mListener.onPreExecute();
			screenRaster = captureScreen();
		    convertScreenToCell();
			mListener.onPostExecute();
		    sleep(interval);
		}
		mListener.onFinish();
	}
    private void preapare() {
		prepareCellDead();
    }
    private void prepareCellDead() {
    	int qw   = _w / 2;
		int qh   = _h / 2;
    	int bgnX = qw - 40;
		int endX = qw + 30;
		int bgnY = qh - 90;
		int endY = qh + 10 + 1;
    	for (int x = bgnX; x < endX; x++) {
    		for (int y = bgnY; y < endY; y++) {
    	    	cellDead[x / cellSize][y / cellSize] = 1;
    		}
    	}
    }
	
    /*
     * 
     * 
     * 
     */

    private int[][][] cellData = null;
    private int[][] cellDead = null;
	private ArrayList<int[]> cellDiff = new ArrayList<>();
	private int cellSize = 10;
    private int cellCols = 0;
    private int cellRows = 0;
    private int cellPixelSize = 0;
    private int cellChangeThreshold = 10;
	private int cellShrinkThreshold = 1; 
    private int cellMatchThreshold  = 10;
	private boolean isCellDataChanged(int x, int y, int[] samples) {
		boolean b = false;
		int[] current = cellData[x][y];
		for (int i = 0; i < current.length; i += 3) {
			if (Math.abs(current[i + 0] - samples[i + 0]) > cellChangeThreshold || 
				Math.abs(current[i + 1] - samples[i + 1]) > cellChangeThreshold || 
				Math.abs(current[i + 2] - samples[i + 2]) > cellChangeThreshold) {
				b = true;
				break;
			}
		}
		return b;
	}
    private void convertScreenToCell() {
		for (int x = 0; x < _w; x  += cellSize) {
			for (int y = 0; y < _h; y += cellSize) {
				convertScreenToCell(x, y);
			}
		}
    }
	private void convertScreenToCell(int x, int y) {
		int cellX = x / cellSize;
		int cellY = y / cellSize;
		if (cellDead[cellX][cellY] == 1) {
			return;
		}
		if (cellData[cellX][cellY] == null) {
			cellData[cellX][cellY] = shrinkCellData(screenRaster.getPixels(x, y, cellSize, cellSize, new int[cellPixelSize]));
		}
		else {
			int[] cellSample = shrinkCellData(screenRaster.getPixels(x, y, cellSize, cellSize, new int[cellPixelSize]));
			if (isCellDataChanged(cellX, cellY, cellSample)) {
				cellDiff.add(new int[] { x, y, cellSize, cellSize });
			}
			cellData[cellX][cellY] = cellSample;
		}
	}
	public int[] shrinkCellData(int[] samples) {
		return shrinkCellData(samples, cellShrinkThreshold);
	}
	public int[] shrinkCellData(int[] samples, int shrinkThreshold) {
		for (int x = 0; x < samples.length; x++) {
			samples[x] = (samples[x] / shrinkThreshold) * shrinkThreshold;
		}
		return samples;
	}
    public boolean isMatch(int[][] testData, int[] cellData, int cellSize) {
		return isMatch(testData, cellData, cellSize, cellMatchThreshold);
	}
	public boolean isMatch(int[][] testData, int[] cellData, int cellSize, int matchThreshold) {
		boolean m = false;
		try {
			P : for (int x = 0; x <= cellData.length - testData[0].length; x += 3) {
				m = true;
				for (int i = 0; i < testData.length; i++) {
					int xProjection = x + (cellSize * 3 * i);
					for (int j = 0; j < testData[i].length; j++) {
						if (testData[i][j] == -1) {
							continue;
						}
						if (Math.abs(cellData[xProjection + j] - testData[i][j]) > matchThreshold) {
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
	public int getCellRows() {
		return cellRows;
	}
	public int getCellCols() {
		return cellCols;
	}
	public ArrayList<int[]> getCellDiff() {
		return cellDiff;
	}
	public ArrayList<int[]> getCellDiffByDistance(int limit) {
		HashMap<Integer, ArrayList<int[]>> map = new HashMap<>();
		int midX = ((_w / 2) / cellSize) - 1;
		int midY = ((_h / 2) / cellSize) - 4;
		for (int[] cell : cellDiff) {
			int dist = Math.abs(midX - (cell[0] / cellSize)) + Math.abs(midY - (cell[1] / cellSize));
			ArrayList<int[]> sorted = map.get(dist);
			if (sorted == null) {
				map.put(dist, sorted = new ArrayList<>());
			}
			sorted.add(cell);
		}
		ArrayList<int[]> result = new ArrayList<>();
		for (int x = 0; (limit >= 0 && x < limit) && x < midX + midY; x++) {
			ArrayList<int[]> sorted = map.get(x);
			if (sorted != null) {
				result.addAll(sorted);
			}
		}
		return result;
	}
    
	/*
	 * 
	 * 
	 * 
	 */

	private Robot robot = null;
	private Raster screenRaster = null;
	public Raster getScreenRaster() {
		return screenRaster;
	}
    public Raster captureScreen() {
	    return robot.createScreenCapture(new Rectangle(_x, _y, _w, _h)).getData();
    }
	public void mouseIdle() {
    	robot.mouseMove(_x + 1, _y + 1);
    }
	public void mouseClick() {
    	robot.mousePress(InputEvent.BUTTON1_MASK);
    	sleep(20);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
	public void mouseGoto(int x, int y) {
    	robot.mouseMove(x, y);
    }
    
    /*
     * 
     * 
     * 
     */
    
    private void sleep(long time) {
    	try {
	    	Thread.sleep(time);
	    } catch (Exception e) {
	    }
    }
    
    private ScannerListener mListener = null;
    public void setScannerListener(ScannerListener listener) {
    	mListener = listener;
    }
    public static interface ScannerListener {

    	public void onStart();
    	public void onPreExecute();
    	public void onPostExecute();
    	public void onFinish();
    	
    }
    
}
