package com.hiandev.rosbot.scanner;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import com.hiandev.rosbot.Service;
import com.hiandev.rosbot.scanner.battle.Cell;

public abstract class Scanner extends Service {
	
    public Scanner(int _x, int _y, int _w, int _h) throws AWTException {
    	this._x = _x;
    	this._y = _y;
    	this._w = _w;
    	this._h = _h;
    	this.robot = new Robot();
    }
    
    @Override
    protected boolean onStart() {
    	boolean start = super.onStart();
		if (frame != null) {
	    	frame.show();
		}
		if (prevFrame != null) {
			prevFrame.show();
		}
    	return start;
    }
    
    @Override
    protected void onPostExecute() {
    	super.onPostExecute();
    	if (prevFrame != null) {
    		prevFrame.updatePreview(toBufferedImage());
    	}
    }
    
    @Override
    protected void onExecute() {
    	super.onExecute();
		BufferedImage buffer = captureScreenImage();
		synchronized (this) {
			screenImage = buffer;
		}
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
    public BufferedImage captureScreenImage(int _x, int _y, int _w, int _h) {
	    return robot.createScreenCapture(new Rectangle(_x, _y, _w, _h));
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
	public void keyPaste() {
	    robot.keyPress  (KeyEvent.VK_CONTROL);
    	sleep(20);
	    robot.keyPress  (KeyEvent.VK_V);
    	sleep(20);
	    robot.keyRelease(KeyEvent.VK_V);
    	sleep(20);
	    robot.keyRelease(KeyEvent.VK_CONTROL);
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	public BufferedImage toBufferedImage(int[][] pixels) {
		return toBufferedImage(Pixel.toPixels(pixels));
	}
	public BufferedImage toBufferedImage(int[] pixels) {
		BufferedImage  bi = getScreenImage();
		ColorModel     cm = bi.getColorModel();
		WritableRaster rs = Raster.createWritableRaster(bi.getRaster().getSampleModel(), new Point(0, 0));
		rs.setPixels(0, 0, bi.getWidth(), bi.getHeight(), pixels);
		return new BufferedImage(cm, rs, cm.isAlphaPremultiplied(), null);
	}
	public BufferedImage toBufferedImage() {
		return null;
	}
    
	/*
	 * 
	 * 
	 * 
	 */
	private ScannerFrame frame;
	public void setScannerFrame(ScannerFrame frame) {
		this.frame = frame;
		this.frame.setScanner(this);
	}
	public ScannerFrame getScannerFrame() {
		return frame;
	}
	private PreviewFrame prevFrame;
	public void setPreviewFrame(PreviewFrame prevFrame) {
		this.prevFrame = prevFrame;
		this.prevFrame.setScanner(this);
	}
	public PreviewFrame getPreviewFrame() {
		return prevFrame;
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	private boolean debug = false;
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public boolean isDebug() {
		return debug;
	}
	
}
