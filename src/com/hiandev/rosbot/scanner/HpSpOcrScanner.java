package com.hiandev.rosbot.scanner;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class HpSpOcrScanner extends Scanner {

    public HpSpOcrScanner(int _x, int _y) throws AWTException {
    	super (_x, _y, 170, 30);
    	setInterval(5000);
    }

    @Override
    protected void onPreExecute() {
    	super.onPreExecute();
    	loadAssets();
    }
    
    @Override
	protected void onExecute() {
		super.onExecute();
		try {
			createPixels(10);
			filterPixelLines();
			filterPixelSpaces();
			createCharMatrixList();
			createSentenceList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /*
     * 
     * 
     * 
     */
    private int[][] pixels = null;
    private void createPixels(int floorThreshold) {
    	pixels = new int[_h][_w * 3];
		for (int y = 0; y < _h; y++) {
			pixels[y] = floorPixels(getScreenImage().getRaster().getPixels(0, y, _w, 1, (int[]) null), floorThreshold);
		}
    }
    private int[] textColor   = new int[] {   0, 0, 0 };
    private int[] filterColor = new int[] { 255, 0, 0 };
    private void filterPixelSpaces() {
		for (int z = 0; z < pixels.length; z++) {
			if (Pixel.isMatch(pixels[z], 0, filterColor[0], filterColor[1], filterColor[2])) {
				continue;
			}
			for (int x = 0; x < pixels[z].length; x += 3) {
				boolean line = true;
				int     rows = 0;
				for (int y = z; y < pixels.length; y += 1) {
					if (Pixel.isMatch(pixels[y], x, filterColor[0], filterColor[1], filterColor[2])) {
						break;
					}
					rows++;
					if (Pixel.isMatch(pixels[y], x, textColor[0], textColor[1], textColor[2])) {
						line = false;
						break ;
					}
				}
				if (line) {
					for (int y = z; y < z + rows; y += 1) {
						Pixel.setPixel(pixels[y], x, filterColor[0], filterColor[1], filterColor[2]);
					}
				}	
			}
		}
    }
    private void filterPixelLines() {
		for (int y = 0; y < pixels.length; y++) {
			boolean line = true;
			L : for (int x = 0; x < pixels[y].length; x += 3) {
				if (Pixel.isMatch  (pixels[y], x, textColor[0], textColor[1], textColor[2])) {
					line = false;
					break L;
				}
			}
			if (line) {
				for (int x = 0; x < pixels[y].length; x += 3) {
					Pixel.setPixel (pixels[y], x, filterColor[0], filterColor[1], filterColor[2]);
				}
			}
		}
    }
    
    /*
     * 
     * 
     * 
     */
    private ArrayList<int[][]> charMatrixList = new ArrayList<>();
    private void createCharMatrixList() {
    	charMatrixList.clear();
		for (int y = 0; y < pixels.length; y++) {
			int w = 0;
			int h = 0;
			for (int x = 0; x < pixels[y].length; x += 3) {
				if (Pixel.isMatch(pixels[y], x, filterColor[0], filterColor[1], filterColor[2])) {
					continue;
				}
				w = createCharMatrixWidth (x, y);
				h = createCharMatrixHeight(x, y);
				charMatrixList.add(createCharMatrix(x, y, w, h));
				x += (w * 3) - 3;
			}
			if (h != 0) {
				y += h - 1;
				charMatrixList.add(createNewLineAsset());
						
			}
		}
    }
    private int[][] createCharMatrix(int x, int y, int w, int h) {
    	int[][] charMatrix = new int[h][w * 3];
		int ih = 0;
		int iw = 0;
		for (int j = y; j < y + charMatrix.length; j += 1) {
			iw = 0;
			for (int i = x; i < x + charMatrix[0].length; i += 3) {
				if (Pixel.isMatch(pixels[j], i, filterColor[0], filterColor[1], filterColor[2])) {
					break;
				}
				charMatrix[ih][iw + 0] = pixels[j][i + 0];
				charMatrix[ih][iw + 1] = pixels[j][i + 1];
				charMatrix[ih][iw + 2] = pixels[j][i + 2];
				iw += 3;
			}
			ih += 1;
		}
		return charMatrix;
    }
    private int createCharMatrixWidth(int x, int y) {
    	int w = 0;
    	for (int i = x; i < pixels[y].length; i += 3) {
			if (Pixel.isMatch(pixels[y], i, filterColor[0], filterColor[1], filterColor[2])) {
				break;
			}
			w++;
		}
    	return w;
    }
    private int createCharMatrixHeight(int x, int y) {
    	int h = 0;
    	for (int j = y; j < pixels.length; j += 1) {
			if (Pixel.isMatch(pixels[j], x, filterColor[0], filterColor[1], filterColor[2])) {
				break;
			}
			h++;
		}
    	return  h;
    }
    private int[][] createNewLineAsset() {
    	return new int[][] { { 0, 0, 0 } };
    }
    private int[][] createSpaceAsset() {
    	return new int[][] { { 1, 1, 1 } };
    }
    
    /*
     * 
     * 
     * 
     */
    private ArrayList<String> sentenceList = new ArrayList<>();
    private void createSentenceList() {
    	sentenceList.clear();
    	StringBuilder sb = new StringBuilder();
		for (int x = 0; x < charMatrixList.size(); x++) {
			String key = toAssetString(charMatrixList.get(x));
			if (key.equals("1,")) {
				sentenceList.add(sb.toString());
				sb = new StringBuilder();
			}
			if (key.equals("0,")) {
				sb.append(" ");
			}
			String val = ASSET_MAP.get(key);
			if (val != null) {
				sb.append(val);
			}
		}
    }

    /*
     * 
     * 
     * 
     */
    private final ConcurrentHashMap<String, String> ASSET_MAP = new ConcurrentHashMap<>();
    private void loadAssets() {
    	ASSET_MAP.clear();
		File[] assets = new File("./assets/chars/").listFiles();
		for (File file : assets) {
			BufferedReader br = null;
    		StringBuilder sb = null;
    		String ln = null;
	    	try {
	    		br = new BufferedReader(new FileReader(file));
	    		sb = new StringBuilder();
	    		while ((ln = br.readLine()) != null) {
	    			sb.append(ln).append("\n");
	    		}
	    		sb.deleteCharAt(sb.length() - 1);
	    		String[] e = sb.toString().split(":");
	    		ASSET_MAP.put(e[1].trim(), e[0]);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	} finally {
	    		try {
	    			if (br != null) {
	    				br.close();
	    			}
	    		} catch (IOException ioe) {
	    		}
	    	}
		}
    }
    private String toAssetString(int[][] charMatrix) {
    	StringBuilder sb = new StringBuilder();
    	for (int y = 0; y < charMatrix.length; y += 1) {
    		for (int x = 0; x < charMatrix[y].length; x += 3) {
    			if (Pixel.isMatch(charMatrix[y], x, textColor[0], textColor[1], textColor[2])) {
            		sb.append("1");
    			}
    			else {
    				sb.append("0");
    			}
        	}
    		sb.append(",\n");
    	}
    	sb.deleteCharAt(sb.length() - 1);
    	return sb.toString();
    }

    /*
     * 
     * 
     * 
     */
    private int mHpPercentage = 100;
    public final int getHpPercentage() {
    	return mHpPercentage;
    }
    
    
    
    public BufferedImage createImage() {
    	return toBufferedImage(pixels);
    }
    
    
    
    
    
    
    
    

	
	/*
	 * 
	 * 
	 */
//	if (!dump) {
//		dump = true;
//		for (int x = 0; x < rowsInfo.size(); x++) {
//			int[][] charMatrix = rowsInfo.get(x);
//			BufferedWriter bw = null;
//			try {
//				bw = new BufferedWriter(new FileWriter(new File("charmatrix-" + x + ".txt"), false));
//				StringBuilder sb = new StringBuilder();
//				for (int j = 0; j < charMatrix.length; j++) {
//					for (int i = 0; i < charMatrix[j].length; i += 3) {
//						if (Pixel.isMatch(charMatrix[j], i, 0, 0, 0)) {
//							sb.append("1");
//						}
//						else {
//							sb.append("0");
//						}
//					}
//					sb.append(",\n");
//				}
//				sb.deleteCharAt(sb.length() - 1);
//				bw.write(sb.toString());
//				bw.flush();
//				bw.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					if (bw != null) {
//						bw.close();
//					}
//				} catch (IOException e) {
//					
//				}
//			}
//		}
//	}
}
