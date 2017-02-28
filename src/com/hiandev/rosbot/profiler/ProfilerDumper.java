package com.hiandev.rosbot.profiler;

import java.util.ArrayList;
import com.hiandev.rosbot.Service;

public class ProfilerDumper extends Service {

	public ProfilerDumper() {
		setInterval(1000 * 60 * 5);
		setDelay(getInterval());
	}

	private final ArrayList<Profiler> list = new ArrayList<>();
	public void addProfiler(Profiler profiler) {
		list.add(profiler);
	}
	
	@Override
	protected void onExecute() {
		super.onExecute();
		for (Profiler p : list) {
			p.dump();
		}
	}
	
}
