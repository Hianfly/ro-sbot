package com.hiandev.rosbot.scanner.text.login;

import java.awt.AWTException;
import com.hiandev.rosbot.scanner.text.TextScanner;

public class CharacterSelectScanner extends TextScanner {
	
	public CharacterSelectScanner(int _x, int _y) throws AWTException {
		super (_x, _y, 575, 358);
		setAssetsDir("./assets/text-login-roman/");
		setTextPixels(new int[][] { { 0, 0, 0 } });
		setInterval(1000);
		setDelay(1000);
	}
	
}
