package com.hiandev.rosbot.scanner.text.dialog;

import java.awt.AWTException;
import java.awt.event.KeyEvent;

import com.hiandev.rosbot.GlobalVar;
import com.hiandev.rosbot.scanner.text.TextScanner;

public class DisconnectScanner extends TextScanner {
	
	public DisconnectScanner(int _x, int _y) throws AWTException {
		super (_x, _y, 280, 104);
		setAssetsDir("./assets/text-dialog/");
		setTextPixels(new int[][] { { 0, 0, 0 } });
		setInterval(5000);
	}
	
	@Override
	public void onTextChanged(String[] rowTexts) {
		super.onTextChanged(rowTexts);
		try {
			System.out.println(rowTexts[0]);
			if (normalize(rowTexts[0]).toLowerCase().startsWith("terputus dari server.")) {
				onDisconnected();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onDisconnected() {
		sleep(5000);
		keyPush(KeyEvent.VK_ENTER);
		GlobalVar.setGameState(GlobalVar.GAME_STATE_DISCONNECT);
	}
	
}
