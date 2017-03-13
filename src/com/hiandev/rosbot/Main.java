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
import com.hiandev.rosbot.scanner.battle.BattleConfig;
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
			
			boolean doit = doWhenStayAtSameLocationReachedItsLimit(now, duration);
			if (doit) {
				return forceRetry;
			}

			if (isAttackDone(duration, prevMode)) {
				switch (BattleConfig.WHEN_ATTACKING_DONE_THEN) {
				case 1: 
		  			int hs = doHardScanSurroundingForItems();
		  			if (hs == BattleScanner.MODE_TARGET) {
		  				return forceRetry;
		  			}
		  			break;
				}
		  	}

			ArrayList<MotionObject> moList = bttlScanner.getMotionObjetList(0);
			if (moList.size() > 0) {
				Collections.sort(moList, new BattleScanner.MotionObjectDistanceComparator());
				MotionObject mo = moList.get(0);
				switch (hoverCell(mo.getMiddleCellX(), mo.getMiddleCellY())) {
				case BattleScanner.MODE_TARGET:
					attack();
					break;
				case BattleScanner.MODE_PICK:
					forceRetry = 1; 
					pick();
					break;
				default:
					forceRetry = 1;
					cancel();
					doWhenIdleReachedItsLimit(now, duration);
					break;
				}
			}
			else {
				cancel();
				doWhenIdleReachedItsLimit(now, duration);
			}
			return forceRetry;
		}
		
		private boolean isAttackDone(long duration, int prevMode) {
			return duration == 0 && prevMode == BattleScanner.MODE_ATTACK;
		}
			
		private int doHardScanSurroundingForItems() {
			int _m = -1;
			int _x = getMiddleCellX();
	  		int _y = getMiddleCellY();
	  		boolean scan = true;
	  		while  (scan) {
		  		L : for (int x = _x - 12; x <= _x + 12; x += 8) {
			  		for (int y = _y + 16; y >= _y -  8; y -= 8) { 
				  		_m = hoverCell(x, y);
				  		switch (_m) {
				  		case BattleScanner.MODE_TARGET: 
				  			attack(); 
				  			break L;
						case BattleScanner.MODE_PICK: 
							pick(); 
							break L;
				  		}
			  		}
		  		}
	  			if (_m == BattleScanner.MODE_TARGET) {
	  				scan = false;
	  			}
	  		}
	  		return _m;
		}
		
		private boolean doWhenIdleReachedItsLimit(long now, long duration) {
			boolean doit = BattleConfig.IDLE_LIMIT > 0 && duration > BattleConfig.IDLE_LIMIT;
			if (doit) {
				switch (BattleConfig.WHEN_IDLE_REACHED_ITS_LIMIT_THEN) {
				case 1:
					teleportLastTime = now;
					teleport(100);
					break;
				}
			}
			return doit;
		}
		
		private boolean doWhenStayAtSameLocationReachedItsLimit(long now, long duration) {
			boolean doit = BattleConfig.STAY_AT_SAME_LOCATION_LIMIT > 0 && duration > BattleConfig.STAY_AT_SAME_LOCATION_LIMIT;
			if (doit) {
				switch (BattleConfig.WHEN_STAY_AT_SAME_LOCATION_REACHED_ITS_LIMIT_THEN) {
				case 1:
					teleportLastTime = now;
			  		teleport();
					break;
				}
			}
			return doit;
		}
		
	}
	
}




//	if (duration == 0 && prevMode == BattleScanner.MODE_ATTACK) {
//		int _x = getMiddleCellX();
//		int _y = getMiddleCellY();
//		int _f = 1;
//		while (_f == 1) {
//  		L : for (int x = _x - 12; x <= _x + 12; x += 8) {
//	  		for (int y = _y + 16; y >= _y -  8; y -= 8) { 
//		  		switch (hoverCell(x, y)) {
//		  		case BattleScanner.MODE_TARGET: _f = 2; attack(); break L;
//				case BattleScanner.MODE_PICK  : _f = 1; pick()  ; break L;
//			  	default: break;
//		  		}
//	  		}
//	  		_f = 0;
//  		}
//		}
//		if (_f == 2) {
//			return forceRetry = 0;
//		}
//	}
