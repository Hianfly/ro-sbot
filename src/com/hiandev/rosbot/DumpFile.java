package com.hiandev.rosbot;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.io.File;

import javax.imageio.ImageIO;

public class DumpFile{
	
	private Scanner scanner;
	public DumpFile(Scanner scanner) {
		this.scanner = scanner;
	}
	
	private Thread thread = null;
	private Thread getThread() {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				while (scanner.running) {
					try {
						Thread.sleep(1000 * 10);
						Raster screenRaster = scanner.getScreenRaster();
						if (screenRaster != null) {
							int[] hp = screenRaster.getPixels(0, 0, scanner._w, scanner._h, new int[scanner._w * scanner._h * 3]);
							int[] sp = scanner.shrinkCellData(hp);
							int[] xx = new int[sp.length / 3];
							for (int x = 0; x < xx.length; x++) {
								xx[x] = Integer.parseInt("" + sp[x + 0] + sp[x + 1] + sp[x + 2]);
							}
							BufferedImage img = new BufferedImage(scanner._w, scanner._h, BufferedImage.TYPE_INT_RGB);
							img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferInt(xx, xx.length), new Point()));
							ImageIO.write(img, "jpg", new File("SS-" + System.currentTimeMillis() + ".jpg"));
						}
					} catch (Exception e) {
						e.printStackTrace();
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
