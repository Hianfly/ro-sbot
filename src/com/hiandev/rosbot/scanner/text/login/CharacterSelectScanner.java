package com.hiandev.rosbot.scanner.text.login;

import java.awt.AWTException;
import java.awt.event.KeyEvent;

import com.hiandev.rosbot.GlobalVar;
import com.hiandev.rosbot.Util;
import com.hiandev.rosbot.scanner.text.TextScanner;

public class CharacterSelectScanner extends TextScanner {
	
	public CharacterSelectScanner(int _x, int _y) throws AWTException {
		super (_x, _y, 575, 358);
		setAssetsDir("./assets/text-login-roman/");
		setTextPixels(new int[][] { { 0, 0, 0 } });
		setInterval(5000);
	}
	
	@Override
	public void onTextChanged(String[] rowTexts) {
		super.onTextChanged(rowTexts);
		try {
//			if (GlobalVar.getGameState() != GlobalVar.GAME_STATE_CHOOSE_SERVER) {
//				return;
//			}
			System.out.println(rowTexts[0]);
			if (normalize(rowTexts[0]).toLowerCase().startsWith("character select")) {
				onCharacterSelect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onCharacterSelect() {
		sleep(5000);
		keyPush(KeyEvent.VK_ENTER);
		GlobalVar.setGameState(GlobalVar.GAME_STATE_CHARACTER_SELECT);
	}
	
}
