package com.hiandev.rosbot.scanner.text.login;

import java.awt.AWTException;
import java.awt.event.KeyEvent;
import com.hiandev.rosbot.GlobalVar;
import com.hiandev.rosbot.Util;
import com.hiandev.rosbot.scanner.text.TextScanner;

public class LogOnScanner extends TextScanner {
	
	public LogOnScanner(int _x, int _y) throws AWTException {
		super (_x, _y, 280, 120);
		setAssetsDir("./assets/text-login/");
		setTextPixels(new int[][] { { 0, 0, 0 } });
		setInterval(5000);
	}
	
	@Override
	public void onTextChanged(String[] rowTexts) {
		super.onTextChanged(rowTexts);
		try {
			if (GlobalVar.getGameState() != GlobalVar.GAME_STATE_DISCONNECT) {
				return;
			}
			System.out.println(rowTexts[0]);
			if (normalize(rowTexts[0]).toLowerCase().startsWith("logon")) {
				onLogOn();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onLogOn() {
		sleep(5000);
		String password = "f006efc56";
		Util.copyToClipboard(password);
		keyPaste();
		sleep(1000);
		keyPush(KeyEvent.VK_ENTER);
		GlobalVar.setGameState(GlobalVar.GAME_STATE_LOGON);
		int r = 20;
		while (r-- > 0) {
			sleep(1000);
			if (GlobalVar.getGameState() != GlobalVar.GAME_STATE_LOGON) {
				break;
			}
			System.out.println(r);
		}
		if (r <= 0) {
			keyPush(KeyEvent.VK_ENTER);
			GlobalVar.setGameState(GlobalVar.GAME_STATE_DISCONNECT);
		}
	}
	
}
