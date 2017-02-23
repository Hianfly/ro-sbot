package com.hiandev.rosbot;

import java.util.ArrayList;
import java.util.Random;

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
	CellProfiler cellProfiler = null;
	HpSpWatcher  hpspWatcher = null;
	
	public Main() {
		try {
			scanner = new Scanner(120, 125, 800, 600);
			scanner.setScannerListener(this);
			scannerFrame = new ScannerFrame(scanner);
			scannerEvent = new EventManager(scanner);
			scannerEvent.setEventListener(this);
			cellProfiler = new CellProfiler(scanner);
			hpspWatcher = new HpSpWatcher(scanner);
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
		hpspWatcher.start();
		new DumpFile(scanner).start();;
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
	public int onTargeting(EventManager event, int[] cellXY) {
		return 0;
	}
	
	@Override
	public int onIdle(EventManager event) {
		int r = 0;
//		ArrayList<int[]> cellDiff = scanner.getCellDiffByDistance(0);
//		int removed = cellProfiler.removeNotClickable(cellDiff);
//		if (cellDiff.size() == 0) {
//			event.moveRandomly();
//			return r;
//		}
//		int index = new Random().nextInt(cellDiff.size() > 10 ? 10 : cellDiff.size());
//		int[] cell = cellDiff.get(index);
//		switch (event.target(cell)) {
//		case 0:
//			r = 0;
//			cellProfiler.add(cell[4], cell[5], CellProfiler.PROFILE_ATTACKABLE);
//			event.attack(cell);
//			break;
//		default:
//			r = 1;
//			cellProfiler.add(cell[4], cell[5], CellProfiler.PROFILE_NOT_CLICKABLE);
//			break;
//		}
////		System.out.println("--- AT " + cellProfiler.getList(CellProfiler.PROFILE_ATTACKABLE).size());
////		System.out.println("--- NC " + cellProfiler.getList(CellProfiler.PROFILE_NOT_CLICKABLE).size());
//		System.out.println("REMOVING BY SIZE >> " + removed + " LEFT >> " + cellDiff.size());
////		System.out.println("PROFILE_ATTACKABLE\n"    + cellProfiler.toSummaryString(CellProfiler.PROFILE_ATTACKABLE));
////		System.out.println("PROFILE_NOT_CLICKABLE\n" + cellProfiler.toSummaryString(CellProfiler.PROFILE_NOT_CLICKABLE));
		return r;
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
