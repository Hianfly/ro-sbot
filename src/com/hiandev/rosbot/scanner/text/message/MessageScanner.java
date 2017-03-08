package com.hiandev.rosbot.scanner.text.message;

import java.awt.AWTException;

import com.hiandev.rosbot.scanner.text.TextScanner;

public class MessageScanner extends TextScanner {
	
	public MessageScanner(int _x, int _y) throws AWTException {
		super (_x, _y, 500, 55);
		setAssetsDir("./assets/text-message/");
		setTextPixels(new int[][] { { 50, 250, 250 } });
		setInterval(100);
	}
	
	@Override
	public void onTextChanged(String[] rowTexts) {
		super.onTextChanged(rowTexts);
		for (String t : rowTexts) {
			System.out.println(t);
		}
	}
	
}
