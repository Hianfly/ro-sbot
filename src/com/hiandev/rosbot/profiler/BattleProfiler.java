package com.hiandev.rosbot.profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.hiandev.rosbot.scanner.Cell;

public class BattleProfiler {
	
	public BattleProfiler() {

	}

	public static final int PROFILE_NOT_CLICKABLE = 0;
	public static final int PROFILE_GROUND = 1;
	public static final int PROFILE_PICKABLE = 2;
	public static final int PROFILE_PORTAL = 3;
	public static final int PROFILE_NPC = 4;
	public static final int PROFILE_TARGETABLE = 5;	
	private int[] createProfiler() {
		return new int[] {
			0, //
			0, // 
			0, // 
			0, // 
			0, // 
			0, // 
		};
	}
	
	private final ConcurrentHashMap<Long, int[]> MAP = new ConcurrentHashMap<>();
	public void add(Cell cell, int profile) {
		long key = Cell.createAveragePixelsKey(cell.averagePixels);
		int[] profiler = MAP.get(key);
		if (profiler == null) {
			MAP.put(key, profiler = createProfiler());
		}
		if (profile == PROFILE_NOT_CLICKABLE) {
			profiler[profile] += 1;
		}
		else {
			profiler[profile] += 1;
			profiler[PROFILE_NOT_CLICKABLE] -= (profiler[PROFILE_NOT_CLICKABLE] == 0 ? 0 : 1);
		}
	}
	public int get(Cell cell, int profile) {
		long key = Cell.createAveragePixelsKey(cell.averagePixels);
		int[] profiler = MAP.get(key);
		if (profiler == null) {
			return -1;
		}
		else {
			return profiler[profile];
		}
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	
	public List<int[]> getList(int profile) {
		List<int[]> r = new ArrayList<>();
		Set<Long> keys = MAP.keySet();
		for (Long key : keys) {
			int[] profiler = MAP.get(key);
			if (profiler[profile] > 0) {
				r.add(profiler);
			}
		}
		return r;
	}
	
}
