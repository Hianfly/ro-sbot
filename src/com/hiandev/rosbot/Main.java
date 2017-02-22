package com.hiandev.rosbot;

import java.util.ArrayList;

import com.hiandev.rosbot.EventManager.EventListener;
import com.hiandev.rosbot.Scanner.ScannerListener;
import com.hiandev.rosbot.ui.ScannerFrame;

/**
 * @author Hian
 *
 */
public class Main implements ScannerListener, EventListener {
	
	public static void main(String[] args) {
		new Main();
	}
	
	Scanner      scanner = null;
	ScannerFrame scannerFrame = null;
	EventManager scannerEvent = null;
	
	public Main() {
		try {
			scanner = new Scanner(80, 120, 800, 600);
			scanner.setScannerListener(this);
			scannerFrame = new ScannerFrame(scanner);
			scannerEvent = new EventManager(scanner);
			scannerEvent.setEventListener(this);
			scanner.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 *
	 * 
	 * 
	 */
	
	@Override
	public void onStart() {
		scannerFrame.show();
		sleep(1000);
	}
	
	@Override
	public void onPreExecute() {
		scannerFrame.clearCells(5);
	}
	
	@Override
	public void onPostExecute() {
		scannerEvent.execute();
		scannerFrame.updateCells(scanner.getCellDiff());
	}
	
	@Override
	public void onFinish() {
		
	}
	
	/*
	 *
	 * 
	 * 
	 */
	
	@Override
	public int onMoving(EventManager event) {
		return 0;
	}
	
	@Override
	public int onAttacking(EventManager event, int[] cellXY) {
		System.out.println("On Attacking");
		return 0;
	}
	
	@Override
	public int onTargeting(EventManager event, int[] cellXY) {
		event.attack(cellXY);
		return 0;
	}
	
	@Override
	public int onIdle(EventManager event) {
		ArrayList<int[]> cellDiff = scanner.getCellDiffByDistance(25);
		for (int[] cell : cellDiff) {
			int t = event.target(cell);
			if (t == 0) {
				break;
			}
    	}
		return 0;
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
