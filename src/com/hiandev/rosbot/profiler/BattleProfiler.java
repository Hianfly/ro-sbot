package com.hiandev.rosbot.profiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.hiandev.rosbot.scanner.Cell;
import com.hiandev.rosbot.util.Text;

public class BattleProfiler extends Profiler {
	
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
	
	private final ConcurrentHashMap<Long, int[]> map = new ConcurrentHashMap<>();
	public void add(Cell cell, int profile) {
		long key = Cell.createAveragePixelsKey(cell.averagePixels);
		int[] profiler = map.get(key);
		if (profiler == null) {
			map.put(key, profiler = createProfiler());
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
		int[] profiler = map.get(key);
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
		Set<Long> keys = map.keySet();
		for (Long key : keys) {
			int[] profiler = map.get(key);
			if (profiler[profile] > 0) {
				r.add(profiler);
			}
		}
		return r;
	}
	
	/*
	 * 
	 * 
	 * 
	 */
	@Override
	public void load() {
		BufferedReader reader = null;
		try {
			File file = new File("./battle-profiler.dmp");
			if (file.exists()) {
				reader = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] elements = line.split(",");
					long  key = Long.parseLong(elements[0]);
					int[] val = createProfiler();
					for (int x = 0; x < val.length; x++) {
						val[x] = Integer.parseInt(elements[x + 1].trim());
					}
					map.put(key, val);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ioe) {
			}
		}
	}
	@Override
	public void dump() {
		BufferedWriter writer = null;
		try {
			String filename = "battle-profiler.dmp";
			File file = new File(filename);
			if (file.exists()) {
				file.renameTo(new File(filename.replace(".dmp", "-" + System.currentTimeMillis() + ".dmp")));
			}
			writer = new BufferedWriter(new FileWriter(new File(filename)));
			Set<Long> keys = map.keySet();
			for (Long key : keys) {
				int[] val = map.get(key);
				StringBuilder sb = new StringBuilder();
				sb.append(key).append(",");
				for (int v : val) {
					sb.append(Text.lpad(v, 3, ' '));
				}
				sb.deleteCharAt(sb.length() - 1);
				writer.write(sb.toString());
				writer.newLine();
				writer.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException ioe) {
			}
		}
	}
	
}
