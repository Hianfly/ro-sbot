package com.hiandev.rosbot;

import com.hiandev.rosbot.ui.CellFrame;

/**
 * @author Hian
 *
 */
public class Main {
	
	public static void main(String[] args) {
		try {
			new Scanner(new CellFrame(40, 40, 800, 600)).preapare().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
