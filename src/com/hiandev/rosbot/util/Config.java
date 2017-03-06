package com.hiandev.rosbot.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hiandev.rosbot.scanner.Cell;

public class Config {

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
