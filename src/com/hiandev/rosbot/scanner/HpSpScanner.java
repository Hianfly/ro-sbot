package com.hiandev.rosbot.scanner;

import java.awt.AWTException;

public class HpSpScanner extends Scanner {

    public HpSpScanner(int _x, int _y) throws AWTException {
    	super (_x + ((800 / 2) - 30), _y + ((600 / 2) + 10), 60, 20);
    	interval = 100;
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
			int last = 0;
			for (int c = 0; c < _w * 3; c += 3) {
				for (int r = 0; r < _h; r += 1) {
					if (Math.abs(pixels[r][c + 0] -  10) <= 20 &&
						Math.abs(pixels[r][c + 1] - 230) <= 20 &&
						Math.abs(pixels[r][c + 2] -  30) <= 20) {
						last = c;
						break;
					}
				}
			}
			if (last != 0) {
				mHpPercentage = (int) (((float) last / 174) * 100);
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
