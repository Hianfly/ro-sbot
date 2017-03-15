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
	public boolean isStarted() {
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
	public boolean isRunning() {
		return running;
	}
	public void setDelay(long delay) {
		this.delay = delay;
	}
	public long getDelay() {
		return delay;
	}
	public void setInterval(long interval) {
		this.interval = interval;
	}
	public long getInterval() {
		return interval;
	}
	public void startForeground(int executionTimes) {
		try {
			running = onStart();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sleep(delay);
		if (running && (executionTimes < 0 || executionTimes > 0)) {
			try {
				onPreExecute();
				onExecute();
				onPostExecute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		    executionTimes--;
		    sleep(interval);
		}
		try {
			onFinish();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
