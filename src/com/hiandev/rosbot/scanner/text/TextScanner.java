package com.hiandev.rosbot.scanner.text;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import com.hiandev.rosbot.scanner.Pixel;
import com.hiandev.rosbot.scanner.Scanner;

public class TextScanner extends Scanner {

    public TextScanner(int _x, int _y, int _w, int _h) throws AWTException {
    	super (_x, _y, _w, _h);
    	setInterval(1000);
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
			createTextList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    private boolean dump = true;
    @Override
    protected void onPostExecute() {
    	super.onPostExecute();
    	try {
    		computeTextChanged();
	    	if (!dump) {
	    		 dump = true;
	    		 dumpAssets();
	    	}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    @Override
    public BufferedImage toBufferedImage() {
    	return toBufferedImage(pixels);
    }
    public void onTextChanged(String[] rowTexts, boolean notify) {
    	
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
			pixels[y] = Pixel.floorPixels(getScreenImage().getRaster().getPixels(0, y, _w, 1, (int[]) null), floorThreshold);
		}
    }
    private int[][] pixelTexts   = new int[][] { { 0, 0, 0 } };
    private int[]   pixelPanel   = new int[] { 255,   0,   0 };
    private int[]   pixelSpace   = new int[] {   0,   0, 255 };
    private int[]   pixelNewLine = new int[] {   0, 255,   0 };
    public void setTextPixels(int[][] textPixels) {
    	this.pixelTexts = textPixels;
    }
    private void filterPixelSpaces() {
		for (int z = 0; z < pixels.length; z++) {
			if (Pixel.isMatch(pixels[z], 0, pixelPanel)) {
				continue;
			}
			int vlineCounter = 0;
			for (int x = 0; x < pixels[z].length; x += 3) {
				boolean vline = true;
				int     rows  = 0;
				for (int y = z; y < pixels.length; y += 1) {
					if (Pixel.isMatch(pixels[y], x, pixelPanel)) {
						break;
					}
					rows++;
					int index = Pixel.findIndex(pixels[y], x, pixelTexts);
					if (index > -1) {
						vline = false;
						break ;
					}
				}
				if (vline) {
					if (++vlineCounter < prmSpaceThreshold) {
						for (int y = z; y < z + rows; y += 1) {
							Pixel.setPixel(pixels[y], x, pixelPanel);
						}
					}
					else {
						vlineCounter = 0;
						for (int y = z; y < z + rows; y += 1) {
							Pixel.setPixel(pixels[y], x, pixelSpace);
						}
					}
				}
				else {
					vlineCounter = 0;
				}
			}
		}
    }
    private void filterPixelLines() {
		for (int y = 0; y < pixels.length; y++) {
			boolean hline = true;
			L : for (int x = 0; x < pixels[y].length; x += 3) {
				int index = Pixel.findIndex(pixels[y], x, pixelTexts);
				if (index > -1) {
					hline = false;
					break L;
				}
			}
			if (hline) {
				for (int x = 0; x < pixels[y].length; x += 3) {
					Pixel.setPixel (pixels[y], x, pixelPanel);
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
				if (Pixel.isMatch(pixels[y], x, pixelPanel)) {
					continue;
				}
				if (Pixel.isMatch(pixels[y], x, pixelSpace)) {
					w = 1;
					h = createCharMatrixHeight(x, y);
					charMatrixList.add(new int[][] { pixelSpace });
				}
				else {
					w = createCharMatrixWidth (x, y);
					h = createCharMatrixHeight(x, y);
					charMatrixList.add(createCharMatrix(x, y, w, h));
				}
				x += (w * 3) - 3;
			}
			if (h != 0) {
				y += h - 1;
				charMatrixList.add(new int[][] { pixelNewLine });
						
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
				if (Pixel.isMatch(pixels[j], i, pixelPanel)) {
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
			if (Pixel.isMatch(pixels[y], i, pixelPanel)) {
				break;
			}
			w++;
		}
    	return w;
    }
    private int createCharMatrixHeight(int x, int y) {
    	int h = 0;
    	for (int j = y; j < pixels.length; j += 1) {
			if (Pixel.isMatch(pixels[j], x, pixelPanel)) {
				break;
			}
			h++;
		}
    	return  h;
    }
    private int prmSpaceThreshold = 3;
    
    /*
     * 
     * 
     * 
     */
    private ArrayList<String> textListOld = new ArrayList<>();
    private ArrayList<String> textListNew = new ArrayList<>();
    private void createTextList() {
    	textListOld = new ArrayList<>();
    	textListOld.addAll(textListNew);
    	textListNew = new ArrayList<>();
    	StringBuilder sb = new StringBuilder();
		for (int x = 0; x < charMatrixList.size(); x++) {
			String key = toAssetString(charMatrixList.get(x));
			if (key.equals(";,")) {
				textListNew.add(sb.toString());
				sb = new StringBuilder();
				continue;
			}
			if (key.equals(" ,")) { // 1=3x10; 02-9=5x10
				if (x > 0) {
					String prev = ASSET_MAP.get(toAssetString(charMatrixList.get(x - 1)));
					if (prev == null || !prev.equals("1")) {
						sb.append(" ");
					}
				}
				else {
					sb.append(" ");
				}
				continue;
			}
			sb.append(ASSET_MAP.get(key) == null ? "?" : ASSET_MAP.get(key));
		}
    }

    /*
     * 
     * 
     * 
     */
    private String assetsDir = "";
    public void setAssetsDir(String assetsDir) {
    	this.assetsDir = assetsDir;
    }
    private final ConcurrentHashMap<String, String> ASSET_MAP = new ConcurrentHashMap<>();
    private void loadAssets() {
    	ASSET_MAP.clear();
		File[] assets = new File(assetsDir).listFiles();
		for (File file : assets) {
			if (!file.getName().endsWith(".txt")) {
				continue;
			}
			BufferedReader br = null;
    		StringBuilder sb = null;
    		String ln = null;
    		String st = null;
    		String[] kv = null;
	    	try {
	    		br = new BufferedReader(new FileReader(file));
	    		sb = new StringBuilder();
	    		while ((ln = br.readLine()) != null) {
	    			sb.append(ln).append("\n");
	    		}
	    		sb.deleteCharAt(sb.length() - 1);
	    		st = sb.toString();
	    		if (st.startsWith(":")) {
	    			kv = new String[] { ":", st.substring(2) };
	    		}
	    		else {
	    			kv = st.split(":");
	    			if (kv.length < 2) {
	    				System.out.println(file.getAbsolutePath());
	    			}
	    		}
	    		ASSET_MAP.put(kv[1].trim(), kv[0].trim());
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
    private void dumpAssets() {
    	int[][] cm = null;
    	StringBuilder sb = null;
    	int rw = 1;
		int cl = 0;
		String dt = null;
		String fn = "./assets/dumps/charmatrix_row_col.txt";
		File fl = null;
		BufferedWriter bw = null;
		for (int x = 0; x < charMatrixList.size(); x++) {
			cm = charMatrixList.get(x);
			sb = new StringBuilder();
			for (int j = 0; j < cm.length; j++) {
				for (int i = 0; i < cm[j].length; i += 3) {
					if (Pixel.isMatch(cm[j], i, pixelSpace)) {
	    				sb.append(" ");
						continue;
	    			}
	    			if (Pixel.isMatch(cm[j], i, pixelNewLine)) {
	    				sb.append(";");
						continue;
	    			}
	    			if (Pixel.findIndex(cm[j], i, pixelTexts) > -1) {
	            		sb.append("1");
	    			}
	    			else {
	    				sb.append("_");
	    			}
				}
				sb.append(",\n");
			}
			sb.deleteCharAt(sb.length() - 1);
			dt = sb.toString();
			if (dt.equals(";,")) {
				rw += 1;
				cl  = 1;
			}
			else {
				cl += 1;
			}
			try {
				fl = new File(fn.replace("row", rw + "").replace("col", cl + ""));
				bw = new BufferedWriter(new FileWriter(fl, false));
				bw.write(sb.toString());
				bw.flush();
				bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null) {
						bw.close();
					}
				} catch (IOException e) {
					
				}
			}
		}
    }
    private String toAssetString(int[][] charMatrix) {
    	StringBuilder sb = new StringBuilder();
    	for (int y = 0; y < charMatrix.length; y += 1) {
    		for (int x = 0; x < charMatrix[y].length; x += 3) {
    			if (Pixel.isMatch(charMatrix[y], x, pixelSpace)) {
    				sb.append(" ");
    				continue;
    			}
    			if (Pixel.isMatch(charMatrix[y], x, pixelNewLine)) {
    				sb.append(";");
    				continue;
    			}
    			if (Pixel.findIndex(charMatrix[y], x, pixelTexts) > -1) {
            		sb.append("1");
    			}
    			else {
    				sb.append("_");
    			}
        	}
    		sb.append(",\n");
    	}
    	sb.deleteCharAt(sb.length() - 1);
    	return trim(sb);
    }
    private String trim(StringBuilder sb) {
    	boolean trimTop = true;
    	while (trimTop) {
    		int    idx = sb.indexOf(",");
    		String row = sb.substring(0, idx);
    		if (row.contains("1") || row.contains(" ") || row.contains(";")) {
    			trimTop = false;
    		}
    		else {
    			sb.delete(0, idx + 2);
    		}
    	}
    	boolean trimBot = true;
    	while (trimBot) {
    		int idx = sb.lastIndexOf("\n");
    		if (idx == -1) {
    			trimBot = false;
    			break;
    		}
    		String row = sb.substring(idx + 1);
    		if (row.contains("1") || row.contains(" ") || row.contains(";")) {
    			trimBot = false;
    		}
    		else {
    			sb.delete(idx, sb.length());
    		}
    	}
    	return sb.toString();
    }

    /*
     * 
     * 
     * 
     */
	private long lastTextChangedTime = 0;
    private void computeTextChanged() {
    	boolean change = false;
    	boolean notify = false;
		long now = System.currentTimeMillis();
		P : { 
    		if (lastTextChangedTime == 0 || now - lastTextChangedTime > 1000) {
    			change = true;
    			notify = true;
				break P;
    		}
			if (textListOld.isEmpty() && !textListNew.isEmpty()) {
				change = true;
				break P;
			}
			if (textListNew.size() != textListOld.size()) {
				change = true;
				break P;
			}
			for (int x = 0; x < textListNew.size(); x++) {
				if (!textListNew.get(x).equals(textListOld.get(x))) {
					change = true;
					break P;
				}
			}
		}
		if (change) {
			lastTextChangedTime = now;
			onTextChanged(textListNew.toArray(new String[0]), notify);
		}
    }
    
}
