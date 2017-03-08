package com.hiandev.rosbot;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import com.hiandev.rosbot.event.ItemEvent;
import com.hiandev.rosbot.scanner.Cell;
import com.hiandev.rosbot.scanner.InfoScanner;
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

	ItemScanner     itemScanner = null;
	MainInfoScanner infoScanner = null;
	
	public Main() {
		try {
			itemScanner = new ItemScanner(5, 30);
			itemScanner.setScannerFrame(new ScannerFrame());
			itemScanner.setItemEvent( new MainItemEvent(itemScanner));

			infoScanner = new MainInfoScanner(5, 30);
			infoScanner.setScannerFrame(new ScannerFrame());
		} catch (Exception e) {
			e.printStackTrace();
		}
		itemScanner.start();
		infoScanner.start();
	}
	
	public class MainInfoScanner extends InfoScanner {

		public MainInfoScanner(int _x, int _y) throws AWTException {
			super (_x, _y);
		}
		
		@Override
		protected void onHpChanged(int oldHp, int newHp, int oldHpMax, int newHpMax) {
			super.onHpChanged(oldHp, newHp, oldHpMax, newHpMax);
			int percentage = (newHp * 100) / newHpMax;
			if (percentage < 50) {
				keyPush(KeyEvent.VK_X);
			}
			if (percentage < 25) {
				keyPush(KeyEvent.VK_Z);
			}
		}
		
		@Override
		protected void onSpChanged(int oldSp, int newSp, int oldSpMax, int newSpMax) {
			super.onSpChanged(oldSp, newSp, oldSpMax, newSpMax);
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
			  		L : for (int x = _x -  8; x <= _x + 8; x += 8) {
				  		for (int y = _y + 16; y >= _y - 8; y -= 8) { 
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
	
}
