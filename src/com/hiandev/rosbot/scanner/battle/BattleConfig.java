package com.hiandev.rosbot.scanner.battle;

import com.hiandev.rosbot.Config;

public class BattleConfig extends Config {

	public BattleConfig(String name) {
		super (name);
	}

	public static int DARK_PIXEL_REMOVAL_THRESHOLD = 50;
	public static int BACKGROUND_REMOVAL_THRESHOLD = 50;
	public static int AVERAGE_CELL_PIXELS_FLOOR = 20;
	public static int CELL_MOTION_THRESHOLD = 20;
	public static int MOTION_OBJECT_PADDING = 2;
	public static int MIN_CROWD_OBJECT_SIZE = 18;
	public static int MAX_CROWD_OBJECT_SIZE = 9999;

	public static int MAX_ATTACK_DURATION = 20000;
	public static int MIN_ATTACK_DURATION = 500;
	public static int EVENT_DETECTION_INTERVAL = 1000;

	public static int WHEN_ATTACK_DONE_THEN = 1; // 0 do nothing; 1 hard scan surrounding for items
	public static int HARD_SCAN_SURROUNDING_FOR_ITEMS_FLAG = 1;
	public static int MAX_STAY_AT_SAME_LOCATION_DURATION = 120000;
	public static int WHEN_STAY_AT_SAME_LOCATION_REACHED_ITS_LIMIT_THEN = 1;
	public static int MAX_IDLE_DURATION = 2000;
	public static int WHEN_IDLE_REACHED_ITS_LIMIT_THEN = 1; // 1:teleport 2:sit 3:move 4:random teleport move
	public static int WAIT_TIME_AFTER_TELEPORT = 1000;
	
	public static int DO_TELEPORTATION_IF_HP_BELOW_THAN = 50;
	public static int USE_POTION_IF_HP_BELOW_THAN = 80;
	public static int USE_POTION_IF_SP_BELOW_THAN = 80;
	
	protected void onLoaded() {
		
		DARK_PIXEL_REMOVAL_THRESHOLD 		= getInt("dark_pixel_removal_threshold", DARK_PIXEL_REMOVAL_THRESHOLD);
		BACKGROUND_REMOVAL_THRESHOLD 		= getInt("background_removal_threshold", BACKGROUND_REMOVAL_THRESHOLD);
		AVERAGE_CELL_PIXELS_FLOOR 	 		= getInt("average_cell_pixels_floor", AVERAGE_CELL_PIXELS_FLOOR);
		CELL_MOTION_THRESHOLD 		 		= getInt("cell_motion_threshold", CELL_MOTION_THRESHOLD);
		MOTION_OBJECT_PADDING 		 		= getInt("motion_object_padding", MOTION_OBJECT_PADDING);
		MIN_CROWD_OBJECT_SIZE 		 		= getInt("min_crowd_object_size", MIN_CROWD_OBJECT_SIZE);
		MAX_CROWD_OBJECT_SIZE 		 		= getInt("max_crowd_object_size", MAX_CROWD_OBJECT_SIZE);

		MAX_ATTACK_DURATION    		 		= getInt("max_attack_duration", MAX_ATTACK_DURATION);
		MIN_ATTACK_DURATION    		 		= getInt("min_attack_duration", MIN_ATTACK_DURATION);
		EVENT_DETECTION_INTERVAL     		= getInt("event_detection_interval", EVENT_DETECTION_INTERVAL);

		WHEN_ATTACK_DONE_THEN     	 		 = getInt("when_attack_done_then", WHEN_ATTACK_DONE_THEN);
		HARD_SCAN_SURROUNDING_FOR_ITEMS_FLAG = getInt("hard_scan_surrounding_for_items_flag", HARD_SCAN_SURROUNDING_FOR_ITEMS_FLAG);
		MAX_STAY_AT_SAME_LOCATION_DURATION   = getInt("max_stay_at_same_location_duration", MAX_STAY_AT_SAME_LOCATION_DURATION);
		WHEN_STAY_AT_SAME_LOCATION_REACHED_ITS_LIMIT_THEN 
											 = getInt("when_stay_at_same_location_reached_its_limit_then", WHEN_STAY_AT_SAME_LOCATION_REACHED_ITS_LIMIT_THEN);
		MAX_IDLE_DURATION     				 = getInt("max_idle_duration", MAX_IDLE_DURATION);
		WHEN_IDLE_REACHED_ITS_LIMIT_THEN     = getInt("when_idle_reached_its_limit_then", WHEN_IDLE_REACHED_ITS_LIMIT_THEN);
		WAIT_TIME_AFTER_TELEPORT             = getInt("wait_time_after_teleport", WAIT_TIME_AFTER_TELEPORT);
		
		DO_TELEPORTATION_IF_HP_BELOW_THAN    = getInt("do_teleportation_if_hp_below_than", DO_TELEPORTATION_IF_HP_BELOW_THAN);
		USE_POTION_IF_HP_BELOW_THAN    		 = getInt("use_potion_if_hp_below_than", USE_POTION_IF_HP_BELOW_THAN);
		USE_POTION_IF_SP_BELOW_THAN    		 = getInt("use_potion_if_sp_below_than", USE_POTION_IF_SP_BELOW_THAN);
		
	}
	
}
