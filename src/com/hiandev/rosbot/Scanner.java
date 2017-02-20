package com.hiandev.rosbot;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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

	public long interval = 100;
	public boolean running = false;
	public void start() {
		showCellFrame();
	    sleep(1000);
		running = true;
		while (running) {
			beginTime();
			beginCell();
			captureScreen();
		    convertScreenToCell();
		    endCell();
		    detectCharMode();
		    endTime();
		    sleep(interval);
		}
	}

    public Scanner preapare() {
    	int _w = cellFrame.getWidth();
    	int _h = cellFrame.getHeight();
    	int qw = _w / 2;
		int qh = _h / 2;
    	
		int bgnX = qw - 40;
		int endX = qw + 30;
		int bgnY = qh - 90;
		int endY = qh + 10 + 1;
    	for (int x = bgnX; x < endX; x++) {
    		for (int y = bgnY; y < endY; y++) {
    	    	cellDead[x / cellSize][y / cellSize] = 1;
    		}
    	}
    	
		this.cellQuadrans[0] = new int[] { qw, qh,  0,  0 };
		this.cellQuadrans[1] = new int[] { qw, qh, _w,  0 };
		this.cellQuadrans[2] = new int[] { qw, qh, _w, _h };
		this.cellQuadrans[3] = new int[] { qw, qh,  0, _h };
    	return this;
    }
	
    /*
     * 
     * 
     * 
     */
	
	private Robot robot = null;
	
	/*
	 * 
	 * 
	 * 
	 */


	public static final int CHAR_MODE_IDLE = 0;
	public static final int CHAR_MODE_TARGETING = 1;
	public static final int CHAR_MODE_ATTACKING = 2;
	private int charMode = -1;
	private int[][] modeTargeting = new int[][] { 
		{  0, 0, 0,   0,   0,   0,   0,   0,   0,   0,   0,   0 },
		{  0, 0, 0, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
		{  0, 0, 0, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
		{  0, 0, 0, 250, 250, 250, 250, 250, 250, 250, 250, 250 }
	};
	private int[][] modeAttacking = new int[][] { 
		{  0, 0, 0,   0,   0,   0,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  0, 0, 0, 160, 190, 220,   0,   0,   0,  -1,  -1,  -1 },
		{  0, 0, 0, 160, 190, 220,  -1,  -1,  -1,   0,   0,   0 },
		{  0, 0, 0, 160, 190, 220,  -1,  -1,  -1,  -1,  -1,  -1 }
	};
	long lastDetectionTime = 0;
	long lastAttackingTime = 0;
	long lastTargetingTime = 0;
	long idleSignal = 0;
	private void detectCharMode() {
    	Point point = MouseInfo.getPointerInfo().getLocation();
    	try {
	    	int mx = (int) point.getX() - cellFrame.getScreenX();
	    	int my = (int) point.getY() - cellFrame.getScreenY();
	    	if (mx > cellFrame.getWidth() || my > cellFrame.getHeight()) {
	    		return;
	    	}
	    	int[] pixels = shrinkCellData(screenRaster.getPixels(mx + 1, my + 1, 4, 4, new int[4 * 4 * 3]), 10);
	    	final 
	    	int newMode = isMatch(modeTargeting, pixels, 4, 20) ? CHAR_MODE_TARGETING : 
	    		          isMatch(modeAttacking, pixels, 4, 20) ? CHAR_MODE_ATTACKING : 
	    		          CHAR_MODE_IDLE;
	    	final 
	    	int oldMode = charMode;
	    	long now = System.currentTimeMillis();
	    	if (now - lastDetectionTime < 500) {
	    		return;
	    	}
	    	lastDetectionTime = now;
	    	if (oldMode == CHAR_MODE_IDLE && newMode == CHAR_MODE_ATTACKING) {
	    		return;
	    	}
	    	/*
	    	 * 
	    	 * 
	    	 * 
	    	 */
	    	System.out.println(oldMode + " : " + newMode + " > " + idleSignal);
	    	if (newMode == CHAR_MODE_TARGETING) {
		    	charMode = newMode;
	    		mScannerListener.onTargeting(Scanner.this, new int[] { mx, my, cellSize, cellSize });
	    	}
	    	if (newMode == CHAR_MODE_IDLE) {
	    		if (idleSignal < 3) {
	    			if (oldMode == CHAR_MODE_TARGETING || oldMode == CHAR_MODE_IDLE) {
		    			saveMode();
		    			idleSignal = 3;
	    			}
	    			else {
		    			saveMode();
		    			idleSignal++;
	    			}
	    		}
	    		else {
	    			idleSignal = 0;
		    		charMode = newMode;
		    		mScannerListener.onIdle(Scanner.this);
	    		}
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
    
	
    public void attack(int[] cellData) {
		robot.mouseMove(cellData[0] + cellFrame.getScreenX() + 1, cellData[1] + cellFrame.getScreenY() + 1);
		try { Thread.sleep(20); } catch (Exception e) { }
		robot.mousePress(InputEvent.BUTTON1_MASK);
		try { Thread.sleep(20); } catch (Exception e) { }
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		charMode = CHAR_MODE_ATTACKING;
		lastAttackingTime = System.currentTimeMillis();
    }
    public void saveMode() {
    	robot.mouseMove(cellFrame.getScreenX() + 1, cellFrame.getScreenY() + 1);
		try { Thread.sleep(20); } catch (Exception e) { }
    }
    public void target(int[] cellData) {
    	robot.mouseMove(cellData[0] + cellFrame.getScreenX() + 1, cellData[1] + cellFrame.getScreenY() + 1);
    }
    public void randomMove() {
    	int x = new Random().nextInt(cellFrame.getWidth());
    	int y = new Random().nextInt(cellFrame.getHeight());
    	robot.mouseMove(x + cellFrame.getScreenX() + 1, y + cellFrame.getScreenY() + 1);try { Thread.sleep(20); } catch (Exception e) { }
		robot.mousePress(InputEvent.BUTTON1_MASK);
		try { Thread.sleep(20); } catch (Exception e) { }
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    /*
     * 
     * 
     * 
     */

    private CellFrame cellFrame = null;
    private int[][][] cellData = null;
    private int[][] cellDead = null;
	private ArrayList<int[]> cellChangedData = new ArrayList<>();
	private int[] cellTargetData = null;
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
    	if (charMode == CHAR_MODE_IDLE) {
    		cellFrame.updateCells(cellChangedData, cellTargetData);
    	}
    	else {
    		cellFrame.updateCells(null, null);
    	}
    }
    private void showCellFrame() {
    	cellFrame.show();
    }
	private boolean isCellDataChanged(int x, int y, int[] samples) {
		boolean b = false;
		int[] current = cellData[x][y];
		for (int i = 0; i < current.length; i += 3) {
			if (Math.abs(current[i + 0] - samples[i + 0]) > shrinkThreshold || 
				Math.abs(current[i + 1] - samples[i + 1]) > shrinkThreshold || 
				Math.abs(current[i + 2] - samples[i + 2]) > shrinkThreshold) {
				b = true;
				break;
			}
		}
		return b;
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
    }
	private void convertScreenToCell(int x, int y) {
		int cellX = x / cellSize;
		int cellY = y / cellSize;
		if (cellDead[cellX][cellY] == 1) {
			return;
		}
		if (cellData[cellX][cellY] == null) {
			cellData[cellX][cellY] = shrinkCellData(screenRaster.getPixels(x, y, cellSize, cellSize, new int[cellSampleSize]));
		}
		else {
			int[] cellSample = shrinkCellData(screenRaster.getPixels(x, y, cellSize, cellSize, new int[cellSampleSize]));
			if (isCellDataChanged(cellX, cellY, cellSample)) {
				cellChangedData.add(new int[] { x, y, cellSize, cellSize });
				cellChangedCounter++;
			}
			cellData[cellX][cellY] = cellSample;
		}
	}
	private int shrinkThreshold = 10; 
	private int[] shrinkCellData(int[] samples) {
		return shrinkCellData(samples, shrinkThreshold);
	}
	private int[] shrinkCellData(int[] samples, int shrinkThreshold) {
		for (int x = 0; x < samples.length; x++) {
			samples[x] = (samples[x] / shrinkThreshold) * shrinkThreshold;
		}
		return samples;
	}
    private int matchThreshold = 10;
	private boolean isMatch(int[][] testData, int[] cellData, int cellSize) {
		return isMatch(testData, cellData, cellSize, matchThreshold);
	}
	private boolean isMatch(int[][] testData, int[] cellData, int cellSize, int matchThreshold) {
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
	public ArrayList<int[]> getCellChangedDataByDistance() {
		ArrayList<int[]> result = new ArrayList<>();
		HashMap<Integer, ArrayList<int[]>> map = new HashMap<>();
		int midX = ((cellFrame.getWidth()  / 2) / cellSize);
		int midY = ((cellFrame.getHeight() / 2) / cellSize);
		
		for (int x = 0; x < cellChangedData.size(); x++) {
			int[] data = cellChangedData.get(x);
			int curX = (data[0] - cellFrame.getScreenX()) / cellSize;
			int curY = (data[1] - cellFrame.getScreenY()) / cellSize;
			int dist = Math.abs(midX - curX) + Math.abs(midY - curY);
			ArrayList<int[]> sorted = map.get(dist);
			if (sorted == null) {
				map.put(dist, sorted = new ArrayList<>());
			}
			sorted.add(data);
		}
		for (int x = 0; x < midX + midY; x++) {
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

	private Raster screenRaster = null;
    private void captureScreen() {
    	Rectangle rect = new Rectangle(cellFrame.getScreenX(), cellFrame.getScreenY(), cellFrame.getWidth(), cellFrame.getHeight());
	    BufferedImage bi = robot.createScreenCapture(rect);
	    screenRaster = bi.getData();
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
    	Point point = MouseInfo.getPointerInfo().getLocation();
    	T1 = System.currentTimeMillis();
//		System.out.println(" MM:" + charMode + " T:" + (T1 - T0) + "ms --> CellChangedCounter:" + cellChangedCounter + " --> Mouse:" + (point.getX() - cellFrame.getScreenX()) + ":" + (point.getY() - cellFrame.getScreenY()));
    }
	
    private void sleep(long time) {
    	try {
	    	Thread.sleep(time);
	    } catch (Exception e) {
	    }
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

//	private long time = 0;
//	private void doAction() {
//		if (cellChangedData.size() == 0) {
//			return;
//		}
//		long now = System.currentTimeMillis();
//		if (now - time < 10000) {
//			return;
//		}
//		time = now;
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				int pick = new Random().nextInt(cellChangedData.size());
//				int[] data = cellChangedData.get(pick);
//				robot.mouseMove(data[0] + cellFrame.getScreenX(), data[1] + cellFrame.getScreenY());
//				try {
//					Thread.sleep(100);
//				} catch (Exception e) {
//				}
//				robot.mousePress(InputEvent.BUTTON1_MASK);
//				try {
//					Thread.sleep(100);
//				} catch (Exception e) {
//				}
//				robot.mouseRelease(InputEvent.BUTTON1_MASK);
//				try {
//					Thread.sleep(100);
//				} catch (Exception e) {
//				}
//				robot.mouseMove(cellFrame.getScreenX() - 1, cellFrame.getScreenY() - 1);
//				try {
//					Thread.sleep(5000);
//				} catch (Exception e) {
//				}
//			}
//		}).start();
//	}
    
    private ScannerListener mScannerListener = null;
    public void setScannerListener(ScannerListener listener) {
    	mScannerListener = listener;
    }
    public static interface ScannerListener {

    	public void onIdle(Scanner scanner);
    	public void onTargeting(Scanner scanner, int[] data);
    	
    }
    
    
}
