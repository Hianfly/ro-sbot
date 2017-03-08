package com.hiandev.rosbot.scanner;

public abstract class Event<T extends Scanner> {

	public abstract void execute();
	
	private T scanner;
	
	public final void setScanner(T scanner) {
		this.scanner = scanner;
	}
	
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
