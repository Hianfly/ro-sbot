package com.hiandev.rosbot.scanner.battle;

public class MotionObject {

	public int _cx0 = 0;
	public int _cy0 = 0;
	public int _cx1 = 0;
	public int _cy1 = 0;
	private boolean dead = false;
	private int crowdCounter = 0;
	public Integer distance = 0;
	
	public MotionObject(int _cx0, int _cy0, int _cx1, int _cy1) {
		this._cx0 = _cx0;
		this._cy0 = _cy0;
		this._cx1 = _cx1;
		this._cy1 = _cy1;
	}
	
	public void setDead() {
		dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public int increaseCrowdCounter() {
		return ++crowdCounter;
	}
	
	public int getCrowdCounter() {
		return crowdCounter;
	}
	
	public int getWidth() {
		return Math.abs(_cx1 - _cx0) + 1;
	}
	
	public int getHeight() {
		return Math.abs(_cy1 - _cy0) + 1;
	}
	
	public int getMiddleCellX() {
		return getWidth()  <= 1 ? _cx0 : _cx0 + (getWidth()  / 2 - 1);
	}
	
	public int getMiddleCellY() {
		return getHeight() <= 1 ? _cy0 : _cy0 + (getHeight() / 2 - 1);
	}

}
