package com.hiandev.rosbot.scanner;

import java.awt.AWTException;

public class HpSpOcrScanner extends Scanner {

    public HpSpOcrScanner(int _x, int _y) throws AWTException {
    	super (_x + ((800 / 2) - 80), _y + ((600 / 2) - 30), 160, 80);
    	setInterval(50);
    }
    
    /*
     * 
     * 
     * 
     */

    @Override
	protected void onExecute() {
		super.onExecute();
		try {
			int[][] pixels = new int[_h][_w * 3];
			for (int r = 0; r < _h; r++) {
				pixels[r] = floorPixels(getScreenImage().getRaster().getPixels(0, r, _w, 1, (int[]) null), 10);
			}
			int first  = -1;
			int last   = -1;
			for (int c = 0; c < _w * 3; c += 3) {
				for (int r = 0; r < _h; r += 1) {
					if (Math.abs(pixels[r][c + 0] -  10) <= 20 &&
						Math.abs(pixels[r][c + 1] - 230) <= 20 &&
						Math.abs(pixels[r][c + 2] -  30) <= 20) {
						last  = c;
						first = first == -1 ? c : first;
						break;
					}
				}
			}
			if (last != -1) {
				mHpPercentage = (int) (((float) (last - first) / 174) * 100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private int mHpPercentage = 100;
    public final int getHpPercentage() {
    	return mHpPercentage;
    }
    
}
