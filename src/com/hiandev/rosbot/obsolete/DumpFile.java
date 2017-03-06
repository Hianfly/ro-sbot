package com.hiandev.rosbot.obsolete;
//package com.hiandev.rosbot;
//
//import java.awt.Point;
//import java.awt.Rectangle;
//import java.awt.Robot;
//import java.awt.image.BufferedImage;
//import java.awt.image.ColorModel;
//import java.awt.image.DataBufferByte;
//import java.awt.image.DataBufferFloat;
//import java.awt.image.DataBufferInt;
//import java.awt.image.Raster;
//import java.awt.image.WritableRaster;
//import java.io.File;
//import java.util.HashMap;
//import java.util.Set;
//
//import javax.imageio.ImageIO;
//
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//
//import com.hiandev.rosbot.scanner.v0.ScannerXz;
//
public class DumpFile {
	
}
//
//	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
//	
//	private ScannerXz scanner;
//	public DumpFile(ScannerXz scanner) {
//		this.scanner = scanner;
//	}
//	
//	private Thread thread = null;
//	private Thread getThread() {
//		return new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while (scanner.running) {
//					try {
//						Thread.sleep(1000 * 10);
//						Raster screenRaster = scanner.getScreenRaster();
//						if (screenRaster != null) {
//							int[] hp = screenRaster.getPixels(0, 0, scanner._w, scanner._h, new int[scanner._w * scanner._h * 3]);
//							int[] sp = scanner.shrinkCellData(hp);
//							int[] xx = new int[sp.length / 3];
//							for (int x = 0; x < xx.length; x++) {
//								xx[x] = Integer.parseInt("" + sp[x + 0] + sp[x + 1] + sp[x + 2]);
//							}
//							BufferedImage img = new BufferedImage(scanner._w, scanner._h, BufferedImage.TYPE_INT_RGB);
//							img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferInt(xx, xx.length), new Point()));
//							ImageIO.write(img, "jpg", new File("SS-" + System.currentTimeMillis() + ".jpg"));
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//	}
//	
//	public void start() {
//		thread = getThread();
//		thread.start();
//	}
//	
//
//
//   	
//
////   	Mat mat = new Mat(screenRaster.getHeight(), screenRaster.getWidth(), CvType.CV_32F);
////   	System.out.println("????????????????? " + screenRaster.getDataBuffer().getDataType());
////   	int[] data = ((DataBufferInt) screenRaster.getDataBuffer()).getData();
////   	mat.put(0, 0, data);
////   	Imgproc.bilateralFilter(mat, mat, 9, 75, 75);
//	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		try {
//			BufferedImage bi = new Robot().createScreenCapture(new Rectangle(0, 0, 800, 600));
//			WritableRaster raster = bi.getRaster();
//			int w = bi.getWidth();
//			int h = bi.getHeight();
//			int[] pixels = raster.getPixels(0,  0, w, h, (int[]) null);
//			int[][] data = new int[pixels.length / (w * 3)][(w * 3)];
//			int index = 0;
//			for (int x = 0; x < data.length; x++) {
//				for (int y = 0; y < data[x].length; y++) {
//					data[x][y] = pixels[index++];
//				}
//			}
//			
//			for (int x = 0; x < data.length; x += 4) {
//				for (int y = 0; y < data[x].length; y += 12) {
//					P : {
//						int b = 0;
//						int[] rgb = new int[3];
//						for (int i = x + 0; i < x + 4; i += 1) {
//							for (int j = y + 0; j < y + 12; j += 3) {
//								if (data[i][j + 0] <= 60 &&
//									data[i][j + 1] <= 60 &&
//									data[i][j + 2] <= 60) {
//									b++;
//									if (b > 0) {
//										break P;
//									}
//								}
//								rgb[0] += data[i][j + 0];
//								rgb[1] += data[i][j + 1];
//								rgb[2] += data[i][j + 2];
//							}
//						}
//						rgb[0] = rgb[0] / 16;
//						rgb[1] = rgb[1] / 16;
//						rgb[2] = rgb[2] / 16;
//						for (int i = x + 0; i < x + 4; i += 1) {
//							for (int j = y + 0; j < y + 12; j += 3) {
//								data[i][j + 0] = rgb[0];
//								data[i][j + 1] = rgb[1];
//								data[i][j + 2] = rgb[2];
//							}
//						}
//					}
//				}
//			}
//			
//			/*
//			 * 
//			 */
//			
//			HashMap<String, Integer> map = new HashMap<>();
//			for (int x = 0; x < data.length; x++) {
//				for (int y = 0; y < data[x].length; y += 3) {
//					data[x][y + 0] =  data[x][y + 0] <= 60 ? 0 : data[x][y + 0];
//					data[x][y + 1] =  data[x][y + 1] <= 60 ? 0 : data[x][y + 1];
//					data[x][y + 2] =  data[x][y + 2] <= 60 ? 0 : data[x][y + 2];
//					data[x][y + 0] = (data[x][y + 0] / 10) * 10;
//					data[x][y + 1] = (data[x][y + 1] / 10) * 10;
//					data[x][y + 2] = (data[x][y + 2] / 10) * 10;
//					String key = toHexString(data[x][y + 0]) + 
//								 toHexString(data[x][y + 1]) + 
//								 toHexString(data[x][y + 2]);
//					Integer i = map.get(key);
//					if (i == null) { 
//						map.put(key, 1);
//					}
//					else {
//						map.put(key, i + 1);
//					}
//				}
//			}
//			
//			
//			Set<String> keys = map.keySet();
//			for (String key : keys) {
//				Integer val = map.get(key);
//				System.out.println(key + " > " + val);
//			}
//			for (int x = 0; x < data.length; x++) {
//				for (int y = 0; y < data[x].length; y += 3) {
//					if (data[x][y + 0] < 20 &&
//						data[x][y + 1] < 20 &&
//						data[x][y + 2] < 20){
//						continue;
//					}
//					String key = toHexString(data[x][y + 0]) + 
//							     toHexString(data[x][y + 1]) + 
//							     toHexString(data[x][y + 2]);
//					Integer val = map.get(key);
//					if (val.intValue() > 100) {
//						data[x][y + 0] = 255;
//						data[x][y + 1] = 255;
//						data[x][y + 2] = 255;
//					}
//				}
//			}
//			
//			/*
//			 * 
//			 */
//
//			for (int x = 0; x < data.length; x += 4) {
//				for (int y = 0; y < data[x].length; y += 12) {
//					P : {
//						boolean removed = true;
//						for (int i = x + 0; i < x + 4; i += 1) {
//							for (int j = y + 0; j < y + 12; j += 3) {
//								if (data[i][j + 0] != 0 &&
//									data[i][j + 0] != 255 &&
//									data[i][j + 1] != 0 &&
//									data[i][j + 1] != 255 &&
//									data[i][j + 2] != 0 &&
//									data[i][j + 2] != 255) {
//									removed = false;
//								} 
//							}
//						}
//						if (!removed) {
//							break P;
//						}
//						for (int i = x + 0; i < x + 4; i += 1) {
//							for (int j = y + 0; j < y + 12; j += 3) {
//								data[i][j + 0] = 255;
//								data[i][j + 1] = 255;
//								data[i][j + 2] = 255;
//							}
//						}
//					}
//				}
//			}
//			
//			/*
//			 * 
//			 */
//			
//			index = 0;
//			for (int x = 0; x < data.length; x++) {
//				for (int y = 0; y < data[x].length; y++) {
//					pixels[index++] = data[x][y];
//				}
//			}
//			ColorModel cm = bi.getColorModel();
//			raster.setPixels(0, 0, bi.getWidth(), bi.getHeight(), pixels);
//			BufferedImage bw  = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
//			ImageIO.write(bw, "png", new File("./test-" + System.currentTimeMillis() + ".png"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	
//	public static BufferedImage matToBufferedImage(Mat matrix, BufferedImage bimg)
//	{
//	    if ( matrix != null ) { 
//	        int cols = matrix.cols();  
//	        int rows = matrix.rows();  
//	        int elemSize = (int)matrix.elemSize();  
//	        byte[] data = new byte[cols * rows * elemSize];  
//	        int type;  
//	        matrix.get(0, 0, data);  
//	        switch (matrix.channels()) {  
//	        case 1:  
//	            type = BufferedImage.TYPE_BYTE_GRAY;  
//	            break;  
//	        case 3:  
//	            type = BufferedImage.TYPE_3BYTE_BGR;  
//	            // bgr to rgb  
//	            byte b;  
//	            for(int i=0; i<data.length; i=i+3) {  
//	                b = data[i];  
//	                data[i] = data[i+2];  
//	                data[i+2] = b;  
//	            }  
//	            break;  
//	        default:  
//	            return null;  
//	        }  
//
//	        // Reuse existing BufferedImage if possible
//	        if (bimg == null || bimg.getWidth() != cols || bimg.getHeight() != rows || bimg.getType() != type) {
//	            bimg = new BufferedImage(cols, rows, type);
//	        }        
//	        bimg.getRaster().setDataElements(0, 0, cols, rows, data);
//	    } else { // mat was null
//	        bimg = null;
//	    }
//	    return bimg;  
//	}   
//	
////	/**
////	 * @param args
////	 */
////	public static void main(String[] args) {
////		try {
////			BufferedImage bi = ImageIO.read(new File("./test.jpg"));
////			WritableRaster raster = bi.getRaster();
////			ColorModel cm = bi.getColorModel();
////
////		   	Mat mat = new Mat(raster.getHeight(), raster.getWidth(), CvType.CV_8UC3);
////		   	byte[] data = ((DataBufferByte) raster.getDataBuffer()).getData();
////		   	mat.put(0, 0, data);
////		   	
////		   	Mat ma2 = new Mat(raster.getHeight(), raster.getWidth(), CvType.CV_32F);
////		   	Imgproc.bilateralFilter(mat, ma2, 15, 50, 200);
////		   	
////		   	
////		   	Mat ma3 = null;
////		   	long T1 = System.currentTimeMillis();
////		   	for (int x = 0; x < 1; x++) {
////			   	ma3 = new Mat(raster.getHeight(), raster.getWidth(), CvType.CV_32F);
////			   	Imgproc.bilateralFilter(ma2, ma3, 15, 50, 200);
////			   	ma2 = ma3;
////		   	}
////		   	long T2 = System.currentTimeMillis();
////		   	System.out.println((T2 - T1) + "ms");
////		   	
////		   	Imgcodecs.imwrite("./test-" + System.currentTimeMillis() + ".png", ma3);
////
//////			int w = bi.getWidth();
//////			int h = bi.getHeight();
//////			int[] pixels = raster.getPixels(0,  0, w, h, (int[]) null);
//////			raster.setPixels(0, 0, bi.getWidth(), bi.getHeight(), pixels);
//////			BufferedImage bw  = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
//////			ImageIO.write(bw, "png", new File("./test-" + System.currentTimeMillis() + ".png"));
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////	}
//	
//
//	private static String toHexString(int rgb) {
//		String hex = Integer.toHexString(rgb);
//		return hex.length() < 2 ? ("0" + hex) : hex;
//	}
//	
//}
