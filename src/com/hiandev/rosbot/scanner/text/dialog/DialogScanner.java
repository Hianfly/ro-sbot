package com.hiandev.rosbot.scanner.text.dialog;

import java.awt.AWTException;
import java.awt.event.KeyEvent;

import com.hiandev.rosbot.GlobalVar;
import com.hiandev.rosbot.scanner.text.TextScanner;

public class DialogScanner extends TextScanner {
	
	public DialogScanner(int _x, int _y) throws AWTException {
		super (_x, _y, 280, 104);
		setAssetsDir("./assets/text-dialog/");
		setTextPixels(new int[][] { { 0, 0, 0 } });
		setInterval(5000);
		setDelay(3245);
	}
	
	@Override
	public final void onTextChanged(String[] rowTexts) {
		super.onTextChanged(rowTexts);
		try {
			if (rowTexts.length > 0 && normalize(rowTexts[0]).toLowerCase().startsWith("terputus dari server")) {
				GlobalVar.setGameState(GlobalVar.GAME_STATE_DISCONNECT);
				keyPush(KeyEvent.VK_ENTER);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
