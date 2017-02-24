package com.hiandev.rosbot;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.HashMap;
import java.util.Set;

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
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BufferedImage bi = ImageIO.read(new File("./test.jpg"));
			WritableRaster raster = bi.getRaster();
			ColorModel cm = bi.getColorModel();
			
			
			int w = bi.getWidth();
			int h = bi.getHeight();
			int[] pixels = raster.getPixels(0,  0, w, h, (int[]) null);
			
			int[][] data = new int[pixels.length / (w * 3)][(w * 3)];
			int index = 0;
			for (int x = 0; x < data.length; x++) {
				for (int y = 0; y < data[x].length; y++) {
					data[x][y] = pixels[index++];
				}
			}
			
			// grayscale
//			for (int x = 0; x < data.length; x++) {
//				for (int y = 0; y < data[x].length; y += 3) {
//					int 
//					sum  = data[x][y + 0];
//					sum += data[x][y + 1];
//					sum += data[x][y + 2];
//					sum /= 3;
//					
//					if (sum >= 192) {
//						sum = 255;
//					}
//					else if (sum >= 128) {
//						sum = 191;
//					}
//					else if (sum >= 68) {
//						sum = 127;
//					}
//					else { 
//						sum = 0;
//					}
//					data[x][y + 0] = sum;
//					data[x][y + 1] = sum;
//					data[x][y + 2] = sum;
//				}
//			}
			HashMap<String, Integer> map = new HashMap<>();
			for (int x = 0; x < data.length; x++) {
				for (int y = 0; y < data[x].length; y += 3) {
					data[x][y + 0] = data[x][y + 0] <= 80 ? 0 : data[x][y + 0];
					data[x][y + 1] = data[x][y + 1] <= 80 ? 0 : data[x][y + 1];
					data[x][y + 2] = data[x][y + 2] <= 80 ? 0 : data[x][y + 2];
					data[x][y + 0] = (data[x][y + 0] / 20) * 20;
					data[x][y + 1] = (data[x][y + 1] / 20) * 20;
					data[x][y + 2] = (data[x][y + 2] / 20) * 20;
					String key = toHexString(data[x][y + 0]) + 
								 toHexString(data[x][y + 1]) + 
								 toHexString(data[x][y + 2]);
					Integer i = map.get(key);
					if (i == null) { 
						map.put(key, 1);
					}
					else {
						map.put(key, i + 1);
					}
				}
			}
			
			Set<String> keys = map.keySet();
			for (String key : keys) {
				Integer val = map.get(key);
				System.out.println(key  + " : " + val.intValue());
			}
			
			for (int x = 0; x < data.length; x++) {
				for (int y = 0; y < data[x].length; y += 3) {
					if (data[x][y + 0] < 20 &&
						data[x][y + 1] < 20 &&
						data[x][y + 2] < 20){
						continue;
					}
					String key = toHexString(data[x][y + 0]) + 
							     toHexString(data[x][y + 1]) + 
							     toHexString(data[x][y + 2]);
					Integer val = map.get(key);
					if (val.intValue() > 1000) {
						data[x][y + 0] = 255;
						data[x][y + 1] = 255;
						data[x][y + 2] = 255;
					}
				}
			}

//			for (int x = 0; x < data.length - 5; x += 1) {
//				for (int y = 0; y < data[x].length - 15; y += 3) {
//					
//					boolean white = true;
//					for (int j = y; j < y + 15; j += 3) {
//						if (data[x][j + 0] == 255 &&
//							data[x][j + 1] == 255 &&
//							data[x][j + 2] == 255) {
//						}
//						else {
//							white = false;
//						}
//					}
//					for (int j = y; j < y + 15; j += 3) {
//						if (data[x + 5][j + 0] == 255 &&
//							data[x + 5][j + 1] == 255 &&
//							data[x + 5][j + 2] == 255) {
//						}
//						else {
//							white = false;
//						}
//					}
//					if (white) {
//						for (int i = x; i < x + 5; i += 1) {
//							for (int j = y; j < y + 15; j += 3) {
//								data[i][j + 0] = 255;
//								data[i][j + 1] = 255;
//								data[i][j + 2] = 255;
//							}
//						}
//					}
//				}
//			}
			
			index = 0;
			for (int x = 0; x < data.length; x++) {
				for (int y = 0; y < data[x].length; y++) {
					pixels[index++] = data[x][y];
				}
			}
			
			raster.setPixels(0, 0, bi.getWidth(), bi.getHeight(), pixels);
			BufferedImage bw  = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
			ImageIO.write(bw, "png", new File("./test-" + System.currentTimeMillis() + ".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private static String toHexString(int rgb) {
		String hex = Integer.toHexString(rgb);
		return hex.length() < 2 ? ("0" + hex) : hex;
	}
	
}
