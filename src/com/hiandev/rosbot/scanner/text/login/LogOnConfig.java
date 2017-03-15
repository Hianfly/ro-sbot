package com.hiandev.rosbot.scanner.text.login;

import com.hiandev.rosbot.Config;

public class LogOnConfig extends Config {

	public LogOnConfig() {
		super ("logon-config.txt");
	}
	
	public static String PASSWORD = "";
	
	protected void onLoaded() {
		
		PASSWORD = getString("password", "");
		
	}
	
}
