package com.hiandev.rosbot;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Scanner {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
    public final int _x;
    public final int _y;
    public final int _w;
    public final int _h;
    
    public Scanner(int x, int y, int w, int h) throws AWTException {
    	this._x = x;
    	this._y = y;
    	this._w = w;
    	this._h = h;
    	this.robot = new Robot();
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

	public long interval = 1;
	public boolean running = false;
	public void start() {
		preapare();
		mListener.onStart();
		running = true;
		while (running) {
	    	cellDiff = new ArrayList<>();
			mListener.onPreExecute();
			execute();
			mListener.onPostExecute();
		    sleep(interval);
		}
		mListener.onFinish();
	}
	public void execute() {
		screenRaster = captureScreen();

   	
	    convertScreenRasterToCellData();
	    convertCellDiffToCellConv();
	}
    private void preapare() {
		prepareCellDead();
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
	private int cellSize = 10;
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
					cellData[cellX][cellY] = shrinkCellData(screenRaster.getPixels(x, y, cellSize, cellSize, new int[cellPixelSize]));
				}
				else {
					int[] cellPixels = shrinkCellData(screenRaster.getPixels(x, y, cellSize, cellSize, new int[cellPixelSize]));
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

	private Robot robot = null;
	private WritableRaster screenRaster = null;
	public WritableRaster getScreenRaster() {
		return screenRaster;
	}
    public WritableRaster captureScreen() {
	    return (WritableRaster) robot.createScreenCapture(new Rectangle(_x, _y, _w, _h)).getRaster();
    }
	public void mouseIdle() {
//    	robot.mouseMove(_x + 1, _y + 1);
    	robot.mouseMove(_x + 1 + 60, _y + zoneChat[2] + 1 + 10);
    }
	public void mouseClick() {
    	robot.mousePress(InputEvent.BUTTON1_MASK);
    	sleep(20);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
	public void mouseGoto(int x, int y) {
    	robot.mouseMove(x, y);
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
    
    private ScannerListener mListener = null;
    public void setScannerListener(ScannerListener listener) {
    	mListener = listener;
    }
    public static interface ScannerListener {

    	public void onStart();
    	public void onPreExecute();
    	public void onPostExecute();
    	public void onFinish();
    	
    }
    
}
