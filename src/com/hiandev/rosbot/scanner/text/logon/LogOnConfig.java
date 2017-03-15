package com.hiandev.rosbot.scanner.text.logon;

import com.hiandev.rosbot.Config;

public class LogOnConfig extends Config {

	public LogOnConfig() {
		super ("logon-config.txt");
	}

	public static int AUTO_LOGON_ENABLED = 0;
	public static String PASSWORD = "";
	
	protected void onLoaded() {
		
		AUTO_LOGON_ENABLED = getInt("auto_logon_enabled", 0);
		PASSWORD = getString("password", "");
		
	}
	
}
