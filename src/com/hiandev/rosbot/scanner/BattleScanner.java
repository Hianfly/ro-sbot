package com.hiandev.rosbot.scanner;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.hiandev.rosbot.profiler.BattleProfiler;

public class BattleScanner extends Scanner {

	private int prmFloorThreshold   = 1;
    private int prmMaxDarkLevel     = 25; 
    private int prmSurfaceThreshold = 250;
    
    public BattleScanner(int _x, int _y) throws AWTException {
    	super (_x, _y, 800, 600);
    	this.battleProviler    = new BattleProfiler();
    	this.cellMatrix        = new Cell[_h / Cell.SIZE][_w / Cell.SIZE];
    	this.neutralCellMatrix = new int [_h / Cell.SIZE][_w / Cell.SIZE];
    	setInterval(10);
    }
    
    /*
     * 
     * 
     * 
     */

    @Override
    public boolean onStart() {
    	boolean s = super.onStart();
    	createNeutralCellMatrix();
    	return s;
    }
    
    @Override
	protected void onExecute() {
		super.onExecute();
		try {
			createCellMatrix();
	//		strengthenDarkPixels();
			createCellAveragePixels();
			removeCellSurface();
			removeNotClickableCell();
//			removeCellNoise();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private final BattleProfiler battleProviler;
    public BattleProfiler getBattleProfiler() {
    	return battleProviler;
    }
    private void removeNotClickableCell() {
    	if (battleProviler != null) {
    		for (int r = 0; r < cellMatrix.length; r++) {
    			for (int c = 0; c < cellMatrix[r].length; c++) {
    				if (neutralCellMatrix[r][c] == 1) {
    					continue;
    				}
    				Cell cell = cellMatrix[r][c];
//    				if (isDark(cell)) {
//    					continue;
//    				}
    	    		long p = battleProviler.get(cell, BattleProfiler.PROFILE_NOT_CLICKABLE);
    	    		if (p > 0) {
    	    			cell.createWhitePixels(true);
    	    		}
        		}
    		}
    	}
    }
    
    /*
     * 
     * 
     * 
     */
    
	private Cell[][] cellMatrix = null;
	public Cell[][] getCellMatrix() {
		return cellMatrix;
	}
	private void createCellMatrix() {
		for (int r = 0; r < cellMatrix.length; r++) {
			for (int c = 0; c < cellMatrix[r].length; c++) {
				int[]    samples = getScreenImage().getRaster().getPixels(c * Cell.SIZE, r * Cell.SIZE, Cell.SIZE, Cell.SIZE, (int[]) null);
				cellMatrix[r][c] = new Cell(c, r, floorPixels(samples, prmFloorThreshold));
			}
		}
	}
	public List<Cell> getNonWhiteCellMatrix() {
		List<Cell> list = new ArrayList<>();
		HashMap<Integer, ArrayList<Cell>> map = new HashMap<>();
		int midx = ((_w / 2) / Cell.SIZE) - 1;
		int midy = ((_h / 2) / Cell.SIZE) - 4;
		for (int r = 0; r < cellMatrix.length; r++) {
			for (int c = 0; c < cellMatrix[r].length; c++) {
				if (neutralCellMatrix[r][c] == 1) {
					continue;
				}
				Cell cell = cellMatrix[r][c];
				if (cell.averagePixels[0] == 255 && 
					cell.averagePixels[1] == 255 && 
					cell.averagePixels[2] == 255) {
					continue;
				}
				int dist = Math.abs(midx - cell._x) + Math.abs(midy - cell._y);
				if (map.get(dist) == null) {
					map.put(dist, new ArrayList<Cell>());
				}
				map.get(dist).add(cell);
			}
		}
		for (int x = 0; x < midx + midy; x++) {
			if (map.get(x) != null) {
				list.addAll(map.get(x));
			}
		}
		return list;
	}
	
	/*
	 * 
	 * 
	 * 
	 */

    private int[][] neutralCellMatrix = null;   
    private void createNeutralCellMatrix() {
    	// Battle Msg
    	for (int x = 100; x < 120; x++) {
    		for (int y = 0; y < 160; y++) {
    	    	neutralCellMatrix[x][y] = 1;
    		}
    	}
    	// Main Character
    	for (int x = 46; x < 62; x++) {
    		for (int y = 75; y < 85; y++) {
    	    	neutralCellMatrix[x][y] = 1;
    		}
    	}
    	// Toa
    	for (int x = 10; x < 18; x++) {
    		for (int y = 29; y < 131; y++) {
    	    	neutralCellMatrix[x][y] = 1;
    		}
    	}
    	// Cash Shop
    	for (int x = 5; x < 18; x++) {
    		for (int y = 118; y < 131; y++) {
    	    	neutralCellMatrix[x][y] = 1;
    		}
    	}
    	// Map
    	for (int x = 5; x < 33; x++) {
    		for (int y = 133; y < 157; y++) {
    	    	neutralCellMatrix[x][y] = 1;
    		}
    	}
    	// Buff List
    	for (int x = 33; x < 120; x++) {
    		for (int y = 150; y < 157; y++) {
    	    	neutralCellMatrix[x][y] = 1;
    		}
    	}
    	// ???
    	for (int x = 0; x < 120; x++) {
    		for (int y = 0; y < 1; y++) {
    	    	neutralCellMatrix[x][y] = 1;
    		}
    	}
    	for (int x = 0; x < 120; x++) {
    		for (int y = 159; y < 160; y++) {
    	    	neutralCellMatrix[x][y] = 1;
    		}
    	}
    	for (int x = 0; x < 1; x++) {
    		for (int y = 0; y < 160; y++) {
    	    	neutralCellMatrix[x][y] = 1;
    		}
    	}
    	for (int x = 119; x < 120; x++) {
    		for (int y = 0; y < 160; y++) {
    	    	neutralCellMatrix[x][y] = 1;
    		}
    	}
    	
    }
    
    /*
     * 
     * 
     * 
     */
    private void strengthenDarkPixels() {
		for (int x = 0; x < cellMatrix.length; x++) {
			for (int y = 0; y < cellMatrix[x].length; y++) {
				strengthenDarkPixels(cellMatrix[x][y]);
			}
		}
    }
    private void strengthenDarkPixels(Cell cell) {
    	for (int x = 0; x < cell.pixels.length; x += 1) {
			for (int y = 0; y < cell.pixels[x].length; y += 3) {
				int r = cell.pixels[x][y + 0];
				int g = cell.pixels[x][y + 1];
				int b = cell.pixels[x][y + 2];
				if (r < prmMaxDarkLevel && g < prmMaxDarkLevel && b < prmMaxDarkLevel) {
					cell.pixels[x][y + 0] = r <= prmMaxDarkLevel ? 0 : r;
					cell.pixels[x][y + 1] = g <= prmMaxDarkLevel ? 0 : g;
					cell.pixels[x][y + 2] = b <= prmMaxDarkLevel ? 0 : b;
				}
			}
		}
    }
    
    /*
     * 
     * 
     * 
     */
    private boolean isDark(Cell cell) {
    	boolean dark = false;
		P : for (int i = 0; i < cell.pixels.length; i += 1) {
			for (int j = 0; j < cell.pixels[i].length; j += 3) {
				if (cell.pixels[i][j + 0] < prmMaxDarkLevel && 
					cell.pixels[i][j + 1] < prmMaxDarkLevel &&
					cell.pixels[i][j + 2] < prmMaxDarkLevel) {
						dark = true;
						break P;
				}
			}
		}
    	return dark;
    }
    private void createCellAveragePixels() {
		for (int x = 0; x < cellMatrix.length; x++) {
			for (int y = 0; y < cellMatrix[x].length; y++) {
				if (neutralCellMatrix[x][y] == 1) {
					continue;
				}
				boolean dark = isDark(cellMatrix[x][y]);
				if (dark) {
					cellMatrix[x][y].createAveragePixels(false, 5);
				}
				else {
					cellMatrix[x][y].createAveragePixels(true,  5);	
				}
			}
		}
    }
    
    /*
     * 
     * 
     * 
     */
    
    private void createSurfaceKeys(Cell cell, HashMap<Long, Integer> map) {
    	if (cell.averagePixels[0] > -1) {
			long    key = Cell.createAveragePixelsKey(cell.averagePixels);
			Integer val = map.get(key);
			map.put(key, val == null ? 1 : val.intValue() + 1);
    	}
    }
    private void removeCellSurface() {
    	HashMap<Long, Integer> map = new HashMap<>();
		for (int x = 0; x < cellMatrix.length; x++) {
			for (int y = 0; y < cellMatrix[x].length; y ++) {
				if (neutralCellMatrix[x][y] == 1) {
					continue;
				}
				createSurfaceKeys(cellMatrix[x][y], map);
			}
		}
//		Set<String> keys = map.keySet();
//		for (String key : keys) { System.out.println(key + " > " + map.get(key)); }
		for (int x = 0; x < cellMatrix.length; x++) {
			for (int y = 0; y < cellMatrix[x].length; y++) {
				if (neutralCellMatrix[x][y] == 1) {
					continue;
				}
				long    key = Cell.createAveragePixelsKey(cellMatrix[x][y].averagePixels);
				Integer val = map.get(key);
				if (val.intValue() > prmSurfaceThreshold) {
					cellMatrix[x][y].createWhitePixels(true);
				}
			}
		}
    }
    
    /*
     * 
     * 
     * 
     */
    private final int[][][] FILTER_NOISE = new int[][][] {
    	{ { 255, 255, 255 }, { 255, 255, 255 }, { 255, 255, 255} },
    	{ { 255, 255, 255 }, {-255,-255,-255 }, { 255, 255, 255} },
    	{ { 255, 255, 255 }, { 255, 255, 255 }, { 255, 255, 255} },
    };
    private boolean isMatch(Cell[][] cellMatrix, int x, int y, int[][][] filter, int errorThreshold) {
    	boolean      r = false;
    	Cell      cell = cellMatrix[x][y];
		Cell neighbour = null;
    	int     offset = (filter.length - 1) / 2;
    	int[]   pixels = filter[0 + offset][0 + offset];
    	if (pixels[0] < 0 && pixels[1] < 0 && pixels[2] < 0) {
			if (cell.averagePixels[0] != -pixels[0] &&
				cell.averagePixels[1] != -pixels[1] &&
				cell.averagePixels[2] != -pixels[2]) {
				r = true;
			}
		}
    	else {
    		if (cell.averagePixels[0] == pixels[0] &&
				cell.averagePixels[1] == pixels[1] &&
				cell.averagePixels[2] == pixels[2]) {
				r = true;
			}
    	}
    	if (r) {
			P : for (int i = x - offset; i < x + offset + 1; i++) {
				for (int j = y - offset; j < y + offset + 1; j++) {
					if (i == x && j == y) {
						continue;
					}
					pixels    = filter[i - (x - offset)][j - (y - offset)];
					neighbour = cellMatrix[i][j];
					if (neighbour.averagePixels[0] != pixels[0] ||
						neighbour.averagePixels[1] != pixels[1] ||
						neighbour.averagePixels[2] != pixels[2]) {
						if (--errorThreshold <= 0) {
							r = false;
							break P;	
						}
					}
				}
			}
    	}
    	return r;
    }
    private void removeCellNoise() {
    	for (int x = 1; x < cellMatrix.length - 1; x++) {
			for (int y = 1; y < cellMatrix[x].length - 1; y++) {
				if (neutralCellMatrix[x][y] == 1) {
					continue;
				}
				boolean match = isMatch(cellMatrix, x, y, FILTER_NOISE, 1);
				if (match) {
					cellMatrix[x][y].createWhitePixels(true);
				}
			}
    	}
    }
    
    /*
     *
     * 
     * 
     */
    
    public BufferedImage createCellMatrixImage() {
		int[] pixels = new int[_w * _h * 3];
		int   index  = 0;
		for (int x = 0; x < cellMatrix.length; x++) {
			for (int z = 0; z < Cell.SIZE; z++) {
				for (int y = 0; y < cellMatrix[x].length; y++) {
					if (cellMatrix[x][y] == null) {
						continue;
					}
					int[] int1d = cellMatrix[x][y].toInt1D(z);
					for (int i : int1d) {
						pixels[index++] = i;
					}
				}
			}
		}
		BufferedImage bi = getScreenImage();
		ColorModel cm = bi.getColorModel();
		WritableRaster raster = Raster.createWritableRaster(bi.getRaster().getSampleModel(), new Point(0, 0));
		raster.setPixels(0, 0, bi.getWidth(), bi.getHeight(), pixels);
		return new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
    }
    public void mouseIdle() {
    	mouseGotoCell(5, 102);
    }

    
    
    
    
    
    
    public static void main(String[] args) {
    	try {
//    		ScreenRobot sr = new ScreenRobot(5, 20);
//    		sr.onCaptureScreen();
//    		BattleScanner scanner = new BattleScanner(sr);
//    		scanner.onExecute();
//    		/*
//			 * 
//			 */
//			BufferedImage bw  = scanner.createCellMatrixImage();
//			ImageIO.write(bw, "png", new File("./test-" + System.currentTimeMillis() + ".png"));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
}
