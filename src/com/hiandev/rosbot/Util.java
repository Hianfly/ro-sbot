package com.hiandev.rosbot;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONObject;
import java.awt.Toolkit;

public class Util {
	
	public static final String lpad(int i, int padLength, char padChar) {
		StringBuilder sb = new StringBuilder();
		sb.append(i);
		while (sb.length() < padLength) {
			sb.insert(0, padChar);
		}
		return sb.toString();
	}
	
	public static final JSONObject loadJSON(String filename) {
		JSONObject json = null;
		BufferedReader reader = null;
    	try {
    		StringBuilder sb = new StringBuilder();
    		reader = new BufferedReader(new FileReader(new File(filename)));
    		String line = null;
    		while ((line = reader.readLine()) != null) {
    			sb.append(line);
    		}
    		json = new JSONObject(sb.toString());
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			if (reader != null) {
    				reader.close();
    			}
    		} catch (IOException ioe) {
    		}
    	}
    	return json;
	}
	
	public static final void copyToClipboard(String characters) {
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(characters), null);
	}
	
	public static final boolean isMacAddressEquals(String macAddress) {
		boolean b = false;
		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			System.out.print("Current MAC address : ");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			String m = sb.toString();
			b = m.equals(macAddress);
			System.out.println(sb.toString() +  " " + sb.toString().equals(macAddress));
		} catch (UnknownHostException e) {
		} catch (SocketException e) {
		}
		return b;
	}
	
}
