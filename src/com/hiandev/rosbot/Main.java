package com.hiandev.rosbot;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.hiandev.rosbot.scanner.PreviewFrame;
import com.hiandev.rosbot.scanner.ScannerFrame;
import com.hiandev.rosbot.scanner.battle.Cell;
import com.hiandev.rosbot.scanner.battle.MotionObject;
import com.hiandev.rosbot.scanner.battle.BattleScanner;
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

	MainBattleScanner  bttlScanner = null;
	MainInfoScanner    infoScanner = null;
	MainMessageScanner mssgScanner = null;
	UIFrame            uiFrame     = null;
	
	public Main() {
		try {
			mssgScanner = new MainMessageScanner(5, 553 + 17);
			mssgScanner.setScannerFrame(new ScannerFrame());
//			mssgScanner.setPreviewFrame(new PreviewFrame());
			mssgScanner.setDebug(true);
			
			bttlScanner = new MainBattleScanner(5, 30);
			bttlScanner.setScannerFrame(new ScannerFrame());
			bttlScanner.setPreviewFrame(new PreviewFrame(200 + 15 + 15, 0));
			bttlScanner.setDebug(false);
			
			infoScanner = new MainInfoScanner(5, 30);
			infoScanner.setScannerFrame(new ScannerFrame());
//			infoScanner.setPreviewFrame(new PreviewFrame());
			infoScanner.setDebug(true);
			
			uiFrame = new UIFrame(bttlScanner._w + 15, 0, 200, bttlScanner._h);
		} catch (Exception e) {
			e.printStackTrace();
		}
		bttlScanner.start();
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
			try {
				super.onPreExecute();
				if (getHpMax() == 0) {
					return;
				}
				long now = System.currentTimeMillis();
				if (lastHpChangedTime > 0 && now - lastHpChangedTime > 3000) {
					lastHpChangedTime = 0;
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
			try {
				if (newHpMax == 0) {
					return;
				}
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		protected void onSpChanged(int oldSp, int newSp, int oldSpMax, int newSpMax) {
			super.onSpChanged(oldSp, newSp, oldSpMax, newSpMax);
		}
		
	}
	
	public class MainBattleScanner extends BattleScanner {
		
		public MainBattleScanner(int _x, int _y) throws AWTException {
			super (_x, _y);
		}
		
		long teleportIdleInterval    = 1000 * 2; 
		long teleportDefaultInterval = 1000 * 60 * 2;
		long teleportLastTime        = 0;
		@Override
		public int onIdle(long duration, int prevMode) {
		  	Point point = MouseInfo.getPointerInfo().getLocation();
		  	if (((int) point.getX() - _x) > 800 || ((int) point.getY() - _y) > 600) {
		  		return 0;
		  	}
		  	/*
		  	 * 
		  	 * 
		  	 * 
		  	 */
			int forceRetry = 0;
			long now = System.currentTimeMillis();
//			if (teleportLastTime > 0 && now - teleportLastTime < 500) {
//				return 0;
//			}
			
			if (teleportDefaultInterval > 0 && duration > teleportDefaultInterval) {
		  		teleport();
		  		return forceRetry;
		  	}

		  	if (duration == 0 && prevMode == BattleScanner.MODE_ATTACK) {
		  		int _x = getMiddleCellX();
		  		int _y = getMiddleCellY();
		  		int _f = 1;
		  		while (_f == 1) {
			  		L : for (int x = _x - 12; x <= _x + 12; x += 8) {
				  		for (int y = _y + 16; y >= _y -  8; y -= 8) { 
					  		switch (hoverCell(x, y)) {
					  		case BattleScanner.MODE_TARGET: _f = 2; attack(); break L;
							case BattleScanner.MODE_PICK  : _f = 1; pick()  ; break L;
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
		  	

			ArrayList<MotionObject> moList = bttlScanner.getMotionObjetList(0);
			if (moList.size() > 0) {
				Collections.sort(moList, new BattleScanner.MotionObjectDistanceComparator());
				int index = new Random().nextInt(moList.size() > 1 ? 1: moList.size());
				MotionObject mo = moList.get(index);
				switch (hoverCell(mo.getMiddleCellX(), mo.getMiddleCellY())) {
				case BattleScanner.MODE_TARGET:
					forceRetry = 0; 
					attack();
					break;
				case BattleScanner.MODE_PICK:
					forceRetry = 1; 
					pick();
					break;
				default:
					forceRetry = 1;
					cancel();
					if (teleportIdleInterval > 0 && duration > teleportIdleInterval) {
						teleportLastTime = now;
						System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< " + duration);
						teleport();
						sleep(50);
					}
					break;
				}
			}
			else {
				cancel();
				if (teleportIdleInterval > 0 && duration > teleportIdleInterval) {
					teleportLastTime = now;
					System.out.println("<<<<<<<adasdasdasdasdasdas x<<<<<<<<<< " + duration);
					teleport();
					sleep(50);
//					sleep(1000);
				}
			}
		  	
		  	
		  	
//			ArrayList<Cell> cellList = bttlScanner.getNonWhiteCells(30);
//		  	ArrayList<Cell> itemList = bttlScanner.findItemCells(cellList, 5);
//		  	ArrayList<Cell> motiList = bttlScanner.getMotionCells();
//		  	ArrayList<Cell> scanList = new ArrayList<>();
//		  	scanList.addAll(itemList);
//		  	scanList.addAll(motiList);
//			if (scanList.size() > 0) {
//				Collections.sort(scanList, new BattleScanner.CellDistanceComparator());
//				int index = new Random().nextInt(scanList.size() > 10 ? 10: scanList.size());
//				Cell cell = scanList.get(index);
//				switch (hoverCell(cell)) {
//				case BattleScanner.MODE_TARGET:
//					forceRetry = 0; 
//					attack();
//					break;
//				case BattleScanner.MODE_PICK:
//					forceRetry = 1; 
//					pick();
//					break;
//				default:
//					forceRetry = 1;
//					cancel();
//					if (teleportIdleInterval > 0 && duration > teleportIdleInterval) {
//						teleport();
////						sleep(1000);
//					}
//					break;
//				}
//			}
//			else {
//				cancel();
//				if (teleportIdleInterval > 0 && duration > teleportIdleInterval) {
//					teleport();
////					sleep(1000);
//				}
//			}
			return forceRetry;
		}
		
	}
	
}
