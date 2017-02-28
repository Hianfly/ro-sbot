package com.hiandev.rosbot;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;
import com.hiandev.rosbot.event.BattleEvent;
import com.hiandev.rosbot.event.HpSpEvent;
import com.hiandev.rosbot.profiler.BattleProfiler;
import com.hiandev.rosbot.scanner.BattleScanner;
import com.hiandev.rosbot.scanner.Cell;
import com.hiandev.rosbot.scanner.HpSpScanner;
import com.hiandev.rosbot.ui.ScannerFrame;

/**
 * @author Hian
 *
 */
public class Main {
	
	public static void main(String[] args) {
		new Main();
	}
	
	ScannerFrame scannerFrame = null;
	MainBattleScanner battleScanner = null;
	MainBattleEvent   battleEvent   = null;
	MainHpSpScanner hpspScanner = null;
	MainHpSpEvent hpspEvent = null;
//	HpSpWatcher  hpspWatcher = null;
	
	public Main() {
		try {
			battleScanner  = new MainBattleScanner(5, 30);
			battleEvent    = new MainBattleEvent(battleScanner);
			scannerFrame   = new ScannerFrame(battleScanner);
			hpspScanner    = new MainHpSpScanner(5, 30);
			hpspEvent      = new MainHpSpEvent(hpspScanner);
//			scannerEvent.setEventListener(this);
//			hpspWatcher = new HpSpWatcher(scanner);
			battleScanner.start();
			hpspScanner.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class MainBattleScanner extends BattleScanner {

		public MainBattleScanner(int _x, int _y) throws AWTException {
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
			battleEvent.execute();
			scannerFrame.updatePreview(battleScanner.createCellMatrixImage());
		}
		
	}
	

	public class MainBattleEvent extends BattleEvent {
		
		public MainBattleEvent(BattleScanner scanner) {
			super (scanner);
		}
		
		@Override
		public int onIdle(BattleEvent event) {
			int forceRetry = 0;
			Point point = MouseInfo.getPointerInfo().getLocation();
		  	if (((int) point.getX() - event.getScanner()._x) > 800 || ((int) point.getY() - event.getScanner()._y) > 600) {
		  		return forceRetry;
		  	}
			List<Cell> cellList = ((BattleScanner) event.getScanner()).getNonWhiteCellMatrix();
			if (cellList.size() == 0) {
				event.moveRandomly();
				return forceRetry;
			}
			int index = new Random().nextInt(cellList.size() > 5 ? 5: cellList.size());
			Cell cell = cellList.get(index);
			switch (event.hover(cell)) {
			case BattleEvent.CHAR_MODE_TARGETING:
				forceRetry = 0; 
				event.attack();
				break;
			case BattleEvent.CHAR_MODE_PICKING:
				forceRetry = 1; 
				event.pick();
				break;
			default:
				forceRetry = 1;
				break;
			}
//			System.out.println("REMOVING:" + 0 + " LEFT:" + cellList.size() + " PROFILE-SIZE:" + battleProfiler.getList(BattleProfiler.PROFILE_NOT_CLICKABLE).size());
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
			if (percentage < 40) {
				System.out.println("HP CHANGED >>> " + percentage);
				long now = System.currentTimeMillis();
				if (now - lastFlyWingTime > 2000) {
					lastFlyWingTime = now;
					getScanner().keyPush(KeyEvent.VK_Z);
				}
			}
			if (percentage < 60) {
				System.out.println("HP CHANGED >>> " + percentage);
				getScanner().keyPush(KeyEvent.VK_X);
			}
		}
		
	}
	
}
