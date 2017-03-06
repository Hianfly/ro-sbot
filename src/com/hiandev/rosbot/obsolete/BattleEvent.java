//package com.hiandev.rosbot.obsolete;
//
//import java.awt.MouseInfo;
//import java.awt.Point;
//import java.awt.image.BufferedImage;
//import java.util.Random;
//
//import com.hiandev.rosbot.event.Event;
//import com.hiandev.rosbot.profiler.BattleProfiler;
//import com.hiandev.rosbot.scanner.BattleScanner;
//import com.hiandev.rosbot.scanner.Cell;
//
//public class BattleEvent extends Event<BattleScanner> {
//	
//	public BattleEvent(BattleScanner scanner) {
//		super (scanner);
//	}
//    
//	public static final int CHAR_MODE_IDLE = 0;
//	public static final int CHAR_MODE_TARGETING = 1;
//	public static final int CHAR_MODE_ATTACKING = 2;
//	public static final int CHAR_MODE_PICKING = 3;
//	
//	/*
//	 * 
//	 * 
//	 * 
//	 */
//	private int  charMode = 0;
//	private int  charMove = 0;
//	private long detectionTime = 0;
//	private long detectionInterval = 2000; // jangan lebih kecil dr 1 dtk untuk menghindari cellChangedData
//	private long attackingTime = 0;
//	private long attackingTimeout = 1000 * 15;
//	private int  numIdleSignal = 0;
//	private int  forceExecute = 0;
//	public boolean isMoving() {
//		return charMove == 1;
//	}
//	
//	/*
//	 * 
//	 * 
//	 * 
//	 */
//	@Override
//	public  void execute() {
//		executeMove();
//		executeMode();
//	}
//	private void executeMove() {
////		if (scanner.getCellDiff().size() > (long) (scanner.getCellRows() * scanner.getCellCols() * 0.9)) {
////			charMove = 1;
////    	}
////		else {
////			charMove = 0;
////		}
//	}
//	private void executeMode() {
//    	try {
//	    	long     now = System.currentTimeMillis();
//	    	int[] cellXY = new int[2];
//	    	int[] pixels = captureMousePixels(cellXY, false);
//    		int  oldMode = charMode;
//	    	int  newMode = executeMode(pixels, now);
//	    	/*
//	    	 * Dont modify code below...
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
//	    	 * Modify code here...
//	    	 */
//	    	P : {
//	    		if (newMode == CHAR_MODE_ATTACKING && numIdleSignal == 0) {
//	    			if (now - attackingTime > attackingTimeout) {
//	    				numIdleSignal = 1;
//	    			}
//	    			else {
//	    				attackingTime = now;
//	    			}
//  					break P;
//	    		}
//	    		if (newMode == CHAR_MODE_ATTACKING && numIdleSignal == 1) {
//	   	    		numIdleSignal = 0;
//		    		charMode = CHAR_MODE_IDLE;
//		    		if (!isMoving()) {
//		    			forceExecute = onIdle(this);
//		    		}
//  					break P;
//	    		}
//		    	if (newMode == CHAR_MODE_TARGETING) {
//		    		charMode = CHAR_MODE_TARGETING;
//		    		break P;
//		    	}
//		    	if (newMode == CHAR_MODE_IDLE) {
//		    		charMode = CHAR_MODE_IDLE;
//		    		if (!isMoving()) {
//		    			forceExecute = onIdle(this);
//		    		}
//		    		break P;
//		    	}
//	    	}
//	    	System.out.println(oldMode + " : " + newMode + " : " + charMode + " >>> " + charMove + " : " + numIdleSignal + " : " + " " + (now - attackingTime) + "ms " + forceExecute);
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}
//	}
//	private int  executeMode(int[] pixels, long now) {
//		int newMode = -1;
//		int m = isMatch(MODE_TARGETING, pixels, 4, 20) ? CHAR_MODE_TARGETING : 
//			    isMatch(MODE_ATTACKING, pixels, 4, 20) ? CHAR_MODE_ATTACKING : 
//				isMatch(MODE_PICKING,   pixels, 4, 20) ? CHAR_MODE_PICKING   : 
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
//	protected int onIdle(BattleEvent event) {
//		return 0;
//	}
//	protected int onPick(BattleEvent event) {
//		return 0;
//	}
//	
//	/*
//	 * 
//	 * 
//	 * 
//	 */
//    public int hover(Cell cell) {
//    	int r = -1;
//    	getScanner().mouseGotoCell(cell._x, cell._y);
//    	sleep(20);
//	    int[] pixels = captureMousePixels(new int[2], true);
//    	if (isMatch(MODE_TARGETING, pixels, 4, 20)) {
//    		r = charMode = CHAR_MODE_TARGETING;
//    		getScanner().getBattleProfiler().add(cell, BattleProfiler.PROFILE_TARGETABLE);
//    	}
//    	else if (isMatch(MODE_PICKING, pixels, 4, 20)) {
//    		r = charMode = CHAR_MODE_PICKING;
//    		getScanner().getBattleProfiler().add(cell, BattleProfiler.PROFILE_PICKABLE);
//    	}
//    	else {
//    		long now = System.currentTimeMillis();
//    		if (now - attackingTime > 1500) {
//    			getScanner().getBattleProfiler().add(cell, BattleProfiler.PROFILE_NOT_CLICKABLE);
//    		}
//    		getScanner().mouseIdle();
//        	sleep(20);
//    	}
//    	return r;
//    }
//    public int attack() {
//    	int r = 0;
//    	attackingTime = System.currentTimeMillis();
//    	charMode = CHAR_MODE_ATTACKING;
//    	getScanner().mouseLeftClick();
//    	sleep(20);
//    	getScanner().mouseIdle();
//    	sleep(20);
//    	return r;
//    }
//    public int pick() {
//    	int r = 0;
//    	attackingTime = System.currentTimeMillis();
//    	charMode = CHAR_MODE_PICKING;
//    	getScanner().mouseLeftClick();
//    	sleep(20);
//    	getScanner().mouseIdle();
//    	sleep(20);
//    	charMode = CHAR_MODE_IDLE;
//    	return r;
//    }
//    public int move(Cell cell) {
//    	int r = 0;
//    	getScanner().mouseGotoCell(cell._x, cell._y);
//    	sleep(20);
//    	getScanner().mouseLeftClick();
//    	sleep(20);
//    	getScanner().mouseIdle();
//    	sleep(20);
//    	return r;
//    }
//    public int moveRandomly() {
//    	int r = 0;
//    	int x = new Random().nextInt(getScanner()._w);
//    	int y = new Random().nextInt(getScanner()._h);
//    	getScanner().mouseGoto(x + 1, y + 1);
//    	sleep(20);
//    	getScanner().mouseLeftClick();
//    	sleep(20);
//    	getScanner().mouseIdle();
//    	sleep(20);
//    	return r;
//    }
//    
//    /*
//     * 
//     * 
//     * 
//     */
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
//	public static final int[][] MODE_PICKING   = new int[][] { 
//		{  230, 240, 250,  230, 240, 250,  -1, -1, -1, -1, -1, -1 },
//		{  230, 240, 250,  230, 240, 250,  -1, -1, -1, -1, -1, -1 },
//		{  230, 240, 250,  230, 240, 250,  -1, -1, -1, -1, -1, -1 },
//		{  230, 240, 250,  230, 240, 250,  -1, -1, -1, -1, -1, -1 },
//		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
//		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
//	};
//	public int[] captureMousePixels(int[] cellXY, boolean newRaster) {
//	  	Point point = MouseInfo.getPointerInfo().getLocation();
//	  	cellXY[0] = (int) point.getX() - getScanner()._x;
//	  	cellXY[1] = (int) point.getY() - getScanner()._y;
//	  	if (cellXY[0] >= getScanner()._w || cellXY[1] >= getScanner()._w) {
//	  		return null;
//	  	}
//	  	BufferedImage bi = newRaster ? getScanner().captureScreenImage() : getScanner().captureScreenImage();
//	  	int[] r = new int[0];
//	  	try {
//	  		r = getScanner().floorPixels(bi.getRaster().getPixels(cellXY[0] + 1, cellXY[1] + 1, 4, 8, new int[4 * 8 * 3]), 10);
//	  	} catch (Exception e) {
//	  		System.out.println(e.getMessage());
//	  	}
//	  	return r;
//	}
//	public boolean isMatch(int[][] testData, int[] pixels, int cellSize, int matchThreshold) {
//		boolean m = false;
//		try {
//			P : for (int x = 0; x <= pixels.length - testData[0].length; x += 3) {
//				m = true;
//				for (int i = 0; i < testData.length; i++) {
//					int xProjection = x + (cellSize * 3 * i);
//					for (int j = 0; j < testData[i].length; j++) {
//						if (testData[i][j] == -1) {
//							continue;
//						}
//						if (Math.abs(pixels[xProjection + j] - testData[i][j]) > matchThreshold) {
//							m = false;
//							break P;
//						}
//					}
//				}
//				if (m) {
//					break;
//				}
//			}
//		} catch (Exception e) {
//			 m = false;
//		}
//		return m;
//	}
//    
//}
