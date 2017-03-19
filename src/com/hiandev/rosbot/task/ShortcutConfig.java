package com.hiandev.rosbot.task;

import com.hiandev.rosbot.Config;

public class ShortcutConfig extends Config {

	public ShortcutConfig(String name) {
		super (name);
	}

	public static int F5_INTERVAL = 60000;
	public static int F6_INTERVAL = 60000;
	public static int F7_INTERVAL = 60000;
	public static int F8_INTERVAL = 60000;
	
	protected void onLoaded() {
		F5_INTERVAL = getInt("f5_interval", 60000);
		F6_INTERVAL = getInt("f6_interval", 60000);
		F7_INTERVAL = getInt("f7_interval", 60000);
		F8_INTERVAL = getInt("f8_interval", 60000);
	}
	
}
