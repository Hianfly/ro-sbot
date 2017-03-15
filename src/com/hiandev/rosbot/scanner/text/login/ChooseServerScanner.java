package com.hiandev.rosbot.scanner.text.login;

import java.awt.AWTException;
import java.awt.event.KeyEvent;

import com.hiandev.rosbot.GlobalVar;
import com.hiandev.rosbot.Util;
import com.hiandev.rosbot.scanner.text.TextScanner;

public class ChooseServerScanner extends TextScanner {
	
	public ChooseServerScanner(int _x, int _y) throws AWTException {
		super (_x, _y, 280, 200);
		setAssetsDir("./assets/text-login/");
		setTextPixels(new int[][] { { 0, 0, 0 } });
		setInterval(5000);
	}
	
	@Override
	public void onTextChanged(String[] rowTexts) {
		super.onTextChanged(rowTexts);
		try {
			if (GlobalVar.getGameState() != GlobalVar.GAME_STATE_LOGON) {
				return;
			}
			System.out.println(rowTexts[0]);
			if (normalize(rowTexts[0]).toLowerCase().startsWith("pilih server")) {
				onChooseServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onChooseServer() {
		sleep(5000);
		keyPush(KeyEvent.VK_ENTER);
		GlobalVar.setGameState(GlobalVar.GAME_STATE_CHOOSE_SERVER);
	}
	
}
