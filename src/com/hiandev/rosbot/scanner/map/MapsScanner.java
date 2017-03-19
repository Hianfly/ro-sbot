package com.hiandev.rosbot.scanner.map;

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

public class MapsScanner extends Scanner {

    public MapsScanner() throws AWTException {
    	super (662, 67, 128, 128);
    	setInterval(250);
    }
    
    @Override
    protected boolean onStart() {
    	boolean s = super.onStart();
    	loadPoints("seaotter.txt");
    	createRoutes();
    	return s;
    }
    
    @Override
    protected void onPreExecute() {
    	super.onPreExecute();
    }
    @Override
	protected void onExecute() {
		super.onExecute();
		try {
			createPixels(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    @Override
    protected void onPostExecute() {
    	super.onPostExecute();
    	try {
    		detectLocation();
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    @Override
    public BufferedImage toBufferedImage() {
    	return toBufferedImage(pixels);
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
    private int _mx = 0;
    private int _my = 0;
    private void detectLocation() {
    	int i = 0;
    	int j = 0;
    	for (int y = 0; y < pixels.length; y += 1) {
    		for (int x = 0; x < pixels[y].length; x += 3) {
        		if (Math.abs(pixels[y][x + 0] - 220) <= 10 &&
    				Math.abs(pixels[y][x + 1] -  40) <= 10 &&
					Math.abs(pixels[y][x + 2] -  40) <= 10) {
        			i = x / 3;
        			j = y;
        			break;
        		}
        	}
    	}
    	if (i == 0 || j == 0) {
    		return;
    	}
    	if (_mx != i || _my != j) {
    		_mx = i;
    		_my = j;
    		onLocationChanged(_mx, _my);
    	}
		
    }
    public void onLocationChanged(int _mx, int _my) {
    	
    }
    
    /*
     * 
     * 
     * 
     */
    private String mapsDir = "./maps/";
    private int routeIndex = -1;
	private ArrayList<int[]> points = new ArrayList<>();
	private ArrayList<int[]> routes = new ArrayList<>();
	private void loadPoints(String name) {
		BufferedReader br = null;
		String ln = null;
		String[] xy = null;
    	try {
    		br = new BufferedReader(new FileReader(new File(mapsDir + name)));
    		points.clear();
    		while ((ln = br.readLine()) != null) {
    			xy = ln.split(",");
    			points.add(new int[] { Integer.parseInt(xy[0]), Integer.parseInt(xy[1]) });
    		}
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
	private void createRoutes() {
		routeIndex = -1;
		routes.clear();
		routes.addAll(points);
	}
	public void reverseRoutes() {
		ArrayList<int[]> temp = new ArrayList<>();
		temp.addAll(routes);
		routes.clear();
		for (int x = 0; x < temp.size(); x++) {
			routes.add(0, temp.get(x));
		}
	}
	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}
	public int getRouteIndex() {
		return routeIndex;
	}
	public int getRouteSize() {
		return routes.size();
	}
	public int[] getNextRoute() {
		if (routeIndex >= routes.size() - 1) {
			return null;
		}
		return routes.get(routeIndex + 1);
	}
	public int[] getCurrentRoute() {
		if (routeIndex >= routes.size()) {
			return null;
		}
		return routes.get(routeIndex);
	}
	
}
