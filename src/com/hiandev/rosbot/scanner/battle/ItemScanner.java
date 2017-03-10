package com.hiandev.rosbot.scanner.battle;

import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import com.hiandev.rosbot.scanner.Pixel;
import com.hiandev.rosbot.scanner.Scanner;

public class ItemScanner extends Scanner {

    public ItemScanner(int _x, int _y) throws AWTException {
    	super (_x, _y, 800, 600);
    	this.cellSize    = ((_h / Cell.SIZE) * (_w / Cell.SIZE));
    	this.middleCellY = ((_h / 2) / Cell.SIZE) - 3;
    	this.middleCellX = ((_w / 2) / Cell.SIZE) - 1;
    	this.cellMatrix  = new Cell[_h / Cell.SIZE][_w / Cell.SIZE];
    	this.cellMotion  = new int [_h / Cell.SIZE][_w / Cell.SIZE];
    	this.cellOldSumm = new int [_h / Cell.SIZE][_w / Cell.SIZE][3];
    	this.cellNewSumm = new int [_h / Cell.SIZE][_w / Cell.SIZE][3];
    	this.cellNeutral = new int [_h / Cell.SIZE][_w / Cell.SIZE];
    	setInterval(10);
    }
    @Override
    public boolean onStart() {
    	boolean s = super.onStart();
    	createNeutralCell();
    	return s;
    }
    @Override
	public void onExecute() {
		super.onExecute();
		try {
			createCellMatrix();
			removeDarkPixels(50); // 50
			removeBackground(80); // 80
			createNewCellSummary(15); // 15
			createCellMotion(0);
			removeCellMotionNoise();
			renderCellMotion();
			createOldCellSummary();
			createItemProfile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    @Override
    public void onPostExecute() {
    	super.onPostExecute();
		computeCharacterMovement();
		computeCharacterMode();
    }
	@Override
    public BufferedImage toBufferedImage() {
		return toBufferedImage(toPixels(cellMatrix));
    }
	protected int onIdle(long duration, int prevMode) {
		return 0;
	}
	
    /*
     * 
     * 
     * 
     */
	private int middleCellX = 0;
    private int middleCellY = 0;
    private int cellSize = 0;    
    public  int getMiddleCellX() {
    	return middleCellX;
    }
    public  int getMiddleCellY() {
    	return middleCellY;
    }
    public  int getCellDistance(int x, int y) {
    	return Math.abs(middleCellX - x) + Math.abs(middleCellY - y);
    }
    public  int getCellSize() {
    	return cellSize;
    }
    
    /*
     * 
     * 
     * 
     */
    private int[][] cellNeutral = null;   
    private void createNeutralCell() {
    	// Battle Msg
    	for (int x = 100; x < 120; x++) {
    		for (int y = 0; y < 160; y++) {
    	    	cellNeutral[x][y] = 1;
    		}
    	}
    	// Main Character
    	for (int x = 46; x < 62; x++) {
    		for (int y = 76; y < 85; y++) {
    	    	cellNeutral[x][y] = 1;
    		}
    	}
    	// Toa
    	for (int x = 0; x < 19; x++) {
    		for (int y = 0; y < 131; y++) {
    	    	cellNeutral[x][y] = 1;
    		}
    	}
    	// Cash Shop
    	for (int x = 0; x < 19; x++) {
    		for (int y = 112; y < 133; y++) {
    	    	cellNeutral[x][y] = 1;
    		}
    	}
    	// Map
    	for (int x = 0; x < 33; x++) {
    		for (int y = 130; y < 160; y++) {
    	    	cellNeutral[x][y] = 1;
    		}
    	}
    	// Buff List
    	for (int x = 33; x < 120; x++) {
    		for (int y = 149; y < 160; y++) {
    	    	cellNeutral[x][y] = 1;
    		}
    	}
    	// ???
    	for (int x = 0; x < 120; x++) {
    		for (int y = 0; y < 1; y++) {
    	    	cellNeutral[x][y] = 1;
    		}
    	}
    	for (int x = 0; x < 120; x++) {
    		for (int y = 159; y < 160; y++) {
    	    	cellNeutral[x][y] = 1;
    		}
    	}
    	for (int x = 0; x < 1; x++) {
    		for (int y = 0; y < 160; y++) {
    	    	cellNeutral[x][y] = 1;
    		}
    	}
    	for (int x = 119; x < 120; x++) {
    		for (int y = 0; y < 160; y++) {
    	    	cellNeutral[x][y] = 1;
    		}
    	}
    	
    }
    
    /*
     * 
     * 
     * 
     */
	private Cell[][] cellMatrix = null;
	public  Cell[][] getCellMatrix() {
		return cellMatrix;
	}
	private void createCellMatrix() {
		motionSize = 0;
		for (int y = 0; y < cellMatrix.length; y++) {
			for (int x = 0; x < cellMatrix[y].length; x++) {
				int[] pixels = getScreenImage().getRaster().getPixels(x * Cell.SIZE, y * Cell.SIZE, Cell.SIZE, Cell.SIZE, (int[]) null);
				Cell  cell   = new Cell(x, y, Pixel.floorBlueOnePixels(pixels), getCellDistance(x, y));
				cellMatrix[y][x] = cell;
			}
		}
	}
	private void removeDarkPixels(int threshold) {
		for (int y = 0; y < cellMatrix.length; y++) {
			for (int x = 0; x < cellMatrix[y].length; x++) {
				if (cellNeutral[y][x] == 1) {
					continue;
				}
				int[][] pixels = cellMatrix[y][x].pixels;
				for (int j = 0; j < pixels.length; j += 1) {
					for (int i = 0; i < pixels[j].length; i += 3) {
						if (Pixel.isBelow (pixels[j], i, threshold)) {
							Pixel.setPixel(pixels[j], i, 255, 255, 255);
						}
					}
				}
			}
		}
	}
	private void removeBackground(int threshold) {
    	int r = 0;
    	int g = 0;
    	int b = 0;
    	int d = 0;
    	for (int y = 0; y < cellMatrix.length; y++) {
			for (int x = 0; x < cellMatrix[y].length; x++) {
				if (cellNeutral[y][x] == 1) {
					continue;
				}
				int[][] pixels = cellMatrix[y][x].pixels;
				for (int j = 0; j < pixels.length; j += 1) {
					for (int i = 0; i < pixels[j].length; i += 3) {
						if (Pixel.isMatch(pixels[j], i, 255, 255, 255)) {
							continue;
						}
						r += pixels[j][i + 0];
						g += pixels[j][i + 1];
						b += pixels[j][i + 2];
						d += 1;
					}
				}
			}
    	}
    	if (d == 0) {
    		r = 255;
    		g = 255;
    		b = 255;
    		d = 1;
    	}
    	r /= d;
    	g /= d;
    	b /= d;
    	for (int y = 0; y < cellMatrix.length; y++) {
			for (int x = 0; x < cellMatrix[y].length; x++) {
				if (cellNeutral[y][x] == 1) {
					continue;
				}
				int[][] pixels = cellMatrix[y][x].pixels;
				for (int j = 0; j < pixels.length; j += 1) {
					for (int i = 0; i < pixels[j].length; i += 3) {
						if (Pixel.isMatch(pixels[j], i, 255, 255, 255)) {
							continue;
						}
						boolean background = Math.abs(pixels[j][i + 0] - r) < threshold &&
											 Math.abs(pixels[j][i + 1] - g) < threshold &&
											 Math.abs(pixels[j][i + 2] - b) < threshold; 
						if (background) {
							Pixel.setPixel(pixels[j], i, 255, 255, 255);
						}
					}
				}
			}
    	}
    }
	private int[] toPixels(Cell[][] cellMatrix) {
		int[] pixels = new int[_w * _h * 3];
		int   index  = 0;
		for (int y = 0; y < cellMatrix.length; y++) {
			for (int z = 0; z < Cell.SIZE; z++) {
				for (int x = 0; x < cellMatrix[y].length; x++) {
					if (cellMatrix[y][x] == null) {
						continue;
					}
					int[] int1d = cellMatrix[y][x].toInt1D(z);
					for (int i : int1d) {
						pixels[index++] = i;
					}
				}
			}
		}
		return pixels;
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	private int[][][] cellOldSumm = null;
	private int[][][] cellNewSumm = null;
	private void createNewCellSummary(int threshold) {
		for (int r = 0; r < cellMatrix.length; r++) {
			for (int c = 0; c < cellMatrix[r].length; c++) {
				if (cellNeutral[r][c] == 1) {
					continue;
				}
				cellNewSumm[r][c] = Pixel.getAveragePixels(cellMatrix[r][c].pixels, threshold, true, false);	
			}
		}
    }
	private void createOldCellSummary() {
		for (int y = 0; y < cellMatrix.length; y++) {
			for (int x = 0; x < cellMatrix[y].length; x++) {
				if (cellNeutral[y][x] == 1) {
					continue;
				}
				cellOldSumm[y][x] = Pixel.clonePixel(cellNewSumm[y][x]);	
			}
		}
    }
	public  ArrayList<Cell> getNonWhiteCells(int maxDistance) {
		ArrayList<Cell> list = new ArrayList<>();
		for (int y = 0; y < cellMatrix.length; y++) {
			for (int x = 0; x < cellMatrix[y].length; x++) {
				if (cellNeutral[y][x] == 1) {
					continue;
				}
				if (Pixel.isMatch(cellNewSumm[y][x], 0, 255, 255, 255)) {
					continue;
				}
				if (getCellDistance(x, y) < maxDistance) {
					list.add(new Cell(cellMatrix[y][x]));
				}
			}
		}
		return list;
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	private int[][] cellMotion = null;
	private int motionSize = 0;
	private void createCellMotion(int threshold) {
		for (int y = 0; y < cellMatrix.length; y++) {
			for (int x = 0; x < cellMatrix[y].length; x++) {
				if (cellNeutral[y][x] == 1) {
					continue;
				}
				if (isMotion(cellOldSumm[y][x], cellNewSumm[y][x], threshold)) {
					cellMotion[y][x] = 1; motionSize++;
				}
				else {
					cellMotion[y][x] = 0;
				}
			}
		}
	}
	private void removeCellMotionNoise() {
		for (int y = 1; y < cellMotion.length - 1; y++) {
			for (int x = 1; x < cellMotion[y].length - 1; x++) {
				if (cellMotion[y][x] == 0) {
					continue;
				}
				int sum = 0;
				if (cellMotion[y - 1][x - 1] == 1) sum++;
				if (cellMotion[y - 1][x - 0] == 1) sum++;
				if (cellMotion[y - 1][x + 1] == 1) sum++;
				if (cellMotion[y - 0][x - 1] == 1) sum++;
//				if (cellMotion[y - 0][x - 0] == 1) sum++;
				if (cellMotion[y - 0][x + 1] == 1) sum++;
				if (cellMotion[y + 1][x - 1] == 1) sum++;
				if (cellMotion[y + 1][x - 0] == 1) sum++;
				if (cellMotion[y + 1][x + 1] == 1) sum++;
				if (sum < 2) {
					cellMotion[y][x] = 0;
					motionSize--;
				}
			}
		}
	}
	private void renderCellMotion() {
		for (int y = 0; y < cellMatrix.length; y++) {
			for (int x = 0; x < cellMatrix[y].length; x++) {
				if (cellNeutral[y][x] == 1) {
					continue;
				}
				if (cellMotion [y][x] == 0) {
					continue;
				}
				if (Pixel.isMatch(cellNewSumm[y][x], 0, 255, 255, 255)) {
					continue;
				}
				int[][] pixels = cellMatrix[y][x].pixels;
				for (int j = 0; j < pixels.length; j += 1) {
					for (int i = 0; i < pixels[j].length; i += 3) {
						if (Pixel. isMatch(pixels[j], i, 255, 255, 255)) {
							Pixel.setPixel(pixels[j], i, 255, 0, 0);
						}
						
					}
				}
			}
    	}
	}
	private boolean isMotion(int[] oldSumm, int[] newSumm, int threshold) {
		return  Math.abs(oldSumm[0] - newSumm[0]) > threshold && 
				Math.abs(oldSumm[1] - newSumm[1]) > threshold &&
				Math.abs(oldSumm[2] - newSumm[2]) > threshold;
	}
	public  int getMotionSize() {
    	return motionSize;
    }
    public  ArrayList<Cell> getMotionCells() {
    	ArrayList<Cell> list = new ArrayList<>();
    	for (int y = 0; y < cellMatrix.length; y++) {
			for (int x = 0; x < cellMatrix[y].length; x++) {
				if (cellNeutral[y][x] == 1) {
					continue;
				}
				if (cellMotion [y][x] == 0) {
					continue;
				}
				list.add(new Cell(cellMatrix[y][x]));
			}
    	}
    	return list;
    }
	
    /*
     * 
     * 
     * 
     */
    private final ConcurrentHashMap<Long, String> ITEM_PIXEL_MAP = new ConcurrentHashMap<Long, String>();
    private void createItemProfile() {
		for (int y = 0; y < cellMatrix.length; y++) {
			for (int x = 0; x < cellMatrix[y].length; x++) {
				if (cellNeutral[y][x] == 1) {
					continue;
				} 
				if (cellMotion [y][x] == 0) {
					continue;
				}
				if (Pixel.isMatch(cellNewSumm[y][x], 0, 255, 255, 255)) {
					continue;
				}
				int[][] pixels = cellMatrix[y][x].pixels;
				for (int j = 0; j < pixels.length; j += 1) {
					for (int i = 0; i < pixels[j].length; i += 3) {
						if (Pixel.isMatch(pixels[j], i,  -1,  -1,   0)) {
							ITEM_PIXEL_MAP.put(Pixel.createPixelKey(Pixel.getPixel(pixels[j], i)), "");
						}
					}
				}
			}
		}
	}
    public  int getItemPixelMapSize() {
		return ITEM_PIXEL_MAP.size();
	}
	public  ArrayList<Cell> findItemCells(ArrayList<Cell> cellList, int threshold) {
		ArrayList<Cell> itemList = new ArrayList<>();
	  	for (Cell cell : cellList) {
	  		for (int y = 0; y < cell.pixels.length; y++) {
	  			int numF = 0;
	  			int numT = 0;
	  			for (int x = 0; x < cell.pixels[y].length; x += 3) {
	  				if (Pixel.isMatch(cell.pixels[y], x, 255, 255, 255)) {
	  					continue;
	  				}
	  				if (Pixel.isMatch(cell.pixels[y], x, 255,   0,   0)) {
	  					continue;
	  				}
	  				int[] pixel = Pixel.getPixel(cell.pixels[y], x); pixel[2] = 0;
		  			long  key   = Pixel.createPixelKey(pixel);
		  			if (ITEM_PIXEL_MAP.get(key) != null) {
		  				numF++;
		  			}
		  			numT++;
	  			}
	  			if (numT > 0 && (numF * 10) / numT >= threshold) {
	  				itemList.add(cell);
	  			}
	  		}
	  	}
	  	return itemList;
	}
    
	/*
	 * 
	 * 
	 * 
	 */
    public static class CellDistanceComparator implements Comparator<Cell> {
    	@Override
		public int compare(Cell arg0, Cell arg1) {
			return arg0.distance.compareTo(arg1.distance);
		}
    }
    
    
    // -----------------------------------------------------------------------------
    // -----------------------------------------------------------------------------
    // -----------------------------------------------------------------------------
    // -----------------------------------------------------------------------------
    // -----------------------------------------------------------------------------
    
	
	private int  charMove = 0;
	private void computeCharacterMovement() {
		if (getMotionSize() > getCellSize() * 0.7) {
			charMove = 1;
    	}
		else {
			charMove = 0;
		}
	}
	
	/*
	 * 
	 * 
	 * 
	 */
    public static final int MODE_IDLE   = 0;
	public static final int MODE_TARGET = 1;
	public static final int MODE_ATTACK = 2;
	public static final int MODE_PICK   = 3;
	private int  charMode            = 0;
	private int  detectionForce      = 0;
	private long detectionUpdateTime = 0;
	private long detectionInterval   = 500; // jangan lebih kecil dr 1 dtk untuk menghindari cellChangedData
	private long attackUpdateTime    = 0;
	private long attackTimeout       = 1000 * 20;
	private long attackInterval      = 350; // 1000
	private long attackStartTime     = 0;
	private int  idleNumSignal       = 0;
	private long idleUpdateTime      = 0;
	private void computeCharacterMode() {
    	try {
	    	long     now = System.currentTimeMillis();
	    	int[] pixels = MouseHelper.capturePixels(this, false, 10);
    		int  oldMode = charMode;
	    	int  newMode = computeCharacterMode(pixels, now);
	    	/*
	    	 * Dont modify code below...
	    	 */
	    	if (charMode == MODE_ATTACK && newMode == MODE_IDLE && now - attackUpdateTime > attackInterval) {
    			idleNumSignal = 1;
    		}
	    	if (now - detectionUpdateTime < detectionInterval && detectionForce == 0) {
	    		return;
	    	}
	    	detectionUpdateTime = now;
	    	detectionForce = 0;
	    	/*
	    	 * Modify code here...
	    	 */
	    	P : {
	    		if (newMode == MODE_ATTACK && idleNumSignal == 0) {
	    			if (now - attackStartTime > attackTimeout) {
	    				idleNumSignal = 1;
	    			}
	    			else {
	    				attackUpdateTime = now;
	    			}
  					break P;
	    		}
	    		if (newMode == MODE_ATTACK && idleNumSignal == 1) {
	   	    		idleNumSignal = 0;
		    		charMode = MODE_IDLE;
		    		if (charMove != 1) {
	    				idleUpdateTime = idleUpdateTime == 0  ? now : idleUpdateTime;
		    			detectionForce = onIdle(now - idleUpdateTime, oldMode);
		    		}
  					break P;
	    		}
		    	if (newMode == MODE_TARGET) {
		    		charMode = MODE_TARGET;
		    		break P;
		    	}
		    	if (newMode == MODE_IDLE) {
		    		charMode = MODE_IDLE;
		    		if (charMove != 1) {
	    				idleUpdateTime = idleUpdateTime == 0  ? now : idleUpdateTime;
		    			detectionForce = onIdle(now - idleUpdateTime, oldMode);
		    		}
		    		break P;
		    	}
	    	}
	    	System.out.println(oldMode + " : " + newMode + " : " + charMode + "  ---  mv:" + charMove + "  is:" + idleNumSignal + "  fe:" + detectionForce + "  it:" + (idleUpdateTime == 0 ? 0 : now - idleUpdateTime)  + "ms  at:" + (attackUpdateTime == 0 ? 0 : now - attackUpdateTime) + "ms");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	private int  computeCharacterMode(int[] pixels, long now) {
		int newMode = -1;
		int m = MouseHelper.isMatch(MouseHelper.ASSET_TARGETING, pixels, 4, 20) ? MODE_TARGET : 
				MouseHelper.isMatch(MouseHelper.ASSET_ATTACKING, pixels, 4, 20) ? MODE_ATTACK : 
				MouseHelper.isMatch(MouseHelper.ASSET_PICKING,   pixels, 4, 20) ? MODE_PICK   : 
	          	MODE_IDLE;
		P : {
    		if (m == MODE_ATTACK) {
   				newMode = (charMode == MODE_IDLE) ? MODE_IDLE : MODE_ATTACK;
    			break P;
    		}
    		if (m == MODE_IDLE) {
   				newMode = (charMode == MODE_ATTACK && charMove == 1) ? MODE_ATTACK : MODE_IDLE;
    			break P;
    		}
			newMode = m;
		}
		return newMode;
	}

	/*
	 * 
	 * 
	 * 
	 */
	private int  mouseCellX = 5;
	private int  mouseCellY = 102;
    public int hoverCell(Cell cell) {
    	return hoverCell(cell._cx, cell._cy);
    }
    public int hoverCell(int _cx, int _cy) {
    	int r = -1;
    	mouseGotoCell(_cx, _cy);
    	sleep(20);
	    int[] pixels = MouseHelper.capturePixels(this, true, 10);
    	if (MouseHelper.isMatch(MouseHelper.ASSET_TARGETING, pixels, 4, 20)) {
    		r = charMode = MODE_TARGET;
    	}
    	else if (MouseHelper.isMatch(MouseHelper.ASSET_PICKING, pixels, 4, 20)) {
    		r = charMode = MODE_PICK;
    	}
    	return r;
    }
    public int cancel() {
    	int r = 0;
    	mouseGotoCell(mouseCellX, mouseCellY);
    	sleep(20);
		return r;
    }
    public int attack() {
    	int r = 0;
    	attackUpdateTime = attackStartTime = System.currentTimeMillis();
    	idleUpdateTime = 0;
    	charMode = MODE_ATTACK;
    	mouseLeftClick();
    	sleep(20);
    	mouseGotoCell(mouseCellX, mouseCellY);
    	sleep(20);
    	return r;
    }
    public int pick() {
    	int r = 0;
    	attackUpdateTime = attackStartTime = System.currentTimeMillis();
    	idleUpdateTime = 0;
    	charMode = MODE_PICK;
    	mouseLeftClick();
    	sleep(20);
    	mouseGotoCell(mouseCellX, mouseCellY);
    	sleep(20);
    	charMode = MODE_IDLE;
    	return r;
    }
    public int teleport() {
    	int r = 0;
    	idleUpdateTime = 0;
		keyPush(KeyEvent.VK_Z);
    	sleep(20);
		return r;
    }
    public int move(Cell cell) {
    	int r = 0;
    	idleUpdateTime = 0;
    	mouseGotoCell(cell._cx, cell._cy);
    	sleep(20);
    	mouseLeftClick();
    	sleep(20);
    	mouseGotoCell(mouseCellX, mouseCellY);
    	sleep(20);
    	return r;
    }
	
}