package com.hiandev.rosbot.obsolete;
//package com.hiandev.rosbot;
//
//import java.awt.AWTException;
//import java.awt.Rectangle;
//import java.awt.Robot;
//import java.awt.event.InputEvent;
//import java.awt.image.BufferedImage;
//
//import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;
//
public class ScreenRobot {
	
}
//
//    public ScreenRobot(int x, int y) throws AWTException {
//    	this._x = x;
//    	this._y = y;
//    	this._w = 800;
//    	this._h = 600;
//    	this.interval = 10;
//    	this.robot = new Robot();
//    }
//    public boolean onStart() {
//		return true;
//	}
//	public void onCaptureScreen() {
//		BufferedImage buffer = captureScreenImage();
//		synchronized (this) {
//			screenImage = buffer;
//		}
//	}
//	public void onFinish() {
//		
//	}
//
//    /*
//     *
//     * 
//     * 
//     */
//    
////    public final int _x;
////    public final int _y;
////    public final int _w;
////    public final int _h;
//    
//	/*
//	 * 
//	 * 
//	 * 
//	 */
//	
////	public final long interval;
////	private boolean running = false;
////	public boolean isRunning() {
////		return running;
////	}
////	public void start() {
////		running = onStart();
////		while (running) {
////			onCaptureScreen();
////		    sleep(interval);
////		}
////		onFinish();
////	}
////	public void stop() {
////		running = false;
////	}
//
//    /*
//     * 
//     * 
//     * 
//     */
//    
////	protected final Robot robot;    
////	private BufferedImage screenImage = null;
////	public BufferedImage getScreenImage() {
////		synchronized (this) {
////			return screenImage;
////		}
////	}
////    public BufferedImage captureScreenImage() {
////	    return robot.createScreenCapture(new Rectangle(_x, _y, _w, _h));
////    }
//    
//    /*
//     * 
//     * 
//     * 
//     */
////    
////    public void mousePress(int buttons) {
////    	robot.mousePress(buttons);
////    }
////    public void mouseRelease(int buttons) {
////    	robot.mouseRelease(buttons);
////    }
////    public void mouseMove(int x, int y) {
////    	robot.mouseMove(_x + x, _y + y);
////    }
////    public void mouseWheel(int wheelAmt) {
////    	robot.mouseWheel(wheelAmt);
////    }
//    
//	/*
//	 * 
//	 * 
//	 * 
//	 */
//	
//    private void sleep(long time) {
//    	try {
//	    	Thread.sleep(time);
//	    } catch (Exception e) {
//	    }
//    }
//	
//}
