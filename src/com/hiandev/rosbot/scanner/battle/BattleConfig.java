package com.hiandev.rosbot.scanner.battle;

public class BattleConfig {

	public static final int DARK_PIXEL_REMOVAL_THRESHOLD = 50;
	public static final int BACKGROUND_REMOVAL_THRESHOLD = 50;
	public static final int CELL_SUMMARY_THRESHOLD = 20;
	public static final int CELL_MOTION_THRESHOLD = 20;
	public static final int MOTION_OBJECT_PADDING = 2;
	public static final int CREATE_CROWD_THRESHOLD = 18;


	public static final int EVENT_MAX_ATTACK_DURATION = 20000;
	public static final int EVENT_MIN_ATTACK_DURATION = 500;
	public static final int EVENT_DETECTION_INTERVAL = 1000;

	public static int WHEN_ATTACKING_DONE_THEN = 1; // 0 do nothing; 1 hard scan surrounding for items
	public static int HARD_SCAN_SURROUNDING_FOR_ITEMS_FLAG = 1;
	public static int STAY_AT_SAME_LOCATION_LIMIT = 1000 * 60 * 2;
	public static int WHEN_STAY_AT_SAME_LOCATION_REACHED_ITS_LIMIT_THEN = 1;
	public static int IDLE_LIMIT = 2000;
	public static int WHEN_IDLE_REACHED_ITS_LIMIT_THEN = 1; // 1:teleport 2:sit 3:move 4:random teleport move
	
}
