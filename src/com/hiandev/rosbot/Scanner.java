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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

	public long interval = 25;
	public boolean running = false;
	public void start() {
		showCellFrame();
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("WATCHERRR");
				while (true) {
					Set<String> keys = UNTARGETABLE_MAP.keySet();
					for (String key : keys) {
						Integer val = get(key);
						System.out.println(key + " >>>>>> " + val);
					}
					try {
						Thread.sleep(1000 * 30);
					} catch (Exception e) {
					}
				}
			}
		}).start();
	    sleep(1000);
		running = true;
		while (running) {
			beginTime();
			beginCell();
			captureScreen();
		    convertScreenToCell();
		    endCell();
		    endTime();
		    detectCharMode();
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
	private int charMode = 0;
	private int charMove = 0;
	private int[][] modeTargeting = new int[][] { 
		{  10, 10, 10,  10,  10,  10,  10,  10,  10,  10,  10,  10 },
		{  10, 10, 10, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
		{  10, 10, 10, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
		{  10, 10, 10, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 }
	};
	private int[][] modeAttacking = new int[][] { 
		{  10, 10, 10,  20,  20,  20,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  10, 10, 10, 150, 170, 210,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  10, 10, 10, 150, 170, 210,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  10, 10, 10, 160, 170, 210,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 }
	};
	long detectionTime = 0;
	long detectionInterval = 2000; // jangan lebih kecil dr 1 dtk untuk menghindari cellChangedData
	long attackingTime = 0;
	long numIdleSignal = 0;
	private void detectCharMode() {
    	try {
    		/*
    		 * 
    		 */
	    	long       now = System.currentTimeMillis();
	    	this .charMove = getMove();
	    	int[] cellData = new int[2];
	    	int[]   pixels = captureMousePixels(cellData);
    		int    oldMode = charMode;
	    	int    newMode = getMode(pixels, now);
	    	if (charMode == CHAR_MODE_ATTACKING && newMode == CHAR_MODE_IDLE && now - attackingTime > 1000) {
    			numIdleSignal = 1;
    		}
	    	if (now - detectionTime < detectionInterval) {
	    		return;
	    	}
	    	detectionTime = now;
	    	/*
	    	 * 
	    	 */
	    	P : {
	    		if (newMode == CHAR_MODE_ATTACKING && numIdleSignal == 0) {
	    			attackingTime = now;
  					break P;
	    		}
	    		if (newMode == CHAR_MODE_ATTACKING && numIdleSignal == 1) {
	   	    		numIdleSignal = 0;
		    		charMode = CHAR_MODE_IDLE;
		    		mScannerListener.onIdle(Scanner.this);
  					break P;
	    		}
		    	if (newMode == CHAR_MODE_TARGETING) {
		    		charMode = CHAR_MODE_TARGETING;
		    		mScannerListener.onTargeting(Scanner.this, cellData);
		    		break P;
		    	}
		    	if (newMode == CHAR_MODE_IDLE) {
		    		charMode = CHAR_MODE_IDLE;
		    		mScannerListener.onIdle(Scanner.this);
		    		break P;
		    	}
	    	}
	    	System.out.println(oldMode + " : " + newMode + " : " + charMode + " >>> " + charMove + " : " + numIdleSignal + " : " + cellChangedCounter  + " " + (now - attackingTime) + "ms");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	private int getMove() {
		int m = 0;
		if (cellChangedCounter > (long) (cellRows * cellCols * 0.9)) {
			m = 1;
    	}
		return m;
	}
	private int getMode(int[] pixels, long now) {
		int newMode = -1;
		int m = isMatch(modeTargeting, pixels, 4, 20) ? CHAR_MODE_TARGETING : 
	            isMatch(modeAttacking, pixels, 4, 20) ? CHAR_MODE_ATTACKING : 
	          	CHAR_MODE_IDLE;
		P : {
    		if (m == CHAR_MODE_ATTACKING) {
   				newMode = (charMode == CHAR_MODE_IDLE) ? CHAR_MODE_IDLE : CHAR_MODE_ATTACKING;
    			break P;
    		}
    		if (m == CHAR_MODE_IDLE) {
   				newMode = (charMode == CHAR_MODE_ATTACKING && charMove == 1) ? CHAR_MODE_ATTACKING : CHAR_MODE_IDLE;
    			break P;
    		}
			newMode = m;
		}
		return newMode;
	}
	private void mouseIdle() {
    	robot.mouseMove(cellFrame.getScreenX() + 1, cellFrame.getScreenY() + 1);
    }
	private void mouseClick() {
    	robot.mousePress(InputEvent.BUTTON1_MASK);
    	sleep(20);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
	private void mouseGoto(int x, int y) {
    	robot.mouseMove(x, y);
    }

	private int get(String key) {
		Integer i = UNTARGETABLE_MAP.get(key);
		if (i == null) {
			return 0;
		}
		else {
			return i.intValue();
		}
	}
	private int add(String key) {
		Integer i = UNTARGETABLE_MAP.get(key);
		if (i == null) {
			UNTARGETABLE_MAP.put(key, 1);
		}
		else {
			UNTARGETABLE_MAP.put(key, i.intValue() + 1);
		}
		return UNTARGETABLE_MAP.get(key);
	}
	private int min(String key) {
		Integer i = UNTARGETABLE_MAP.get(key);
		if (i == null) {
			UNTARGETABLE_MAP.put(key, -1);
		}
		else {
			UNTARGETABLE_MAP.put(key, i.intValue() - 1);
		}
		return UNTARGETABLE_MAP.get(key);
	}
	private ConcurrentHashMap<String, Integer> UNTARGETABLE_MAP = new ConcurrentHashMap<>();  
	private String createCellRGB(int[] cellData) {
		StringBuilder cellSummary = new StringBuilder();
		int[] pixels = this.cellData[cellData[0] / cellSize][cellData[1] / cellSize];
		int[] rgb    = new int[] { 0, 0, 0 };
		for (int x = 0; x < pixels.length; x += 3) {
			rgb[0] += pixels[x + 0];
			rgb[1] += pixels[x + 1];
			rgb[2] += pixels[x + 2];
		}
		rgb[0] = (rgb[0] / (cellSize * cellSize)) / 10 * 10;
		rgb[1] = (rgb[1] / (cellSize * cellSize)) / 10 * 10;
		rgb[2] = (rgb[2] / (cellSize * cellSize)) / 10 * 10;
		cellSummary.append(lrgb(rgb[0])).append(lrgb(rgb[1])).append(lrgb(rgb[2]));
		return cellSummary.toString();
		
	}
	String lrgb(int value) {
		if (value < 10) {
			return "00" + value;
		}
		if (value < 100) {
			return "0" + value;
		}
		return "" + value;
	}
    public int target(int[] cellData) {
    	int r = 1;
    	String cs = createCellRGB(cellData);
    	if (get(cs) < 0) {
    		r = 2;
    		min(cs);
    	}
    	else {
	    	robot.mouseMove(cellData[0] + cellFrame.getScreenX() + 1, cellData[1] + cellFrame.getScreenY() + 1);
	    	sleep(20);
			captureScreen();
		    int[] pixels = captureMousePixels(new int[2]);
	    	if (isMatch(modeTargeting, pixels, 4, 20)) {
	    		r = 0;
	    		add(cs);
	    		charMode = CHAR_MODE_TARGETING;
	    		mScannerListener.onTargeting(this, cellData);
	    	}
	    	else {
	    		r = 1;
	    		min(cs);
	    	}
    	}
//    	robot.mouseMove(cellData[0] + cellFrame.getScreenX() + 1, cellData[1] + cellFrame.getScreenY() + 1);
//    	sleep(20);
//		captureScreen();
//	    int[] pixels = captureMousePixels(new int[2]);
//    	if (isMatch(modeTargeting, pixels, 4, 20)) {
//    		r = 0;
//    		charMode = CHAR_MODE_TARGETING;
//    		mScannerListener.onTargeting(this, cellData);
//    	}
    	return r;
    }
//    public int targetNearby() {
//    	int r = 0;
//    	
//    	
//    	ArrayList<int[]> data = getCellChangedDataByDistance();
//    	if (data == null || data.isEmpty()) {
//    		r = 1;
//    	}
//    	else {
////			int index = data.size();
////			int index = data.size() > 100 ? 100 : data.size();
////			while (--index >= 0) {
////				int r = scanner.target(data.get(index));
////				if (r == 0) {
////					break;
////				}
////			}
//
//        	String cs = createCellRGB(cellData);
//        	if (UNTARGETABLE_MAP.get(cs) != null) {
//        		System.out.println("UNTARGETABLE_MAP --> " + cs);
//        		r = 2;
//        	}
//        	else {
//    	    	robot.mouseMove(cellData[0] + cellFrame.getScreenX() + 1, cellData[1] + cellFrame.getScreenY() + 1);
//    	    	sleep(20);
//    			captureScreen();
//    		    int[] pixels = captureMousePixels(new int[2]);
//    	    	if (isMatch(modeTargeting, pixels, 4, 20)) {
//    	    		charMode = CHAR_MODE_TARGETING;
//    	    		mScannerListener.onTargeting(this, cellData);
//    	    	}
//    	    	else {
//    	    		r = 1;
//    	    		UNTARGETABLE_MAP.put(cs, "");
//    	    	}
//        	}
//		}
//    	
//    	
//    	return r;
//    }
    private int[] captureMousePixels(int[] cellData) {
    	Point point = MouseInfo.getPointerInfo().getLocation();
    	cellData[0] = (int) point.getX() - cellFrame.getScreenX();
    	cellData[1] = (int) point.getY() - cellFrame.getScreenY();
    	if (cellData[0] >= cellFrame.getWidth() || cellData[1] >= cellFrame.getHeight()) {
    		return null;
    	}
    	int[] r = new int[0];
    	try {
    		r = shrinkCellData(screenRaster.getPixels(cellData[0] + 1, cellData[1] + 1, 4, 8, new int[4 * 8 * 3]), 10);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return r;
    }
    public void attack() {
    	System.out.println("Trying to Attack");
    	attackingTime = System.currentTimeMillis();
    	charMode = CHAR_MODE_ATTACKING;
    	mouseClick();
    	sleep(20);
		mouseIdle();
    	sleep(100);
		mScannerListener.onAttacking(Scanner.this);	
    }
    public void move(int[] cellData) {
//    	charMode = CHAR_MODE_MOVING;
    	robot.mouseMove(cellData[0] + cellFrame.getScreenX() + 1, cellData[1] + cellFrame.getScreenY() + 1);
    	sleep(20);
    	mouseClick();
    	sleep(20);
		mouseIdle();
		sleep(20);
//		mScannerListener.onMoving(Scanner.this);
    }
    public void moveRandomly() {
    	int x = new Random().nextInt(cellFrame.getWidth());
    	int y = new Random().nextInt(cellFrame.getHeight());
    	mouseGoto(x + cellFrame.getScreenX() + 1, y + cellFrame.getScreenY() + 1);
    	sleep(20);
    	mouseClick();
    	sleep(20);
		mouseIdle();
		sleep(20);
//    	charMode = CHAR_MODE_MOVING;
//		mScannerListener.onMoving(Scanner.this);
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
	private int cellSize = 4;
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
//    	cellFrame.updateCells(cellChangedData, cellTargetData);
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
		int midX = ((cellFrame.getWidth()  / 2) / cellSize) - 0;
		int midY = ((cellFrame.getHeight() / 2) / cellSize) - 3;
		
		int csCounter = 0;
		for (int x = 0; x < cellChangedData.size(); x++) {
			int[] data = cellChangedData.get(x);
			
			String cs  = createCellRGB(data);
			if (get(cs) < 0) {
				csCounter++;
				continue;
			}
			
			int curX = (data[0]) / cellSize;
			int curY = (data[1]) / cellSize;
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
		System.out.println("UNTARGETABLE: " + csCounter + " / " + UNTARGETABLE_MAP.size());
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
    	public void onMoving(Scanner scanner);
    	public void onAttacking(Scanner scanner);
    	public void onTargeting(Scanner scanner, int[] data);
    	
    }
    
    

//	if (numIdleSignal < maxIdleSignal) {
//		if (oldMode == CHAR_MODE_MOVING) {
//			numIdleSignal += 1;
//		}
//		else if (oldMode == CHAR_MODE_IDLE) {
//			numIdleSignal = maxIdleSignal;
//		}
//		else if (oldMode == CHAR_MODE_TARGETING) {
//			numIdleSignal = maxIdleSignal;
//		}
//		else if (oldMode == CHAR_MODE_ATTACKING) {
//			if (now - attackingTime > 1000) {
//				numIdleSignal = maxIdleSignal;
//			}
//		}
//		else {
//			numIdleSignal += 1;
//		}
//		mouseIdle();
//	}
//	else {
//		numIdleSignal = 0;
//		charMode = CHAR_MODE_IDLE;
//		mScannerListener.onIdle(Scanner.this);
//	}
    
}
