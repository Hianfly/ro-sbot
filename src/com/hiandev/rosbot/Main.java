package com.hiandev.rosbot;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import com.hiandev.rosbot.scanner.ScannerFrame;
import com.hiandev.rosbot.scanner.battle.Cell;
import com.hiandev.rosbot.scanner.battle.ItemScanner;
import com.hiandev.rosbot.scanner.text.info.InfoScanner;
import com.hiandev.rosbot.scanner.text.message.MessageScanner;
import com.hiandev.rosbot.ui.UIFrame;

/**
 * @author Hian
 *
 */
public class Main {
	
	public static void main(String[] args) {
		new Main();
	}

	MainItemScanner itemScanner = null;
	MainInfoScanner infoScanner = null;
	MainMessageScanner mssgScanner = null;
	UIFrame uiFrame = null;
	
	public Main() {
		try {
			mssgScanner = new MainMessageScanner(5, 571);
			mssgScanner.setDebug(false);
			mssgScanner.setScannerFrame(new ScannerFrame());
			itemScanner = new MainItemScanner(5, 30);
			itemScanner.setScannerFrame(new ScannerFrame());
			itemScanner.setDebug(false);
			infoScanner = new MainInfoScanner(5, 30);
			infoScanner.setScannerFrame(new ScannerFrame());
			infoScanner.setDebug(false);
			uiFrame = new UIFrame(5, 30 + itemScanner._h, itemScanner._w, itemScanner._h);
		} catch (Exception e) {
			e.printStackTrace();
		}
		itemScanner.start();
		infoScanner.start();
		mssgScanner.start();
		uiFrame.show();
	}
	
	public class MainMessageScanner extends MessageScanner {
		
		public MainMessageScanner(int _x, int _y) throws AWTException {
			super (_x, _y);
		}
		
	}
	
	public class MainInfoScanner extends InfoScanner {
		private int prmPotionThreshold = 80;
		private int prmTeleportThreshold = 60;
		public MainInfoScanner(int _x, int _y) throws AWTException {
			super (_x, _y);
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				long now = System.currentTimeMillis();
				if (lastHpChangedTime > 0 && now - lastHpChangedTime > 3000) {
					int percentage = (getHp() * 100) / getHpMax();
					if (percentage < prmPotionThreshold) {
						keyPush(KeyEvent.VK_X);
						sleep(50);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		private long lastHpChangedTime = 0; 
		@Override
		protected void onHpChanged(int oldHp, int newHp, int oldHpMax, int newHpMax) {
			super.onHpChanged(oldHp, newHp, oldHpMax, newHpMax);
			lastHpChangedTime = System.currentTimeMillis();
			int percentage = (newHp * 100) / newHpMax;
			if (percentage < prmTeleportThreshold) {
				keyPush(KeyEvent.VK_Z);
				sleep(50);
			}
			if (percentage < prmPotionThreshold) {
				keyPush(KeyEvent.VK_X);
				sleep(50);
			}
		}
		@Override
		protected void onSpChanged(int oldSp, int newSp, int oldSpMax, int newSpMax) {
			super.onSpChanged(oldSp, newSp, oldSpMax, newSpMax);
		}
		
	}
	
	public class MainItemScanner extends ItemScanner {
		
		public MainItemScanner(int _x, int _y) throws AWTException {
			super (_x, _y);
		}
		
		long teleportIdleInterval    = 1000 * 4; 
		long teleportDefaultInterval = 1000 * 60 * 2;
		@Override
		public int onIdle(long duration, int prevMode) {
			int forceRetry = 0;
		  	Point point = MouseInfo.getPointerInfo().getLocation();
		  	if (((int) point.getX() - _x) > 800 || ((int) point.getY() - _y) > 600) {
		  		return forceRetry;
		  	}
		  	if (teleportDefaultInterval > 0 && duration > teleportDefaultInterval) {
		  		teleport();
		  		return forceRetry;
		  	}

//		  	if (duration == 0 && prevMode == ItemScanner.MODE_ATTACK) {
//		  		int _x = getMiddleCellX();
//		  		int _y = getMiddleCellY();
//		  		int _f = 1;
//		  		while (_f == 1) {
//			  		L : for (int x = _x -  8; x <= _x + 8; x += 8) {
//				  		for (int y = _y + 16; y >= _y - 8; y -= 8) { 
//					  		switch (hoverCell(x, y)) {
//					  		case ItemScanner.MODE_TARGET: _f = 2; attack(); break L;
//							case ItemScanner.MODE_PICK  : _f = 1; pick()  ; break L;
//						  	default: break;
//					  		}
//				  		}
//				  		_f = 0;
//			  		}
//		  		}
//		  		if (_f == 2) {
//		  			return forceRetry = 0;
//		  		}
//		  	}
		  	
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
				switch (hoverCell(cell)) {
				case ItemScanner.MODE_TARGET:
					forceRetry = 0; 
					attack();
					break;
				case ItemScanner.MODE_PICK:
					forceRetry = 1; 
					pick();
					break;
				default:
					forceRetry = 1;
					cancel();
					if (teleportIdleInterval > 0 && duration > teleportIdleInterval) {
						teleport();
					}
					break;
				}
			}
			else {
				cancel();
				if (teleportIdleInterval > 0 && duration > teleportIdleInterval) {
					teleport();
				}
			}
			return forceRetry;
		}
		
	}
	
}
