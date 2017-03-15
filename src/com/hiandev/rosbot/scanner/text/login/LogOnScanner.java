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
		setDelay(5000);
	}
	
	@Override
	public final void onTextChanged(String[] rowTexts) {
		super.onTextChanged(rowTexts);
		try {
			System.out.println("### " + rowTexts[0]);
			if (normalize(rowTexts[0]).toLowerCase().startsWith("logon")) {
				P : {
					if (doLogon() <= 0) {
						break P;
					}
					if (doServerSelection(271, 326) <= 0) {
						break P;
					}
					if (doCharacterSelection(120, 156) <= 0) {
						break P;
					}
					GlobalVar.setGameState(GlobalVar.GAME_STATE_BATTLE);
				}
				resetTextList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int doLogon() {
		Util.copyToClipboard(LogOnConfig.PASSWORD);
		keyPaste();
		sleep(1000);
		keyPush(KeyEvent.VK_ENTER);
		return 1;
	}
	
	private int doServerSelection(int _x, int _y) {
		int r = 20;
		try {
			ChooseServerScanner css = null;
			while (r-- > 0) {
				css = new ChooseServerScanner(_x, _y);
				css.startForeground(1);
				String[] textList = css.getCurrentTextList();
				System.out.println(">>> " + textList[0]);
				if (textList.length > 0 && normalize(textList[0]).toLowerCase().startsWith("pilih server")) {
					keyPush(KeyEvent.VK_ENTER);
					break;
				}
			}
			if (r <= 0) {
				GlobalVar.setGameState(GlobalVar.GAME_STATE_DISCONNECT);
				keyPush(KeyEvent.VK_ENTER);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}
	
	private int doCharacterSelection(int _x, int _y) {
		int s = 20;
		try {
			CharacterSelectScanner ssc = null;
			while (s-- > 0) {
				ssc = new CharacterSelectScanner(_x, _y);
				ssc.startForeground(1);
				String[] textList = ssc.getCurrentTextList();
				System.out.println(">>> " + textList[0]);
				if (textList.length > 0 && normalize(textList[0]).toLowerCase().startsWith("character select")) {
					keyPush(KeyEvent.VK_ENTER);
					break;
				}
			}
			if (s <= 0) {
				keyPush(KeyEvent.VK_ENTER);
				GlobalVar.setGameState(GlobalVar.GAME_STATE_DISCONNECT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
}
