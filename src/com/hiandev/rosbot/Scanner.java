package com.hiandev.rosbot;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import com.hiandev.rosbot.ui.CellFrame;

public class Scanner {
	
    public Scanner(CellFrame cellFrame) throws AWTException {
    	this.robot = new Robot();
    	this.cellFrame = cellFrame;
		this.cellCols = cellFrame.getWidth()  / cellSize;
		this.cellRows = cellFrame.getHeight() / cellSize;
		this.cellData = new int[cellCols][cellRows][];
		this.cellSampleSize = cellSize * cellSize * 3;
		this.cellDead = new int[cellCols][cellRows];
		this.cellQuadrans = new int[4][];
    }

    public Scanner preapare() {
    	int _w = cellFrame.getWidth();
    	int _h = cellFrame.getHeight();
    	int qw = _w / 2;
		int qh = _h / 2;
    	
		int bgnX = qw - 30;
		int endX = qw + 30;
		int bgnY = qh - 80;
		int endY = qh + 10 + 1;
    	for (int x = bgnX; x < endX; x++) {
    		for (int y = bgnY; y < endY; y++) {
    	    	cellDead[x / cellSize][y / cellSize] = 1;
    		}
    	}
    	
		this.cellQuadrans[0] = new int[] { qw - 1, qh - 1,  0,  0 };
		this.cellQuadrans[1] = new int[] { qw - 0, qh - 1, _w,  0 };
		this.cellQuadrans[2] = new int[] { qw - 0, qh - 0, _w, _h };
		this.cellQuadrans[3] = new int[] { qw - 1, qh - 0,  0, _h };
    	return this;
    }
    
	public void start() {
		showCellFrame();
	    sleep(1000);
		running = true;
		while (running) {
			beginTime();
			beginCell();
			{
			    captureScreen();
			    convertScreenToCell();
			}
		    endCell();
		    endTime();
		    sleep(interval);
		}
	}
    
    /*
     * 
     * 
     * 
     */
	
	private Robot robot = null;
	public boolean running = false;
	public long interval = 100;
    
    /*
     * 
     * 
     * 
     */

    private CellFrame cellFrame = null;
    private int[][][] cellData = null;
    private int[][] cellDead = null;
	private ArrayList<int[]> cellChangedData = new ArrayList<>();
	private int cellSize = 10;
    private int cellCols = 0;
    private int cellRows = 0;
    private int cellSampleSize = 0;
    private long cellChangedCounter = 0;
    private int[][] cellQuadrans = null;
    private void beginCell() {
    	cellChangedCounter = 0;
    	cellChangedData = new ArrayList<>();
		cellFrame.clearCells(5);
    }
    private void endCell() {
	    cellFrame.updateCells(cellChangedData);
    }
    private void showCellFrame() {
    	cellFrame.show();
    }
	private boolean isCellDataChanged(int x, int y, int[] samples) {
		boolean b = false;
		int[] current = cellData[x][y];
		for (int i = 0; i < current.length; i += 3) {
			if (Math.abs(current[i + 0] - samples[i + 0]) > 16 || 
				Math.abs(current[i + 1] - samples[i + 1]) > 16 || 
				Math.abs(current[i + 2] - samples[i + 2]) > 16) {
				b = true;
				break;
			}
		}
		return b;
	}
    
	/*
	 * 
	 * 
	 * 
	 */

	private Raster screenRaster = null;
    private void captureScreen() {
    	Rectangle rect = new Rectangle(cellFrame.getScreenX(), cellFrame.getScreenY(), cellFrame.getWidth(), cellFrame.getHeight());
	    BufferedImage bi = robot.createScreenCapture(rect);
	    screenRaster = bi.getData();
    }
    private void convertScreenToCell() {
    	int[] q = null;
    	for (int i = 0; i < cellQuadrans.length; i++) {
    		q = cellQuadrans[i];
    		switch (i) {
			case 0:
				for (int x = q[0] - cellSize; x >= q[2]; x -= cellSize) {
					for (int y = q[1] - cellSize; y >= q[3]; y -= cellSize) {
						convertScreenToCell(x, y);
					}
				}
				break;
			case 1:
				for (int x = q[0]; x < q[2]; x += cellSize) {
					for (int y = q[1] - cellSize; y >= q[3]; y -= cellSize) {
						convertScreenToCell(x, y);
					}
				}
				break;
			case 2:
				for (int x = q[0]; x < q[2]; x += cellSize) {
					for (int y = q[1]; y < q[3]; y += cellSize) {
						convertScreenToCell(x, y);
					}
				}
				break;
			case 3:
				for (int x = q[0] - cellSize; x >= q[2]; x -= cellSize) {
					for (int y = q[1]; y < q[3]; y += cellSize) {
						convertScreenToCell(x, y);
					}
				}
				break;
			}
    	}
//    	for (int x = 0; x < screenRaster.getWidth(); x += cellSize) { 
//			for (int y = 0; y < screenRaster.getHeight(); y += cellSize) {
//	    		int cellX = x / cellSize;
//	    		int cellY = y / cellSize;
//				if (cellData[cellX][cellY] == null) {
//					cellData[cellX][cellY] = screenRaster.getPixels(x, y, cellSize, cellSize, new int[cellSampleSize]);
//				}
//				else {
//					int[] cellSample = screenRaster.getPixels(x, y, cellSize, cellSize, new int[cellSampleSize]);
//					if (isCellDataChanged(cellX, cellY, cellSample)) {
//						cellChangedData.add(new int[] { x, y, cellSize, cellSize });
//						cellChangedCounter++;
//					}
//					cellData[cellX][cellY] = cellSample;
//				}
//			}
//		}
    }
	private void convertScreenToCell(int x, int y) {
		int cellX = x / cellSize;
		int cellY = y / cellSize;
		if (cellDead[cellX][cellY] == 1) {
			return;
		}
		if (cellData[cellX][cellY] == null) {
			cellData[cellX][cellY] = screenRaster.getPixels(x, y, cellSize, cellSize, new int[cellSampleSize]);
		}
		else {
			int[] cellSample = screenRaster.getPixels(x, y, cellSize, cellSize, new int[cellSampleSize]);
			if (isCellDataChanged(cellX, cellY, cellSample)) {
				cellChangedData.add(new int[] { x, y, cellSize, cellSize });
				cellChangedCounter++;
			}
			cellData[cellX][cellY] = cellSample;
		}
	}
    
    /*
     * 
     * 
     * 
     */
    
    private long T0 = 0l;
    private long T1 = 0l;
    private void beginTime() {
    	T0 = System.currentTimeMillis();
    }
    private void endTime() {
    	T1 = System.currentTimeMillis();
		System.out.println("T:" + (T1 - T0) + "ms --> CellChangedCounter:" + cellChangedCounter);
    }

//	robot.mouseMove(x, y);
//	try {
//		Thread.sleep(100);
//	} catch (Exception e) {
//	}
//	robot.mousePress  (InputEvent.BUTTON1_MASK);
//	try {
//		Thread.sleep(100);
//	} catch (Exception e) {
//	}
//	robot.mouseRelease(InputEvent.BUTTON1_MASK);
//	try {
//		Thread.sleep(5000);
//	} catch (Exception e) {
//	}
	
    private void sleep(long time) {
    	try {
	    	Thread.sleep(time);
	    } catch (Exception e) {
	    }
    }
	
}
