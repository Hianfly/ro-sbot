package com.hiandev.rosbot.scanner;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public abstract class Scanner {
	
    public Scanner(int _x, int _y, int _w, int _h) throws AWTException {
    	this._x = _x;
    	this._y = _y;
    	this._w = _w;
    	this._h = _h;
    	this.interval = 1;
    	this.robot = new Robot();
    }

    /*
     * 
     * 
     * 
     */
    public boolean onStart() {
		return true;
	}
	public void onPreExecute() {
		
	}
	protected void onExecute() {
		BufferedImage buffer = captureScreenImage();
		synchronized (this) {
			screenImage = buffer;
		}
	}
	public void onPostExecute() {
		
	}
	public void onFinish() {
		
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
    
	protected final Robot robot;    
	private BufferedImage screenImage = null;
	public BufferedImage getScreenImage() {
		synchronized (this) {
			return screenImage;
		}
	}
    public BufferedImage captureScreenImage() {
	    return robot.createScreenCapture(new Rectangle(_x, _y, _w, _h));
    }
    
	/*
	 * 
	 * 
	 * 
	 */
    private boolean running = false;
	private Thread thread = null;
	private Thread getThread() {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				running = onStart();
				while (running) {
					onPreExecute();
					onExecute();
					onPostExecute();
				    sleep(interval);
				}
				onFinish();
			}
		});
	}
	public long interval;
	private boolean started = false;
	public void start() {
		if (started) {
			
		}
		else {
			started = true;
			thread = getThread();
			thread.start();
		}
	}
	public void stop() {
		running = false;
	}

	/*
	 * 
	 * 
	 * 
	 */
	public void mouseLeftClick() {
		robot.mousePress(InputEvent.BUTTON1_MASK);
    	sleep(20);
    	robot.mouseRelease(InputEvent.BUTTON1_MASK);	
    }
	public void mouseGoto(int inGameX, int inGameY) {
		robot.mouseMove(_x + inGameX, _x + inGameY);
    }
	public void mouseGotoCell(int cellX, int cellY) {
		robot.mouseMove(_x + (cellX * Cell.SIZE) + 3, _y + (cellY * Cell.SIZE) + 3);
    }
	public void keyPush(int keycode) {
		robot.keyPress(keycode);
    	sleep(20);
		robot.keyRelease(keycode);
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	public int[] floorPixels(int[] samples, int threshold) {
		for (int x = 0; x < samples.length; x++) {
			samples[x] = (samples[x] / threshold) * threshold;
		}
		return samples;
	}
    protected void sleep(long time) {
    	try {
	    	Thread.sleep(time);
	    } catch (Exception e) {
	    }
    }
    
}
