package com.hiandev.rosbot.scanner.battle;

public class MotionBound {

	public int _bcx = 0;
	public int _bcy = 0;
	public int _ecy = 0;
	public int _ecx = 0;
	private long time = 0;
	public boolean dead = false;
	
	public MotionBound(int _bcx, int _bcy, int _ecx, int _ecy, long time) {
		this._bcx = _bcx;
		this._bcy = _bcy;
		this._ecx = _ecx;
		this._ecy = _ecy;
		this.time = time;
		this.dead = false;
	}
	
	public void update(long now) {
		this.time = now;
	}
	
	public boolean isTimeout(long now) {
		return now - time > 3000;
	}
	
}
