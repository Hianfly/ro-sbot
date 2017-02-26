package com.hiandev.rosbot.scanner;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public abstract class Scanner {
    
    public Scanner(int x, int y, int w, int h) throws AWTException {
    	this._x = x;
    	this._y = y;
    	this._w = w;
    	this._h = h;
    	this.robot = new Robot();
    }
	protected boolean onStart() {
		return true;
	}
	protected void onFinish() {
		
	}
	protected void onPreExecute() {
		
	}
	protected void onExecute() {
		screenImage = captureScreenImage();
	}
	protected void onPostExecute() {
		
	}

    /*
     *
     * 
     * 
     */
    
    public final int _x;
    public final int _y;
    public final int _w;
    public final int _h;
    
	/*
	 * 
	 * 
	 * 
	 */
	
	public long interval = 1;
	public boolean running = false;
	public void start() {
		running = onStart();
		while (running) {
			onPreExecute();
			onExecute();
			onPostExecute();
		    sleep(interval);
		}
		onFinish();
	}
	public void stop() {
		running = false;
	}

    /*
     * 
     * 
     * 
     */
    
	protected final Robot robot;    
	private BufferedImage screenImage = null;
	public BufferedImage getScreenImage() {
		return screenImage;
	}
    public BufferedImage captureScreenImage() {
	    return robot.createScreenCapture(new Rectangle(_x, _y, _w, _h));
    }
	public void mouseIdle() {
//    	robot.mouseMove(_x + 1 + 60, _y + zoneChat[2] + 1 + 10);
    }
	public void mouseClick() {
    	robot.mousePress(InputEvent.BUTTON1_MASK);
    	sleep(20);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
	public void mouseGoto(int x, int y) {
    	robot.mouseMove(x, y);
    }
	
	/*
	 * 
	 * 
	 * 
	 */
	
    private void sleep(long time) {
    	try {
	    	Thread.sleep(time);
	    } catch (Exception e) {
	    }
    }
    
}
