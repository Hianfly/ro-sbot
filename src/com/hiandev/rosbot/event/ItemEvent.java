package com.hiandev.rosbot.event;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;
import com.hiandev.rosbot.scanner.Cell;
import com.hiandev.rosbot.scanner.ItemScanner;

public class ItemEvent extends Event<ItemScanner> {
	
	public ItemEvent(ItemScanner scanner) {
		super (scanner);
	}
    
	public static final int CHAR_MODE_IDLE = 0;
	public static final int CHAR_MODE_TARGETING = 1;
	public static final int CHAR_MODE_ATTACKING = 2;
	public static final int CHAR_MODE_PICKING = 3;
	
	/*
	 * 
	 * 
	 * 
	 */
	private int  mouseCellX = 5;
	private int  mouseCellY = 102;
	private int  charMode = 0;
	private int  charMove = 0;
	private long detectionTime = 0;
	private long detectionInterval = 1500; // jangan lebih kecil dr 1 dtk untuk menghindari cellChangedData
	private long attackingTime = 0;
	private long attackingTimeout = 1000 * 20;
	private long attackingStartTime = 0;
	private int  numIdleSignal = 0;
	private int  forceExecute = 0;
	private long idleTime = 0;
	public boolean isMoving() {
		return charMove == 1;
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	@Override
	public  void execute() {
		executeMove();
		executeMode();
	}
	private void executeMove() {
		if (getScanner().getMotionSize() > getScanner().getCellSize() * 0.7) {
			charMove = 1;
    	}
		else {
			charMove = 0;
		}
	}
	private void executeMode() {
    	try {
	    	long     now = System.currentTimeMillis();
	    	int[] pixels = captureMousePixels(false, 10);
    		int  oldMode = charMode;
	    	int  newMode = executeMode(pixels, now);
	    	/*
	    	 * Dont modify code below...
	    	 */
	    	if (charMode == CHAR_MODE_ATTACKING && newMode == CHAR_MODE_IDLE && now - attackingTime > 1000) {
    			numIdleSignal = 1;
    		}
	    	if (now - detectionTime < detectionInterval && forceExecute == 0) {
	    		return;
	    	}
	    	detectionTime = now;
	    	forceExecute = 0;
	    	/*
	    	 * Modify code here...
	    	 */
	    	P : {
	    		if (newMode == CHAR_MODE_ATTACKING && numIdleSignal == 0) {
	    			if (now - attackingStartTime > attackingTimeout) {
	    				numIdleSignal = 1;
	    			}
	    			else {
	    				attackingTime = now;
	    			}
  					break P;
	    		}
	    		if (newMode == CHAR_MODE_ATTACKING && numIdleSignal == 1) {
	   	    		numIdleSignal = 0;
		    		charMode = CHAR_MODE_IDLE;
		    		if (!isMoving()) {
	    				idleTime = idleTime == 0  ? now : idleTime;
		    			forceExecute = onIdle(this, now - idleTime, oldMode);
		    		}
  					break P;
	    		}
		    	if (newMode == CHAR_MODE_TARGETING) {
		    		charMode = CHAR_MODE_TARGETING;
		    		break P;
		    	}
		    	if (newMode == CHAR_MODE_IDLE) {
		    		charMode = CHAR_MODE_IDLE;
		    		if (!isMoving()) {
	    				idleTime = idleTime == 0  ? now : idleTime;
		    			forceExecute = onIdle(this, now - idleTime, oldMode);
		    		}
		    		break P;
		    	}
	    	}
	    	System.out.println(oldMode + " : " + newMode + " : " + charMode + "  ---  mv:" + charMove + "  is:" + numIdleSignal + "  fe:" + forceExecute + "  it:" + (idleTime == 0 ? 0 : now - idleTime)  + "ms  at:" + (attackingTime == 0 ? 0 : now - attackingTime) + "ms");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	private int  executeMode(int[] pixels, long now) {
		int newMode = -1;
		int m = isMatch(MODE_TARGETING, pixels, 4, 20) ? CHAR_MODE_TARGETING : 
			    isMatch(MODE_ATTACKING, pixels, 4, 20) ? CHAR_MODE_ATTACKING : 
				isMatch(MODE_PICKING,   pixels, 4, 20) ? CHAR_MODE_PICKING   : 
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
//    		if (m == CHAR_MODE_TARGETING) {
//    			newMode = (charMode == CHAR_MODE_ATTACKING) ? CHAR_MODE_ATTACKING : CHAR_MODE_TARGETING;
//    			break P;
//    		}
			newMode = m;
		}
		return newMode;
	}
	protected int onIdle(ItemEvent event, long duration, int prevMode) {
		return 0;
	}

	/*
	 * 
	 * 
	 * 
	 */
    public int hoverCell(Cell cell) {
    	return hoverCell(cell._cx, cell._cy);
    }
    public int hoverCell(int _cx, int _cy) {
    	int r = -1;
    	getScanner().mouseGotoCell(_cx, _cy);
    	sleep(20);
	    int[] pixels = captureMousePixels(true, 10);
    	if (isMatch(MODE_TARGETING, pixels, 4, 20)) {
    		r = charMode = CHAR_MODE_TARGETING;
    	}
    	else if (isMatch(MODE_PICKING, pixels, 4, 20)) {
    		r = charMode = CHAR_MODE_PICKING;
    	}
    	return r;
    }
    public int cancel() {
    	int r = 0;
    	getScanner().mouseGotoCell(mouseCellX, mouseCellY);
    	sleep(20);
		return r;
    }
    
    /*
     * 
     * 
     * 
     */
    public int attack() {
    	int r = 0;
    	attackingTime = attackingStartTime = System.currentTimeMillis();
    	idleTime = 0;
    	charMode = CHAR_MODE_ATTACKING;
    	getScanner().mouseLeftClick();
    	sleep(20);
    	getScanner().mouseGotoCell(mouseCellX, mouseCellY);
    	sleep(20);
    	return r;
    }
    public int pick() {
    	int r = 0;
    	attackingTime = attackingStartTime = System.currentTimeMillis();
    	idleTime = 0;
    	charMode = CHAR_MODE_PICKING;
    	getScanner().mouseLeftClick();
    	sleep(20);
    	getScanner().mouseGotoCell(mouseCellX, mouseCellY);
    	sleep(20);
    	charMode = CHAR_MODE_IDLE;
    	return r;
    }
    public int teleport() {
    	int r = 0;
    	idleTime = 0;
		getScanner().keyPush(KeyEvent.VK_Z);
    	sleep(20);
		return r;
    }
    public int move(Cell cell) {
    	int r = 0;
    	idleTime = 0;
    	getScanner().mouseGotoCell(cell._cx, cell._cy);
    	sleep(20);
    	getScanner().mouseLeftClick();
    	sleep(20);
    	getScanner().mouseGotoCell(mouseCellX, mouseCellY);
    	sleep(20);
    	return r;
    }
//    public int moveRandomly() {
//    	int r = 0;
//    	idleTime = 0;
//    	int x = new Random().nextInt(getScanner()._w);
//    	int y = new Random().nextInt(getScanner()._h);
//    	getScanner().mouseGoto(x + 1, y + 1);
//    	sleep(20);
//    	getScanner().mouseLeftClick();
//    	sleep(20);
//    	getScanner().mouseGotoCell(mouseCellX, mouseCellY);
//    	sleep(20);
//    	return r;
//    }
    
    /*
     * 
     * 
     * 
     */
	public static final int[][] MODE_TARGETING = new int[][] { 
		{  10, 10, 10,  10,  10,  10,  10,  10,  10,  10,  10,  10 },
		{  10, 10, 10, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
		{  10, 10, 10, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
		{  10, 10, 10, 250, 250, 250, 250, 250, 250, 250, 250, 250 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 }
	};
	public static final int[][] MODE_ATTACKING = new int[][] { 
		{  10, 10, 10,  20,  20,  20,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  10, 10, 10, 150, 170, 210,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  10, 10, 10, 150, 170, 210,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  10, 10, 10, 160, 170, 210,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 }
	};
	public static final int[][] MODE_PICKING   = new int[][] { 
		{  230, 240, 250,  230, 240, 250,  -1, -1, -1, -1, -1, -1 },
		{  230, 240, 250,  230, 240, 250,  -1, -1, -1, -1, -1, -1 },
		{  230, 240, 250,  230, 240, 250,  -1, -1, -1, -1, -1, -1 },
		{  230, 240, 250,  230, 240, 250,  -1, -1, -1, -1, -1, -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
		{  -1, -1, -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1 },
	};
	public int[] captureMousePixels(boolean newRaster, int floorThreshold) {
	  	Point point = MouseInfo.getPointerInfo().getLocation();
	  	int x = (int) point.getX() - getScanner()._x;
	  	int y = (int) point.getY() - getScanner()._y;
	  	int w = 4;
	  	int h = 8;
	  	int f = 1;
	  	int t = 10;
	  	if (x < 0 || y < 0 || x >= getScanner()._w || y >= getScanner()._w) {
	  		return null;
	  	}
	  	int[] r = new int[0];
	  	try {
		  	if (newRaster) {
			  	BufferedImage bi = getScanner().captureScreenImage((int) point.getX(), (int) point.getY(), w + f, h + f);
				r = getScanner().floorPixels(bi.getRaster().getPixels(0 + f, 0 + f, w, h, new int[w * h * 3]), floorThreshold);
			}
		  	else {
			  	BufferedImage bi = getScanner().getScreenImage();
				r = getScanner().floorPixels(bi.getRaster().getPixels(x + f, y + f, w, h, new int[w * h * 3]), floorThreshold);
			}
	  	} catch (Exception e) {
	  		e.printStackTrace();
	  	}
	  	return r;
	}
	public boolean isMatch(int[][] testData, int[] pixels, int cellSize, int matchThreshold) {
		boolean m = false;
		try {
			P : for (int x = 0; x <= pixels.length - testData[0].length; x += 3) {
				m = true;
				for (int i = 0; i < testData.length; i++) {
					int xProjection = x + (cellSize * 3 * i);
					for (int j = 0; j < testData[i].length; j++) {
						if (testData[i][j] == -1) {
							continue;
						}
						if (Math.abs(pixels[xProjection + j] - testData[i][j]) > matchThreshold) {
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
    
}