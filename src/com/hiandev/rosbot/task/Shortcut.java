package com.hiandev.rosbot.task;

import java.awt.event.KeyEvent;
import com.hiandev.rosbot.scanner.battle.BattleScanner;

public class Shortcut {

	public static Shortcut shortcut = null;
	public static synchronized Shortcut getInstance() {
		if (shortcut == null) {
			shortcut = new Shortcut();
		}
		return shortcut;
	}
	private Shortcut() {
		
	}
	
	public boolean push(BattleScanner scanner) {
		boolean push = false;
		long now = System.currentTimeMillis();
		if (f5Time == 0 || now - f5Time >= ShortcutConfig.F5_INTERVAL) {
			f5Time = now;
			push = true;
			scanner.keyPush(KeyEvent.VK_F5, 100);
		}
		if (f6Time == 0 || now - f6Time >= ShortcutConfig.F6_INTERVAL) {
			f6Time = now;
			push = true;
			scanner.keyPush(KeyEvent.VK_F6, 100);
		}
		if (f7Time == 0 || now - f7Time >= ShortcutConfig.F7_INTERVAL) {
			f7Time = now;
			push = true;
			scanner.keyPush(KeyEvent.VK_F7, 100);
		}
		if (f8Time == 0 || now - f8Time >= ShortcutConfig.F8_INTERVAL) {
			f8Time = now;
			push = true;
			scanner.keyPush(KeyEvent.VK_F8, 100);
		}
		return push;
	}

	private long f5Time = 0;
	private long f6Time = 0;
	private long f7Time = 0;
	private long f8Time = 0;
	
}
