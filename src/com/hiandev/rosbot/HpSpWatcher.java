package com.hiandev.rosbot;

import java.awt.image.Raster;

public class HpSpWatcher{
	
	private ScannerXz scanner;
	public HpSpWatcher(ScannerXz scanner) {
		this.scanner = scanner;
	}
	
	private Thread thread = null;
	private Thread getThread() {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				while (scanner.running) {
					Raster screenRaster = scanner.getScreenRaster();
					if (screenRaster != null) {
						int x = scanner.zoneHpSp[0];
						int y = scanner.zoneHpSp[2];
						int w = scanner.zoneHpSp[1] - scanner.zoneHpSp[0];
						int h = 1;
						int[] hp = screenRaster.getPixels(x, y + 2, w, h, new int[w * h * 3]);
						int[] sp = screenRaster.getPixels(x, y + 7, w, h, new int[w * h * 3]);
						int hpp = 0;
						for (int i = 0; i < hp.length; i += 3) {
							if (Math.abs(hp[i + 0] -  10) <= 20 &&
								Math.abs(hp[i + 1] - 230) <= 20 &&
								Math.abs(hp[i + 2] -  30) <= 20) {
								hpp = i;
							}
						}
						int spp = 0;
						for (int i = 0; i < sp.length; i += 3) {
							if (Math.abs(sp[i + 0] -  20) <= 20 &&
								Math.abs(sp[i + 1] -  90) <= 20 &&
								Math.abs(sp[i + 2] - 220) <= 20) {
								spp = i;
							}
						}
						System.out.println("HP > " + (hpp / 3 / 6) + " SP > " + (spp / 3 / 6));
					}
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
			}
		});
	}
	
	public void start() {
		thread = getThread();
		thread.start();
	}
	

}
