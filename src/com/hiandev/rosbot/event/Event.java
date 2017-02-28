package com.hiandev.rosbot.event;

import com.hiandev.rosbot.scanner.Scanner;

public abstract class Event<T extends Scanner> {

	public Event(T scanner) {
		this.scanner = scanner;
	}
	
	public abstract void execute();
	
	private final T scanner;
	public final T getScanner() {
		return scanner;
	}

    protected void sleep(long time) {
    	try {
	    	Thread.sleep(time);
	    } catch (Exception e) {
	    }
    }
	
}
