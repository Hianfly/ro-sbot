package com.hiandev.rosbot.obsolete;
//package com.hiandev.rosbot;
//
//import java.awt.MouseInfo;
//import java.awt.Point;
//import java.awt.image.Raster;
//import java.util.Random;
//
//import com.hiandev.rosbot.scanner.Scanner;
//import com.hiandev.rosbot.scanner.v0.ScannerXz;
//
//public class EventManager {
//	
//	private final ScannerXz scanner;
//	public EventManager(ScannerXz scanner) {
//		this.scanner = scanner;
//	}
//    
//	public static final int CHAR_MODE_IDLE = 0;
//	public static final int CHAR_MODE_TARGETING = 1;
//	public static final int CHAR_MODE_ATTACKING = 2;
//	
//	private int  charMode = 0;
//	private int  charMove = 0;
//	private long detectionTime = 0;
//	private long detectionInterval = 2000; // jangan lebih kecil dr 1 dtk untuk menghindari cellChangedData
//	private long attackingTime = 0;
//	private int  numIdleSignal = 0;
//	private int  forceExecute = 0;
//
//	/*
//	 * 
//	 * 
//	 * 
//	 */
//	
//	public void execute() {
//		executeMove();
//		executeMode();
//	}
//	private void executeMove() {
//		if (scanner.getCellDiff().size() > (long) (scanner.getCellRows() * scanner.getCellCols() * 0.9)) {
//			charMove = 1;
//    	}
//		else {
//			charMove = 0;
//		}
//	}
//	private void executeMode() {
//    	try {
//	    	long     now = System.currentTimeMillis();
//	    	int[] cellXY = new int[2];
//	    	int[] pixels = captureMousePixels(cellXY, false);
//    		int  oldMode = charMode;
//	    	int  newMode = getMode(pixels, now);
//	    	/*
//	    	 * 
//	    	 */
//	    	if (charMode == CHAR_MODE_ATTACKING && newMode == CHAR_MODE_IDLE && now - attackingTime > 1000) {
//    			numIdleSignal = 1;
//    		}
//	    	if (now - detectionTime < detectionInterval && forceExecute == 0) {
//	    		return;
//	    	}
//	    	detectionTime = now;
//	    	forceExecute = 0;
//	    	/*
//	    	 * 
//	    	 */
//	    	P : {
//	    		if (newMode == CHAR_MODE_ATTACKING && numIdleSignal == 0) {
//	    			attackingTime = now;
//  					break P;
//	    		}
//	    		if (newMode == CHAR_MODE_ATTACKING && numIdleSignal == 1) {
//	   	    		numIdleSignal = 0;
//		    		charMode = CHAR_MODE_IDLE;
//		    		if (!isMoving()) {
//		    			forceExecute = mListener.onIdle(this);
//		    		}
//  					break P;
//	    		}
//		    	if (newMode == CHAR_MODE_TARGETING) {
//		    		charMode = CHAR_MODE_TARGETING;
//		    		mListener.onTargeting(this, cellXY);
//		    		break P;
//		    	}
//		    	if (newMode == CHAR_MODE_IDLE) {
//		    		charMode = CHAR_MODE_IDLE;
//		    		if (!isMoving()) {
//		    			forceExecute = mListener.onIdle(this);
//		    		}
//		    		break P;
//		    	}
//	    	}
//	    	System.out.println(oldMode + " : " + newMode + " : " + charMode + " >>> " + charMove + " : " + numIdleSignal + " : " + scanner.getCellDiff().size()  + " " + (now - attackingTime) + "ms " + forceExecute);
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}
//	}
//	
//	private int getMode(int[] pixels, long now) {
//		int newMode = -1;
//		int m = scanner.isMatch(MODE_TARGETING, pixels, 4, 20) ? CHAR_MODE_TARGETING : 
//			    scanner.isMatch(MODE_ATTACKING, pixels, 4, 20) ? CHAR_MODE_ATTACKING : 
//	          	CHAR_MODE_IDLE;
//		P : {
//    		if (m == CHAR_MODE_ATTACKING) {
//   				newMode = (charMode == CHAR_MODE_IDLE) ? CHAR_MODE_IDLE : CHAR_MODE_ATTACKING;
//    			break P;
//    		}
//    		if (m == CHAR_MODE_IDLE) {
//   				newMode = (charMode == CHAR_MODE_ATTACKING && charMove == 1) ? CHAR_MODE_ATTACKING : CHAR_MODE_IDLE;
//    			break P;
//    		}
////    		if (m == CHAR_MODE_TARGETING) {
////    			newMode = (charMode == CHAR_MODE_ATTACKING) ? CHAR_MODE_ATTACKING : CHAR_MODE_TARGETING;
////    			break P;
////    		}
//			newMode = m;
//		}
//		return newMode;
//	}
//	public boolean isMoving() {
//		return charMove == 1;
//	}
//	
//	/*
//	 * 
//	 * 
//	 * 
//	 */
//    public int target(int[] cellXY) {
//    	int r = 1;
//    	scanner.mouseGoto(cellXY[0] + scanner._x + 1, cellXY[1] + scanner._y + 1);
//    	sleep(20);
//	    int[] pixels = captureMousePixels(new int[2], true);
//    	if (scanner.isMatch(MODE_TARGETING, pixels, 4, 20)) {
//    		r = 0;
//    		charMode = CHAR_MODE_TARGETING;
//    	}
//    	else {
//        	scanner.mouseIdle();
//        	sleep(20);
//    	}
//    	return r;
//    }
//    public int attack(int[] cellXY) {
//    	int r = 0;
//    	attackingTime = System.currentTimeMillis();
//    	charMode = CHAR_MODE_ATTACKING;
//    	scanner.mouseClick();
//    	sleep(20);
//    	scanner.mouseIdle();
//    	sleep(20);
//    	return r;
//    }
//    public int move(int[] cell) {
//    	int r = 0;
//    	scanner.mouseGoto(cell[0] + scanner._x + 1, cell[1] + scanner._y + 1);
//    	sleep(20);
//    	scanner.mouseClick();
//    	sleep(20);
//    	scanner.mouseIdle();
//    	sleep(20);
//    	return r;
//    }
//    public int moveRandomly() {
//    	int r = 0;
//    	int x = new Random().nextInt(scanner._w);
//    	int y = new Random().nextInt(scanner._h);
//    	scanner.mouseGoto(x + scanner._x + 1, y + scanner._y + 1);
//    	sleep(20);
//    	scanner.mouseClick();
//    	sleep(20);
//    	scanner.mouseIdle();
//    	sleep(20);
//    	return r;
//    }
//    
//    /*
//     * 
//     * 
//     * 
//     */
//
//	public static final int[][] MODE_TARGETING = new int[][] { 
//		{  10, 10, 10,  10,  10,  10,  10,  10,  10,  10,  10,  10 },
//		{  10, 10, 10, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
//		{  10, 10, 10, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
//		{  10, 10, 10, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
//		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 }
//	};
//	public static final int[][] MODE_ATTACKING = new int[][] { 
//		{  10, 10, 10,  20,  20,  20,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  10, 10, 10, 150, 170, 210,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  10, 10, 10, 150, 170, 210,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  10, 10, 10, 160, 170, 210,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 }
//	};
//	public int[] captureMousePixels(int[] cellXY, boolean newRaster) {
//	  	Point point = MouseInfo.getPointerInfo().getLocation();
//	  	cellXY[0] = (int) point.getX() - scanner._x;
//	  	cellXY[1] = (int) point.getY() - scanner._y;
//	  	if (cellXY[0] >= scanner._w || cellXY[1] >= scanner._w) {
//	  		return null;
//	  	}
//	  	Raster screenRaster = newRaster ? scanner.captureScreen() : scanner.getScreenRaster();
//	  	int[] r = new int[0];
//	  	try {
//	  		r = scanner.shrinkCellData(screenRaster.getPixels(cellXY[0] + 1, cellXY[1] + 1, 4, 8, new int[4 * 8 * 3]), 10);
//	  	} catch (Exception e) {
//	  		System.out.println(e.getMessage());
//	  	}
//	  	return r;
//	  }
//    
//    /*
//     *
//     * 
//     * 
//     */
//    
//    private void sleep(long time) {
//    	try {
//	    	Thread.sleep(time);
//	    } catch (Exception e) {
//	    }
//    }
//
//    private EventListener mListener = null;
//    public void setEventListener(EventListener listener) {
//    	mListener = listener;
//    }
//    public static interface EventListener {
//
//    	public int onIdle(EventManager event);
//    	public int onMoving(EventManager event);
//    	public int onTargeting(EventManager event, int[] cellXY);
//
//    }
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
////
////    private int target(int[] cellXY) {
////    	int r = 1;
//////    	String cs = createCellRGB(cellData);
//////    	if (get(cs) < 0) {
//////    		r = 2;
//////    		min(cs);
//////    	}
//////    	else {
//////	    	robot.mouseMove(cellData[0] + _x + 1, cellData[1] + _y + 1);
//////	    	sleep(20);
//////			captureScreen();
//////		    int[] pixels = captureMousePixels(new int[2]);
//////	    	if (isMatch(modeTargeting, pixels, 4, 20)) {
//////	    		r = 0;
//////	    		add(cs);
//////	    		charMode = CHAR_MODE_TARGETING;
//////	    		mScannerListener.onTargeting(this, cellData);
//////	    	}
//////	    	else {
//////	    		r = 1;
//////	    		min(cs);
//////	    	}
//////    	}
////    	scanner.mouseGoto(cell[0] + scanner._x + 1, cell[1] + scanner._y + 1);
////    	sleep(20);
////    	scanner.captureScreen();
////	    int[] pixels = scanner.captureMousePixels(new int[2], 4, 8);
////    	if (scanner.isMatch(MODE_TARGETING, pixels, 4, 20)) {
////    		r = 0;
////    		charMode = CHAR_MODE_TARGETING;
////    		afterTargeting(mListener.onTargeting(this, cell), cell);
////    	}
////    	return r;
////    }
//
//
////	private int get(String key) {
////		Integer i = UNTARGETABLE_MAP.get(key);
////		if (i == null) {
////			return 0;
////		}
////		else {
////			return i.intValue();
////		}
////	}
////	private int add(String key) {
////		Integer i = UNTARGETABLE_MAP.get(key);
////		if (i == null) {
////			UNTARGETABLE_MAP.put(key, 1);
////		}
////		else {
////			UNTARGETABLE_MAP.put(key, i.intValue() + 1);
////		}
////		return UNTARGETABLE_MAP.get(key);
////	}
////	private int min(String key) {
////		Integer i = UNTARGETABLE_MAP.get(key);
////		if (i == null) {
////			UNTARGETABLE_MAP.put(key, -1);
////		}
////		else {
////			UNTARGETABLE_MAP.put(key, i.intValue() - 1);
////		}
////		return UNTARGETABLE_MAP.get(key);
////	}
////	private ConcurrentHashMap<String, Integer> UNTARGETABLE_MAP = new ConcurrentHashMap<>();  
////	private String createCellRGB(int[] cellData) {
////		StringBuilder cellSummary = new StringBuilder();
////		int[] pixels = this.cellData[cellData[0] / cellSize][cellData[1] / cellSize];
////		int[] rgb    = new int[] { 0, 0, 0 };
////		for (int x = 0; x < pixels.length; x += 3) {
////			rgb[0] += pixels[x + 0];
////			rgb[1] += pixels[x + 1];
////			rgb[2] += pixels[x + 2];
////		}
////		rgb[0] = (rgb[0] / (cellSize * cellSize)) / 10 * 10;
////		rgb[1] = (rgb[1] / (cellSize * cellSize)) / 10 * 10;
////		rgb[2] = (rgb[2] / (cellSize * cellSize)) / 10 * 10;
////		cellSummary.append(lrgb(rgb[0])).append(lrgb(rgb[1])).append(lrgb(rgb[2]));
////		return cellSummary.toString();
////		
////	}
////	String lrgb(int value) {
////		if (value < 10) {
////			return "00" + value;
////		}
////		if (value < 100) {
////			return "0" + value;
////		}
////		return "" + value;
////	}
//    
//}
