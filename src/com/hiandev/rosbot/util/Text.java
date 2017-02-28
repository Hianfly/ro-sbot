package com.hiandev.rosbot.util;

public class Text {

	public static final String lpad(int i, int padLength, char padChar) {
		StringBuilder sb = new StringBuilder();
		sb.append(i);
		while (sb.length() < padLength) {
			sb.insert(0, padChar);
		}
		return sb.toString();
	}
	
}
