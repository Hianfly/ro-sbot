package com.hiandev.rosbot.scanner.map;

import com.hiandev.rosbot.Config;

public class PortalConfig extends Config {

	public PortalConfig(String name) {
		super (name);
	}

	public static String PORTAL_LOCATIONS = "";
	
	protected void onLoaded() {
		
		PORTAL_LOCATIONS = getString("portal_locations", "0,0;");
		
	}
	
}
