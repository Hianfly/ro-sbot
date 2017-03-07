package com.hiandev.rosbot;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.hiandev.rosbot.event.HpSpEvent;
import com.hiandev.rosbot.event.ItemEvent;
import com.hiandev.rosbot.profiler.ProfilerDumper;
import com.hiandev.rosbot.scanner.Cell;
import com.hiandev.rosbot.scanner.Pixel;
import com.hiandev.rosbot.scanner.HpSpScanner;
import com.hiandev.rosbot.scanner.ItemScanner;
import com.hiandev.rosbot.ui.ScannerFrame;

/**
 * @author Hian
 *
 */
public class Main {
	
	public static void main(String[] args) {
		new Main();
	}
	
	ScannerFrame      scannerFrame = null;
	MainHpSpScanner   hpspScanner = null;
	MainHpSpEvent     hpspEvent = null;
	ProfilerDumper    profilerDumper = null;
	MainItemScanner   itemScanner = null;
	MainItemEvent     itemEvent = null;
	
	public Main() {
		try {
			itemScanner    = new MainItemScanner(5, 30);
			itemEvent      = new MainItemEvent(itemScanner);
			scannerFrame   = new ScannerFrame(itemScanner);
			hpspScanner    = new MainHpSpScanner(5, 30);
			hpspEvent      = new MainHpSpEvent(hpspScanner);
			hpspScanner.start();
			itemScanner.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public class MainItemEvent extends ItemEvent {
		
		public MainItemEvent(ItemScanner scanner) {
			super (scanner);
		}
		
		long teleportIdleInterval    = 1000 * 4; 
		long teleportDefaultInterval = 1000 * 60 * 2;
		@Override
		public int onIdle(ItemEvent event, long duration, int prevMode) {
			int forceRetry = 0;
		  	Point point = MouseInfo.getPointerInfo().getLocation();
		  	if (((int) point.getX() - event.getScanner()._x) > 800 || ((int) point.getY() - event.getScanner()._y) > 600) {
		  		return forceRetry;
		  	}
		  	if (teleportDefaultInterval > 0 && duration > teleportDefaultInterval) {
		  		event.teleport();
		  		return forceRetry;
		  	}

		  	if (duration == 0 && prevMode == ItemEvent.CHAR_MODE_ATTACKING) {
		  		int _x = event.getScanner().getMiddleCellX();
		  		int _y = event.getScanner().getMiddleCellY();
		  		int _f = 1;
		  		while (_f == 1) {
			  		L : for (int x = _x - 5; x <= _x + 5; x += 5) {
				  		for (int y = _y + 10; y >= _y - 10; y -= 5) { 
					  		switch (event.hoverCell(x, y)) {
					  		case ItemEvent.CHAR_MODE_TARGETING: _f = 2; event.attack(); break L;
							case ItemEvent.CHAR_MODE_PICKING  : _f = 1; event.pick()  ; break L;
						  	default: break;
					  		}
				  		}
				  		_f = 0;
			  		}
		  		}
		  		if (_f == 2) {
		  			return forceRetry = 0;
		  		}
		  	}
			ArrayList<Cell> cellList = itemScanner.getNonWhiteCells(30);
		  	ArrayList<Cell> itemList = itemScanner.findItemCells(cellList, 5);
		  	ArrayList<Cell> motiList = itemScanner.getMotionCells();
		  	ArrayList<Cell> scanList = new ArrayList<>();
//		  	scanList.addAll(itemList);
		  	scanList.addAll(motiList);
			if (scanList.size() > 0) {
				Collections.sort(scanList, new ItemScanner.CellDistanceComparator());
				int index = new Random().nextInt(scanList.size() > 10 ? 10: scanList.size());
				Cell cell = scanList.get(index);
				switch (event.hoverCell(cell)) {
				case ItemEvent.CHAR_MODE_TARGETING:
					forceRetry = 0; 
					event.attack();
					break;
				case ItemEvent.CHAR_MODE_PICKING:
					forceRetry = 1; 
					event.pick();
					break;
				default:
					forceRetry = 1;
					event.cancel();
					if (teleportIdleInterval > 0 && duration > teleportIdleInterval) {
						event.teleport();
					}
					break;
				}
			}
			else {
				event.cancel();
				if (teleportIdleInterval > 0 && duration > teleportIdleInterval) {
					event.teleport();
				}
			}
			return forceRetry;
		}
		
	}
	
	
	public class MainHpSpScanner extends HpSpScanner {
		
		public MainHpSpScanner(int _x, int _y) throws AWTException {
			super (_x, _y);
		}
		
		@Override
		public void onPostExecute() {
			super.onPostExecute();
			hpspEvent.execute();
		}
		
	}
	
	public class MainHpSpEvent extends HpSpEvent {
		public MainHpSpEvent(HpSpScanner scanner) {
			super (scanner);
		}
		private long lastFlyWingTime = 0;
		@Override
		public void onHpChanged(int percentage) {
			super.onHpChanged(percentage);
			if (percentage < 45) {
				System.out.println("HP CHANGED >>> " + percentage);
				long now = System.currentTimeMillis();
				if (now - lastFlyWingTime > 3000) {
					lastFlyWingTime = now;
					getScanner().keyPush(KeyEvent.VK_Z);
				}
			}
			if (percentage < 80) {
				System.out.println("HP CHANGED >>> " + percentage);
				getScanner().keyPush(KeyEvent.VK_X);
			}
		}
	}
	
	public class MainItemScanner extends ItemScanner {
		public MainItemScanner(int _x, int _y) throws AWTException {
			super (_x, _y);
		}
		@Override
		public boolean onStart() {
			boolean start = super.onStart();
			scannerFrame.show();
			sleep(1000);
			return start;
		}
		@Override
		public void onPreExecute() {
			super.onPreExecute();
			scannerFrame.clearCells(5);
		}
		@Override
		public void onPostExecute() {
			super.onPostExecute();
			itemEvent.execute();
			scannerFrame.updatePreview(itemScanner.createCellMatrixImage());
		}
	}
	
}
