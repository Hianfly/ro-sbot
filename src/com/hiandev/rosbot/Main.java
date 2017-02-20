package com.hiandev.rosbot;

import java.util.ArrayList;
import java.util.Random;

import com.hiandev.rosbot.Scanner.ScannerListener;
import com.hiandev.rosbot.ui.CellFrame;

/**
 * @author Hian
 *
 */
public class Main {
	
	public static void main(String[] args) {
		try {
			Scanner 
			scanner = new Scanner(new CellFrame(80, 120, 800, 600));
			scanner.setScannerListener(new MyScannerListener());
			scanner.preapare();
			scanner.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class MyScannerListener implements ScannerListener {
		
		@Override
		public void onTargeting(Scanner scanner, int[] data) {
			System.out.println("On Targeting");
			scanner.attack(data);
			scanner.saveMode();
		}
		
		long lastMove = 0;
		@Override
		public void onIdle(Scanner scanner) {
			ArrayList<int[]> data = scanner.getCellChangedDataByDistance();
			if (data == null || data.size() == 0) {
				System.out.println("On Idle Moving");
				long now = System.currentTimeMillis();
				if (now - lastMove > 5) {
					lastMove = now;
					scanner.randomMove();
				}
			}
			else {
				System.out.println("On Idle Target" + data.get(0)[0] + ":" + data.get(0)[1]);
				scanner.target(data.get(0));
			}
		}
		
	}

}
