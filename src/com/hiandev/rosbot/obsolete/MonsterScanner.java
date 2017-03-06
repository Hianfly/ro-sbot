package com.hiandev.rosbot.obsolete;
//package com.hiandev.rosbot.scanner;
//
//import java.awt.AWTException;
//import java.awt.Point;
//import java.awt.image.BufferedImage;
//import java.awt.image.ColorModel;
//import java.awt.image.Raster;
//import java.awt.image.WritableRaster;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import com.hiandev.rosbot.util.Config;
//
//public class MonsterScanner extends Scanner {
//
//    private String prmMonsterColorJsonPath = "monster-colors.json";
//    
//    public MonsterScanner(int _x, int _y) throws AWTException {
//    	super (_x, _y, 800, 600);
//    	this.cellSize       = (_h / Cell.SIZE) * (_w / Cell.SIZE);
//    	this.midCellRow     = ((_h / 2) / Cell.SIZE) - 4;
//    	this.midCellCol     = ((_w / 2) / Cell.SIZE) - 1;
//    	this.cellMatrixOld  = new Cell  [_h / Cell.SIZE][_w / Cell.SIZE];
//    	this.cellMatrixNew  = new Cell  [_h / Cell.SIZE][_w / Cell.SIZE];
//    	this.cellNeutral    = new int   [_h / Cell.SIZE][_w / Cell.SIZE];
//    	this.cellMotion     = new int   [_h / Cell.SIZE][_w / Cell.SIZE];
//    	this.cellMonster    = new String[_h / Cell.SIZE][_w / Cell.SIZE];
//    	setInterval(10);
//    }
//    @Override
//    public boolean onStart() {
//    	boolean s = super.onStart();
//    	createNeutralCell();
//    	createMonsterColors();
//    	return s;
//    }
//    @Override
//	protected void onExecute() {
//		super.onExecute();
//		try {
//			createCellMatrix();
//			averageCellMatrixPixels();
//			createMonsterCell();
//			turnNonMotionCellWhite();
////			removeCellSurface();
////			removeNotClickableCell();
////			removeCellNoise();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//    
//    /*
//     * 
//     * 
//     * 
//     */
//
//    private int midCellRow = 0;
//	private int midCellCol = 0;
//	private int cellSize = 0;
//	
//	/*
//	 * 
//	 * 
//	 * 
//	 */
//    private int[][] cellNeutral = null;   
//    private void createNeutralCell() {
//    	// Battle Msg
//    	for (int x = 100; x < 120; x++) {
//    		for (int y = 0; y < 160; y++) {
//    	    	cellNeutral[x][y] = 1;
//    		}
//    	}
//    	// Main Character
//    	for (int x = 46; x < 62; x++) {
//    		for (int y = 75; y < 85; y++) {
//    	    	cellNeutral[x][y] = 1;
//    		}
//    	}
//    	// Toa
//    	for (int x = 10; x < 18; x++) {
//    		for (int y = 29; y < 131; y++) {
//    	    	cellNeutral[x][y] = 1;
//    		}
//    	}
//    	// Cash Shop
//    	for (int x = 0; x < 24; x++) {
//    		for (int y = 112; y < 133; y++) {
//    	    	cellNeutral[x][y] = 1;
//    		}
//    	}
//    	// Map
//    	for (int x = 0; x < 33; x++) {
//    		for (int y = 133; y < 160; y++) {
//    	    	cellNeutral[x][y] = 1;
//    		}
//    	}
//    	// Buff List
//    	for (int x = 33; x < 120; x++) {
//    		for (int y = 150; y < 160; y++) {
//    	    	cellNeutral[x][y] = 1;
//    		}
//    	}
//    	// ???
//    	for (int x = 0; x < 120; x++) {
//    		for (int y = 0; y < 1; y++) {
//    	    	cellNeutral[x][y] = 1;
//    		}
//    	}
//    	for (int x = 0; x < 120; x++) {
//    		for (int y = 159; y < 160; y++) {
//    	    	cellNeutral[x][y] = 1;
//    		}
//    	}
//    	for (int x = 0; x < 1; x++) {
//    		for (int y = 0; y < 160; y++) {
//    	    	cellNeutral[x][y] = 1;
//    		}
//    	}
//    	for (int x = 119; x < 120; x++) {
//    		for (int y = 0; y < 160; y++) {
//    	    	cellNeutral[x][y] = 1;
//    		}
//    	}
//    	
//    }
//    
//    /*
//     * 
//     * 
//     * 
//     */
//    private String[][] cellMonster = null;
//    private ConcurrentHashMap<Long, String> monsterColors = new ConcurrentHashMap<Long, String>();
//    private void createMonsterColors() {
//    	JSONObject json = Config.loadJSON(prmMonsterColorJsonPath);
//    	if (json != null) {
//        	monsterColors.clear();
//        	JSONObject monsters = json.getJSONObject("monsters");
//        	Set<String> keys = monsters.keySet();
//    		for (String key : keys) {
//    			JSONArray arr = monsters.getJSONArray(key);
//    			for (int x = 0; x < arr.length(); x += 3) {
//    				int[] pixels = new int[] {
//        					Integer.parseInt(arr.get(x + 0).toString()),
//        					Integer.parseInt(arr.get(x + 1).toString()),
//        					Integer.parseInt(arr.get(x + 2).toString()),
//    				};
//    				monsterColors.put(Cell.createAveragePixelsKey(pixels), key);
//    			}
//    		}
//    	}
//    }
//    private void createMonsterCell() {
//    	for (int r = 0; r < cellMonster.length; r++) {
//			for (int c = 0; c < cellMonster[r].length; c++) {
//				cellMonster[r][c] = null;
//			}
//    	}
//    	StringBuilder sb = new StringBuilder();
//    	for (int r = 0; r < cellMotion.length; r++) {
//			for (int c = 0; c < cellMotion[r].length; c++) {
//				if (cellMotion[r][c] == 0) {
//					continue;
//				}
//				long   key = Cell.createAveragePixelsKey(cellMatrixNew[r][c].averagePixels);
//				String mon = monsterColors.get(key);
//				if (mon != null) {
//					sb.append(mon).append(", ");
//					cellMonster[r][c] = mon;
//				}
//			}
//    	}
//    	if (sb.length() > 0) {
////    		System.out.println(sb.toString());
//    	}
//    }
//    public String[][] getMonsterCell() {
//    	return cellMonster;
//    }
//    
//
//    /*
//     * 
//     * 
//     * 
//     */
//	private Cell[][] cellMatrixNew = null;
//	private Cell[][] cellMatrixOld = null;
//	public Cell[][] getCellMatrix() {
//		return cellMatrixNew;
//	}
//	private void createCellMatrix() {
//		motionSize = 0;
//		for (int r = 0; r < cellMatrixNew.length; r++) {
//			for (int c = 0; c < cellMatrixNew[r].length; c++) {
//				int[] pixels = getScreenImage().getRaster().getPixels(c * Cell.SIZE, r * Cell.SIZE, Cell.SIZE, Cell.SIZE, (int[]) null);
//				Cell  cell   = new Cell(c, r, pixels);
//				if (isMotion(cellMatrixOld[r][c], cell)) {
//					cellMotion[r][c] = 1; motionSize++;
//				}
//				else {
//					cellMotion[r][c] = 0;
//				}
//				cellMatrixOld[r][c] = cell;
//				cellMatrixNew[r][c] = new Cell(cell);
//			}
//		}
//	}
//    private void averageCellMatrixPixels() {
//		for (int x = 0; x < cellMatrixNew.length; x++) {
//			for (int y = 0; y < cellMatrixNew[x].length; y++) {
//				if (cellNeutral[x][y] == 1) {
//					continue;
//				}
//				cellMatrixNew[x][y].createAveragePixels(true,  5);	
//			}
//		}
//    }
//	
//	/*
//	 * 
//	 * 
//	 * 
//	 */
//	private int[][] cellMotion = null;
//	private int motionSize = 0;
//	private boolean isMotion(Cell oldCell, Cell newCell) {
//		boolean r = false;
//		if (oldCell != null &&
//			oldCell.averagePixels[0] != newCell.averagePixels[0] && 
//			oldCell.averagePixels[1] != newCell.averagePixels[1] &&
//			oldCell.averagePixels[2] != newCell.averagePixels[2]) {
//			r = true;
//		}
//		return r;
//	}
//    private void turnNonMotionCellWhite() {
//		for (int r = 0; r < cellMatrixNew.length; r++) {
//			for (int c = 0; c < cellMatrixNew[r].length; c++) {
//				if (cellNeutral[r][c] == 1) {
//					continue;
//				}
//				if (cellMotion[r][c] == 1) {
//					continue;
//				}
//				cellMatrixNew[r][c].createWhitePixels(true);
//    		}
//		}
//    }
//    
//    /*
//     *
//     * 
//     * 
//     */
//    public BufferedImage createCellMatrixImage() {
//		int[] pixels = new int[_w * _h * 3];
//		int   index  = 0;
//		for (int x = 0; x < cellMatrixNew.length; x++) {
//			for (int z = 0; z < Cell.SIZE; z++) {
//				for (int y = 0; y < cellMatrixNew[x].length; y++) {
//					if (cellMatrixNew[x][y] == null) {
//						continue;
//					}
//					int[] int1d = cellMatrixNew[x][y].toInt1D(z);
//					for (int i : int1d) {
//						pixels[index++] = i;
//					}
//				}
//			}
//		}
//		BufferedImage bi = getScreenImage();
//		ColorModel cm = bi.getColorModel();
//		WritableRaster raster = Raster.createWritableRaster(bi.getRaster().getSampleModel(), new Point(0, 0));
//		raster.setPixels(0, 0, bi.getWidth(), bi.getHeight(), pixels);
//		return new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
//    }
//    public void mouseIdle() {
//    	mouseGotoCell(5, 102);
//    }
//    public int getDistanceToCenter(int _x, int _y) {
//    	return Math.abs(midCellCol - _x) + Math.abs(midCellRow - _y);
//    }
//    public int getCellSize() {
//    	return cellSize;
//    }
//    public int getMotionSize() {
//    	return motionSize;
//    }
//    public static void main(String[] args) {
//    	try {
////    		ScreenRobot sr = new ScreenRobot(5, 20);
////    		sr.onCaptureScreen();
////    		BattleScanner scanner = new BattleScanner(sr);
////    		scanner.onExecute();
////    		/*
////			 * 
////			 */
////			BufferedImage bw  = scanner.createCellMatrixImage();
////			ImageIO.write(bw, "png", new File("./test-" + System.currentTimeMillis() + ".png"));
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}
//    }
//    
//    
//}
