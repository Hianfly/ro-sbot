package com.hiandev.rosbot;

public abstract class Service {

	public Service() {

	}
	
    /*
     * 
     * 
     * 
     */
	protected boolean onStart() {
		return true;
	}
    protected void onPreExecute() {
		
	}
	protected void onExecute() {
		
	}
	protected void onPostExecute() {
		
	}
	protected void onFinish() {
		
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	private boolean started = false;
	public void start() {
		if (started) {
			
		}
		else {
			started = true;
			thread = getThread();
			thread.start();
		}
	}
	public void stop() {
		running = false;
	}
	protected boolean isStarted() {
		return started;
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	private long delay = 0;
	private long interval = 1000;
    private boolean running = false;
	private Thread thread = null;
	private Thread getThread() {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				running = onStart();
				sleep(delay);
				while (running) {
					onPreExecute();
					onExecute();
					onPostExecute();
				    sleep(interval);
				}
				onFinish();
			}
		});
	}
	protected boolean isRunning() {
		return running;
	}
	protected void setDelay(long delay) {
		this.delay = delay;
	}
	protected long getDelay() {
		return delay;
	}
	protected void setInterval(long interval) {
		this.interval = interval;
	}
	protected long getInterval() {
		return interval;
	}
	
	/*
	 * 
	 * 
	 * 
	 */
    protected void sleep(long time) {
    	try {
    		if (time > 0) {
    			Thread.sleep(time);
    		}
	    } catch (InterruptedException ie) {
	    }
    }
    
}
