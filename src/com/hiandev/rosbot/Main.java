package com.hiandev.rosbot;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import com.hiandev.rosbot.scanner.PreviewFrame;
import com.hiandev.rosbot.scanner.ScannerFrame;
import com.hiandev.rosbot.scanner.battle.MotionObject;
import com.hiandev.rosbot.scanner.map.MapsScanner;
import com.hiandev.rosbot.scanner.battle.BattleConfig;
import com.hiandev.rosbot.scanner.battle.BattleScanner;
import com.hiandev.rosbot.scanner.text.dialog.DialogScanner;
import com.hiandev.rosbot.scanner.text.info.InfoScanner;
import com.hiandev.rosbot.scanner.text.logon.LogOnConfig;
import com.hiandev.rosbot.scanner.text.logon.LogOnScanner;
import com.hiandev.rosbot.scanner.text.message.MessageScanner;
import com.hiandev.rosbot.task.Shortcut;
import com.hiandev.rosbot.task.ShortcutConfig;
import com.hiandev.rosbot.ui.UIFrame;

/**
 * @author Hian
 *
 */
public class Main {
	
	public static void main(String[] args) {
		new Main();
	}

	LogOnScanner       lognScanner = null;
	DialogScanner      dlogScanner = null;
	MainBattleScanner  bttlScanner = null;
	MainInfoScanner    infoScanner = null;
	MainMessageScanner mssgScanner = null;
	MainMapsScanner     mapsScanner = null;
	UIFrame            uiFrame     = null;
	
	public Main() {
		try {
			new     MainConfig("main-config.txt").load();
			new   BattleConfig(MainConfig  .BATTLE_CONFIG_NAME).load();
			new    LogOnConfig(MainConfig   .LOGON_CONFIG_NAME).load();
			new ShortcutConfig(MainConfig.SHORTCUT_CONFIG_NAME).load();

			lognScanner = new LogOnScanner(271, 406);
			lognScanner.setDebug(true);
			dlogScanner = new DialogScanner(271, 288);
			dlogScanner.setDebug(true);
			
			mssgScanner = new MainMessageScanner(5, 553 + 17);
			mssgScanner.setScannerFrame(new ScannerFrame());
			mssgScanner.setDebug(true);
			bttlScanner = new MainBattleScanner(5, 30);
			bttlScanner.setScannerFrame(new ScannerFrame());
			bttlScanner.setPreviewFrame(new PreviewFrame(200 + 15 + 15, 0));
			bttlScanner.setDebug(false);
			infoScanner = new MainInfoScanner(5, 30);
			infoScanner.setScannerFrame(new ScannerFrame());
			infoScanner.setDebug(true);
			mapsScanner = new MainMapsScanner();
			mapsScanner.setScannerFrame(new ScannerFrame());
			mapsScanner.setDebug(true);
			mapsScanner.setPreviewFrame(new PreviewFrame(15, 0));
			uiFrame = new UIFrame(bttlScanner._w + 15, 0, 200, bttlScanner._h);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		lognScanner.start();
		dlogScanner.start();
		bttlScanner.start();
		infoScanner.start();
		mssgScanner.start();
//		mapsScanner.start();
		uiFrame.show();
	}
	
	public class MainConfig extends Config {

		public MainConfig(String name) {
			super (name);
		}
		
		@Override
		protected void onLoaded() {
			BATTLE_CONFIG_NAME   = getString(  "battle_config_name",   "battle-config.txt");
			LOGON_CONFIG_NAME    = getString(   "logon_config_name",    "logon-config.txt");
			SHORTCUT_CONFIG_NAME = getString("shortcut_config_name", "shortcut-config.txt");
		}
		
	}
	
	
	public class MainMapsScanner extends MapsScanner {
		
		public MainMapsScanner() throws AWTException {
			super ();
		}

		@Override
		public void onLocationChanged(int _mx, int _my) {
			System.out.println("Location --> " + _mx + " : "  + _my + " : " + getRouteIndex());
			int[] r = getNextRoute();
			if (r == null) {
				setRouteIndex(-1);
				reverseRoutes();
				return;
			}
			boolean routeChanged = false;
			if (Math.abs(_mx - r[0]) <= 1 && Math.abs(_my - r[1]) <= 1) {
				setRouteIndex(getRouteIndex() + 1);
				routeChanged = true;
			}
			if (routeChanged) {
				int[] n = getNextRoute();
				if (n == null) {
					return;
				}
				int  xx = 400 + ((n[0] - _mx) * 90);
				int  yy = 300 + ((n[1] - _my) * 80);
				System.out.println(xx + ":" + yy);
				bttlScanner.mouseGoto(xx, yy);
				sleep(100);
				bttlScanner.mouseLeftClick();
			}
		}
		
	}
	
	public class MainMessageScanner extends MessageScanner {
		public MainMessageScanner(int _x, int _y) throws AWTException {
			super (_x, _y);
		}
	}
	
	public class MainInfoScanner extends InfoScanner {
		public MainInfoScanner(int _x, int _y) throws AWTException {
			super (_x, _y);
		}
		@Override
		protected void onPreExecute() {
			try {
				super.onPreExecute();
				int percentage = getHpMax() == 0 ? 100 : (getHp() * 100) / getHpMax();
				if (percentage < BattleConfig.USE_POTION_IF_HP_BELOW_THAN) {
					bttlScanner.consumeHpPotion(50);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		protected void onHpChanged(int oldHp, int newHp, int oldHpMax, int newHpMax) {
			try {
				super.onHpChanged(oldHp, newHp, oldHpMax, newHpMax);
				int percentage = newHpMax == 0 ? 100 : (newHp * 100) / newHpMax;
				if (percentage < BattleConfig.DO_TELEPORTATION_IF_HP_BELOW_THAN && newHp < oldHp) {
					bttlScanner.teleport(50);
				}
				if (percentage < BattleConfig.USE_POTION_IF_HP_BELOW_THAN) {
					bttlScanner.consumeHpPotion(50);
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
		long teleportLastTime = 0;
		@Override
		protected int onTeleported() {
			int t = super.onTeleported();
			teleportLastTime = System.currentTimeMillis();
			return t;
		}
		@Override
		public int onIdle(long duration, int prevMode) {
			int forceRetry = 0;
		  	Point point = MouseInfo.getPointerInfo().getLocation();
		  	if (((int) point.getX() - _x) > 800 || ((int) point.getY() - _y) > 600) {
		  		return forceRetry;
		  	}
		  	if (GlobalVar.getGameState() != GlobalVar.GAME_STATE_BATTLE) {
		  		return forceRetry;
		  	}
//		  	if (forceRetry == 0) {
//		  		return forceRetry;
//		  	}
		  	/*
		  	 * 
		  	 */
			long now = System.currentTimeMillis();
			if (teleportLastTime > 0 && now - teleportLastTime < 1000) {
				return 0;
			}
		  	/*
		  	 * 
		  	 */
			if (isAttackDone(duration, prevMode)) {
				switch (BattleConfig.WHEN_ATTACK_DONE_THEN) {
				case 1: 
		  			int hs = doHardScanSurroundingForItems();
		  			if (hs == BattleScanner.MODE_TARGET) {
		  				return forceRetry;
		  			}
		  			break;
				}
		  	}
		  	/*
		  	 * 
		  	 */
			boolean push = Shortcut.getInstance().push(this);
			if (push) {
				return 0;
			}
		  	/*
		  	 * 
		  	 */
			boolean doit = doWhenStayAtSameLocationReachedItsLimit(now, duration);
			if (doit) {
				return forceRetry;
			}
		  	/*
		  	 * 
		  	 */
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
		  	/*
		  	 * 
		  	 */
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
	  			scan = _m == BattleScanner.MODE_PICK;
	  		}
	  		return _m;
		}
		
		private boolean doWhenIdleReachedItsLimit(long now, long duration) {
			boolean doit = BattleConfig.MAX_IDLE_DURATION > 0 && duration > BattleConfig.MAX_IDLE_DURATION;
			if (doit) {
				switch (BattleConfig.WHEN_IDLE_REACHED_ITS_LIMIT_THEN) {
				case 1:
					teleport();
					break;
				case 2:
					
					break;
				}
			}
			return doit;
		}
		
		private boolean doWhenStayAtSameLocationReachedItsLimit(long now, long duration) {
			boolean doit = BattleConfig.MAX_STAY_AT_SAME_LOCATION_DURATION > 0 && duration > BattleConfig.MAX_STAY_AT_SAME_LOCATION_DURATION;
			if (doit) {
				switch (BattleConfig.WHEN_STAY_AT_SAME_LOCATION_REACHED_ITS_LIMIT_THEN) {
				case 1:
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
