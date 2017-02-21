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
		public void onMoving(Scanner scanner) {
			System.out.println("On Moving");
		}
		
		@Override
		public void onAttacking(Scanner scanner) {
			System.out.println("On Attacking");
		}
		
		@Override
		public void onTargeting(Scanner scanner, int[] data) {
			System.out.println("On Targeting");
			scanner.attack();
		}
		
		@Override
		public void onIdle(Scanner scanner) {
//			ArrayList<int[]> data = scanner.getCellChangedDataByDistance();
//			if (data == null || data.size() == 0) {
//				scanner.moveRandomly();
//			}
//			else {
//				int index = data.size() > 50 ? 50 : data.size();
//				while (--index >= 0) {
//					int r = scanner.target(data.get(index));
//					if (r == 0) {
//						break;
//					}
//				}
//			}
		}
		
	}

}
