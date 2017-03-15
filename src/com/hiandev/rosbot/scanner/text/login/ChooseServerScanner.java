package com.hiandev.rosbot.scanner.text.login;

import java.awt.AWTException;
import com.hiandev.rosbot.scanner.text.TextScanner;

public class ChooseServerScanner extends TextScanner {
	
	public ChooseServerScanner(int _x, int _y) throws AWTException {
		super (_x, _y, 280, 200);
		setAssetsDir("./assets/text-login/");
		setTextPixels(new int[][] { { 0, 0, 0 } });
		setInterval(1000);
		setDelay(1000);
	}
	
}
