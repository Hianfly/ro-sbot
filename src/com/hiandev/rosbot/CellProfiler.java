package com.hiandev.rosbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CellProfiler {

	public static final int PROFILE_OBJECT_ID = 0;
	public static final int PROFILE_GROUND = 1;
	public static final int PROFILE_ITEM = 2;
	public static final int PROFILE_PORTAL = 3;
	public static final int PROFILE_NPC = 4;
	public static final int PROFILE_ATTACKABLE = 5;
	public static final int PROFILE_NOT_CLICKABLE = 6;
	
	private Scanner scanner = null;
	public CellProfiler(Scanner scanner) {
		this.scanner = scanner;
	}
	
	private final ConcurrentHashMap<String, int[]> MAP = new ConcurrentHashMap<>();
	
	public int removeNotClickable(ArrayList<int[]> cellDiff) {
		int r = 0;
		for (int x = 0; x < cellDiff.size(); x++) {
			int[] cell = cellDiff.get(x);
			if (get(cell[4], cell[5], PROFILE_NOT_CLICKABLE) > 0) {
				cellDiff.remove(x--);
				r++;
			}
		}
		return r;
	}
	
	public void add(int cellX, int cellY, int profile) {
		String cellConv = scanner.getCellConv(cellX, cellY);
		int[] profiler = MAP.get(cellConv);
		if (profiler == null) {
			MAP.put(cellConv, profiler = createProfiler());
		}
		profiler[profile] += 1;
	}
	
	public int get(int cellX, int cellY, int profile) {
		String cellConv = scanner.getCellConv(cellX, cellY);
		int[] profiler = MAP.get(cellConv);
		if (profiler == null) {
			return -1;
		}
		else {
			return profiler[profile];
		}
	}
	
	public ArrayList<int[]> getList(int profile) {
		ArrayList<int[]> r = new ArrayList<>();
		Set<String> keys = MAP.keySet();
		for (String key : keys) {
			int[] profiler = MAP.get(key);
			if (profiler[profile] > 0) {
				r.add(profiler);
			}
		}
		return r;
	}
	
	public String toSummaryString(int profile) {
		StringBuilder sb = new StringBuilder();
		HashMap<Integer, Integer> summary = new HashMap<>();
		Set<String> keys = MAP.keySet();
		for (String key : keys) {
			int[] profiler = MAP.get(key);
			if (profiler[profile] > 0) {
				Integer val = summary.get(profiler[profile]);
				if (val == null) {
					summary.put(profiler[profile], 1);
				}
				else {
					summary.put(profiler[profile], val.intValue() + 1);
				}
			}
		}
		Set<Integer> sums = summary.keySet();
		for (Integer key : sums) {
			sb.append(key).append(" > ").append(summary.get(key));
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Set<String> keys = MAP.keySet();
		for (String key : keys) {
			sb.append(key).append(" > ").append(toString(MAP.get(key)));
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public String toString(int profile) {
		StringBuilder sb = new StringBuilder();
		Set<String> keys = MAP.keySet();
		for (String key : keys) {
			int[] profiler = MAP.get(key);
			if (profiler[profile] > 0) {
				sb.append(key).append(" > ").append(toString(profiler));
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	public String toString(String cellConv) {
		return toString(MAP.get(cellConv));
	}
	
	public String toString(int[] profiler) {
		StringBuilder sb = new StringBuilder();
		if (profiler != null) {
			for (int p : profiler) {
				sb.append(p).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	private int[] createProfiler() {
		return new int[] {
			0, // object id
			0, // ground
			0, // item
			0, // portal
			0, // npc
			0, // attackable
			0, // unclickable
		};
	}
	
	
}
