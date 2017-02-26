package com.hiandev.rosbot.scanner.v1;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import javax.imageio.ImageIO;
import com.hiandev.rosbot.scanner.Scanner;

public class ScannerXy extends Scanner {
    
    public ScannerXy(int x, int y, int w, int h) throws AWTException {
    	super (x, y, w, h);
    	this.cellMatrix = new Cell[_h / Cell.SIZE][_w / Cell.SIZE];
		this.cellCols = w / cellSize;
		this.cellRows = h / cellSize;
		this.cellData = new int[cellCols][cellRows][];
		this.cellPixelSize = cellSize * cellSize * 3;
		this.cellDead = new int[cellCols][cellRows];
		this.cellConvLevels = createCellConvLevels();
		this.zoneChar = new int[] {
	    		(_w / 2) -  30,
	    		(_w / 2) +  30,
	    		(_h / 2) -  80,
	    		(_h / 2) +  10 + 5
	    };
		this.zoneChat = new int[] { 0, 600, 500, 599 }; 
		this.zoneHpSp = new int[] {
	    		(_w / 2) - 30,
	    		(_w / 2) + 30,
	    		(_h / 2) + 10,
	    		(_h / 2) + 20
	    };
		this.zoneIdle = new int[] { 0, 30, 0, 40 };
    }

    /*
     * 
     * 
     * 
     */

    @Override
    protected void onPreExecute() {
    	cellDiff = new ArrayList<>();
		prepareCellDead();
    }
    @Override
	protected void onExecute() {
		super.onExecute();
		createCellMatrix();
//		strengthenDarkPixels();
		averageCellMatrix();
		removeSurfaceCellFromCellMatrix();
		clearNoise();
	}
    @Override
	protected void onPostExecute() {
    }
    @Override
    protected boolean onStart() {
    	return true;
    }
    @Override
    protected void onFinish() {
    	
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
	private int prmFloorThreshold = 10;
	private void createCellMatrix() {
		for (int x = 0; x < cellMatrix.length; x++) {
			for (int y = 0; y < cellMatrix[x].length; y++) {
				int[]    samples = getScreenImage().getRaster().getPixels(y * Cell.SIZE, x * Cell.SIZE, Cell.SIZE, Cell.SIZE, (int[]) null);
				cellMatrix[x][y] = new Cell(floorPixels(samples, prmFloorThreshold), prmFloorThreshold);
			}
		}
	}
	public int[] floorPixels(int[] samples, int threshold) {
		for (int x = 0; x < samples.length; x++) {
			samples[x] = (samples[x] / threshold) * threshold;
		}
		return samples;
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	
    public static class Cell {
    	
    	public static final int SIZE = 5;
    	
    	public final int[]   averagePixels = new int[] { -1, -1, -1 };
    	public final int[][] pixels;
    	public final int prmFloorThreshold;
    	
    	public Cell(int[] samples, int prmFloorThreshold) {
    		this.prmFloorThreshold = prmFloorThreshold;
    		int[] buffer = new int[3];
    		pixels = new int[SIZE][SIZE * 3];
    		int index = 0;
    		for (int x = 0; x < pixels.length; x += 1) {
    			for (int y = 0; y < pixels[x].length; y += 3) {
        			buffer[0] += (pixels[x][y + 0] = samples[index++]);
        			buffer[1] += (pixels[x][y + 1] = samples[index++]);
        			buffer[2] += (pixels[x][y + 2] = samples[index++]);
        		}
    		}
    		int divider = Cell.SIZE * Cell.SIZE;
    		averagePixels[0] = ((buffer[0] / divider) / prmFloorThreshold) * prmFloorThreshold;
    		averagePixels[1] = ((buffer[1] / divider) / prmFloorThreshold) * prmFloorThreshold;
    		averagePixels[2] = ((buffer[2] / divider) / prmFloorThreshold) * prmFloorThreshold;
    	}
    	
    	public final void createWhitePixels(boolean average) {
    		for (int x = 0; x < pixels.length; x += 1) {
    			for (int y = 0; y < pixels[x].length; y += 3) {
        			pixels[x][y + 0] = 255;
        			pixels[x][y + 1] = 255;
        			pixels[x][y + 2] = 255;
        		}
    		}
    		if (average) {
    			averagePixels[0] = 255;
    			averagePixels[1] = 255;
    			averagePixels[2] = 255;
    		}
    	}
    	
    	public final void createAveragePixels(boolean changePixels) {
    		int divider = Cell.SIZE * Cell.SIZE;
    		int[] rgb = new int[3];
    		for (int i = 0; i < pixels.length; i += 1) {
    			for (int j = 0; j < pixels[i].length; j += 3) {
    				rgb[0] += pixels[i][j + 0];
    				rgb[1] += pixels[i][j + 1];
    				rgb[2] += pixels[i][j + 2];
    			}
    		}
    		rgb[0] = ((rgb[0] / divider) / prmFloorThreshold) * prmFloorThreshold;
    		rgb[1] = ((rgb[1] / divider) / prmFloorThreshold) * prmFloorThreshold;
    		rgb[2] = ((rgb[2] / divider) / prmFloorThreshold) * prmFloorThreshold;
    		if (changePixels) {
	    		for (int i = 0; i < pixels.length; i += 1) {
	    			for (int j = 0; j < pixels[i].length; j += 3) {
	    				pixels[i][j + 0] = rgb[0];
	    				pixels[i][j + 1] = rgb[1];
	    				pixels[i][j + 2] = rgb[2];
	    			}
	    		}
    		}
    		averagePixels[0] = rgb[0];
    		averagePixels[1] = rgb[1];
    		averagePixels[2] = rgb[2];
        }
    	
    	public final void createAveragePixels(int prmFloorThreshold) {
    		int[] buffer = new int[3];
    		for (int x = 0; x < pixels.length; x += 1) {
    			for (int y = 0; y < pixels[x].length; y += 3) {
    				buffer[0] += pixels[x][y + 0];
    				buffer[1] += pixels[x][y + 1];
    				buffer[2] += pixels[x][y + 2];
        		}
    		}
    		int divider = Cell.SIZE * Cell.SIZE;
    		averagePixels[0] = ((buffer[0] / divider) / prmFloorThreshold) * prmFloorThreshold;
    		averagePixels[1] = ((buffer[1] / divider) / prmFloorThreshold) * prmFloorThreshold;
    		averagePixels[2] = ((buffer[2] / divider) / prmFloorThreshold) * prmFloorThreshold;
    	}
    	
    	public final int[] toInt1D(int x) {
    		int[] int1d = new int[Cell.SIZE * 3];
    		int index = 0;
			for (int y = 0; y < pixels[x].length; y += 3) {
    			int1d[index++] = pixels[x][y + 0];
    			int1d[index++] = pixels[x][y + 1];
    			int1d[index++] = pixels[x][y + 2];
    		}
    		return int1d; 
    	}
    	
    }
    
    /*
     * 
     * 
     * 
     */

    private int prmMaxDarkLevel = 100; 
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
    
//    private boolean containsEdge(Cell cell) {
//    	boolean r = false;
//    	for (int x = 0; x < cell.pixels.length; x++) {
//    		for (int y = 0; y < cell.pixels[x].length; y++) {
//        		int[] 
//        	}
//    	}
//    	return r;
//    }
    private void averageCellMatrix() {
		for (int x = 0; x < cellMatrix.length; x++) {
			for (int y = 0; y < cellMatrix[x].length; y++) {
				boolean dark = false;
				
				int[][] pixels = cellMatrix[x][y].pixels;
				P : for (int i = 0; i < pixels.length; i += 1) {
					for (int j = 0; j < pixels[i].length; j += 3) {
						if (pixels[i][j + 0] < prmMaxDarkLevel && 
							pixels[i][j + 1] < prmMaxDarkLevel &&
							pixels[i][j + 2] < prmMaxDarkLevel) {
								dark = true;
								break P;
							}
					}
				}
				
				cellMatrix[x][y].createAveragePixels(!dark);
			}
		}
    }
    
    /*
     * 
     * 
     * 
     */
    private long createSurfaceKey(int[] pixels) {
    	return ((pixels[0] * 1000000l) +  1000000000l) + 
			   ((pixels[1] *    1000l) +     1000000l) +
			   ((pixels[2] *       1l) +        1000l);
    }
    private void createSurfaceKeys(Cell cell, HashMap<Long, Integer> map) {
    	if (cell.averagePixels[0] > -1) {
			long    key = createSurfaceKey(cell.averagePixels);
			Integer val = map.get(key);
			map.put(key, val == null ? 1 : val.intValue() + 1);
    	}
    }
    private void removeSurfaceCellFromCellMatrix() {
    	HashMap<Long, Integer> map = new HashMap<>();
		for (int x = 0; x < cellMatrix.length; x++) {
			for (int y = 0; y < cellMatrix[x].length; y ++) {
				createSurfaceKeys(cellMatrix[x][y], map);
			}
		}
//		Set<String> keys = map.keySet();
//		for (String key : keys) { System.out.println(key + " > " + map.get(key)); }
		for (int x = 0; x < cellMatrix.length; x++) {
			for (int y = 0; y < cellMatrix[x].length; y++) {
				long    key = createSurfaceKey(cellMatrix[x][y].averagePixels);
				Integer val = map.get(key);
				if (val.intValue() > 10) {
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
    	boolean    r = false;
    	Cell    cell = cellMatrix[x][y];
    	int   offset = (filter.length - 1) / 2;
    	int[] pixels = filter[0 + offset][0 + offset];
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
					pixels = filter[i - (x - offset)][j - (y - offset)];
					if (i == x && j == y) {
						continue;
					}
					if (cellMatrix[i][j].averagePixels[0] != pixels[0] ||
					    cellMatrix[i][j].averagePixels[1] != pixels[1] ||
					    cellMatrix[i][j].averagePixels[2] != pixels[2]) {
						errorThreshold--;
						if (errorThreshold <= 0) {
							r = false;
							break P;	
						}
					}
				}
			}
    	}
    	return r;
    }
    private void clearNoise() {
    	for (int x = 1; x < cellMatrix.length - 1; x++) {
			for (int y = 1; y < cellMatrix[x].length - 1; y++) {
				boolean match = isMatch(cellMatrix, x, y, FILTER_NOISE, 1);
				if (match) {
					cellMatrix[x][y].createWhitePixels(true);
				}
			}
    	}
    }
    
    
    
    
    
    
    
    
    
    
    
    public final int[] zoneChar;
    public final int[] zoneIdle;
    public final int[] zoneHpSp;
    public final int[] zoneChat;
    private void prepareCellDead() {
    	for (int x = zoneChar[0]; x < zoneChar[1]; x++) {
    		for (int y = zoneChar[2]; y < zoneChar[3]; y++) {
    	    	cellDead[x / cellSize][y / cellSize] = 1;
    		}
    	}
    	for (int x = zoneIdle[0]; x < zoneIdle[1]; x++) {
    		for (int y = zoneIdle[2]; y < zoneIdle[3]; y++) {
    	    	cellDead[x / cellSize][y / cellSize] = 1;
    		}
    	}
    	for (int x = zoneChat[0]; x < zoneChat[1]; x++) {
    		for (int y = zoneChat[2]; y < zoneChat[3]; y++) {
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
	private ArrayList<int[]> cellDiff = new ArrayList<>(); // [ x, y, xSize, ySize ]
	private HashMap<String, String> cellConv = new HashMap<>(); // [ x:y, xyConvolution ]
	private int cellSize = 5;
    private int cellCols = 0;
    private int cellRows = 0;
    private int cellPixelSize = 0;
    private int cellChangeThreshold = 10;
	private int cellShrinkThreshold = 200; 
    private int cellMatchThreshold  = 10;
	private boolean isCellDataChanged(int cellX, int cellY, int[] samples) {
		boolean b = false;
		int[] current = cellData[cellX][cellY];
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
    private void convertScreenRasterToCellData() {
		for (int x = 0; x < _w; x  += cellSize) {
			for (int y = 0; y < _h; y += cellSize) {
				int cellX = x / cellSize;
				int cellY = y / cellSize;
				if (cellDead[cellX][cellY] == 1) {
					continue;
				}
				if (cellData[cellX][cellY] == null) {
					cellData[cellX][cellY] = shrinkCellData(getScreenImage().getRaster().getPixels(x, y, cellSize, cellSize, new int[cellPixelSize]));
				}
				else {
					int[] cellPixels = shrinkCellData(getScreenImage().getRaster().getPixels(x, y, cellSize, cellSize, new int[cellPixelSize]));
				  	if (isCellDataChanged(cellX, cellY, cellPixels)) {
						cellDiff.add(new int[] { x, y, cellSize, cellSize, cellX, cellY });
					}
					cellData[cellX][cellY] = cellPixels;
				}
			}
		}
    }
    
    public int getCellSize() {
    	return cellSize;
    }
    public int[][] getCellDead() {
    	return cellDead;
    }
    public int[][][] getCellData() {
    	return cellData;
    }
    public String getCellConv(int cellX, int cellY) {
    	return cellConv.get(cellX + ":" + cellY);
    }
	private void convertCellDiffToCellConv() {
		for (int x = 0; x < cellDiff.size(); x++) {
			int[] cell = cellDiff.get(x);
			int[] data = cellData[cell[4]][cell[5]];
			String key = cell[4] + ":" + cell[5];
			String val = conv(data, cellSize);
			cellConv.put(key, val);
		}
	}
	private String conv(int[] cellData, int cellSize) {
		StringBuilder  sb = new StringBuilder();
		int cellPixelSize = cellSize * 3;
		int[][]   array2D = toArray2D(cellData, cellPixelSize);
		for (int i = 0; i < cellConvLevels.length; i++) {
			int rowPad = (cellSize      - (cellConvLevels[i] * 1)) / 2;
			int colPad = (cellPixelSize - (cellConvLevels[i] * 3)) / 2;
			int[] rgb  = new int[3]; 
			int nCount = 0;
			for (int x = rowPad; x < array2D.length - rowPad; x += 1) {
				for (int y = colPad; y < array2D[x].length - colPad; y += 3) {
					rgb[0] += array2D[x][y + 0];
					rgb[1] += array2D[x][y + 1];
					rgb[2] += array2D[x][y + 2];
					nCount += 1;
				}
			}
			sb.append(toHexString(optimizeConv(rgb[0] / nCount)))
			  .append(toHexString(optimizeConv(rgb[1] / nCount)))
			  .append(toHexString(optimizeConv(rgb[2] / nCount))).append(":");
		}
		return sb.toString();
	}
	private int optimizeConv(int rgb) {
		return (rgb / 10) * 10;
	}
	private String toHexString(int rgb) {
		String hex = Integer.toHexString(rgb);
		return hex.length() < 2 ? ("0" + hex) : hex;
	}
	private int[][] toArray2D(int[] array1D, int cols) {
		int rows = array1D.length / cols;
		int[][] array = new int[rows][cols];
		for (int x = 0; x < array1D.length; x += cols) {
			int row = x / cols;
			for (int y = 0; y < cols; y++) {
				array[row][y] = array1D[x + y];
			}
		}
		return array;
	}
	private int[] cellConvLevels = null;
	private int[] createCellConvLevels() {
		int level = cellSize;
		ArrayList<Integer> temp = new ArrayList<>();
		temp.add(level);
		while (level > 2) {
			level = level / 2;
			if (level % 2 == 1) {
				level -= 1;
			}
			temp.add(level);
		}
		int[] r = new int[temp.size()];
		for (int x = 0; x < temp.size(); x++) {
			r[x] = temp.get(x);
		}
		return r;
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
			int dist = Math.abs(midX - cell[4]) + Math.abs(midY - cell[5]);
			if (map.get(dist) == null) {
				map.put(dist, new ArrayList<int[]>());
			}
			map.get(dist).add(cell);
		}
		ArrayList<int[]> result = new ArrayList<>();
		for (int x = 0; x < midX + midY; x++) {
			if (map.get(x) != null) {
				result.addAll(map.get(x));
			}
			if (limit > 0 && result.size() > limit) {
				while (result.size() > limit) {
					result.remove(result.size() - 1);
				}
				break;
			}
		}
		return result;
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
    
    public BufferedImage createFinalImage() {
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
    
    public static void main(String[] args) {
    	try {
    		ScannerXy scanner = new ScannerXy(20, 40, 800, 600);
    		
    		scanner.onExecute();
    		/*
			 * 
			 */
			BufferedImage bw  = scanner.createFinalImage();
			ImageIO.write(bw, "png", new File("./test-" + System.currentTimeMillis() + ".png"));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
}
