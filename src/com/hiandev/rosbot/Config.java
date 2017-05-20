package com.hiandev.rosbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
	
	public static String BATTLE_CONFIG_NAME = "";
	public static String LOGON_CONFIG_NAME = "";
	public static String SHORTCUT_CONFIG_NAME = "";
	public static String PORTAL_CONFIG_NAME = "";

	public static int BATTLE_SCANNER_ENABLED = 1;
	public static int INFO_SCANNER_ENABLED = 1;
	public static int MESSAGE_SCANNER_ENABLED = 1;
	public static int POPUP_SCANNER_ENABLED = 1;
	public static int LOGON_SCANNER_ENABLED = 1;
	public static int MAPS_SCANNER_ENABLED = 1;
		
	public Config(String name) {
		this.name = name;
	}
	
	private String name = "";

	private ConcurrentHashMap<String, String> MAP = new ConcurrentHashMap<>();
	public String getString(String key, String def) {
		return MAP.getOrDefault(key, def);
	}
	public long getLong(String key, long def) {
		long res = def;
		try {
			 res = Long.parseLong(MAP.get(key));
		} catch (Exception e) {
		}
		return res;
	}
	public int getInt(String key, int def) {
		int res = def;
		try {
			 res = Integer.parseInt(MAP.get(key));
		} catch (Exception e) {
		}
		return res;
	}
	
	public final void load() {
		BufferedReader reader = null;
		String line = null;
		try {
			reader = new BufferedReader(new FileReader(new File("./configs/" + name)));
			MAP.clear();
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				if (line.startsWith("#")) {
					continue;
				}
				String[] kv = line.split("=", 2);
				MAP.put(kv[0].trim(), kv[1].trim());
			}
			onLoaded();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
			}
		}
		
		
	}
	protected void onLoaded() {
		
	}
	
}
