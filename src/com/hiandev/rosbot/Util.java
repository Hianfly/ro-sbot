package com.hiandev.rosbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;

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
	
}
