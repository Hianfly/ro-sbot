package com.hiandev.rosbot.scanner.text.message;

import java.awt.AWTException;

import com.hiandev.rosbot.scanner.text.TextScanner;

public class MessageScanner extends TextScanner {
	
	public MessageScanner(int _x, int _y) throws AWTException {
		super (_x + 5, _y, 580, 30); // 50
		setAssetsDir("./assets/text-message/");
		setTextPixels(new int[][] { { 250, 250, 0 } });
		setInterval(100);
		setDelay(1421);
	}
	
	@Override
	public void onTextChanged(String[] rowTexts) {
		super.onTextChanged(rowTexts);
		if (rowTexts.length > 0) {
			System.out.println(rowTexts[rowTexts.length - 1]);
		}
	}
	
}
