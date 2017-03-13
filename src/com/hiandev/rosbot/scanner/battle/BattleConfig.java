package com.hiandev.rosbot.scanner.battle;

import com.hiandev.rosbot.Config;

public class BattleConfig extends Config {

	public BattleConfig() {
		super ("battle-config.txt");
	}
	
	public static int DARK_PIXEL_REMOVAL_THRESHOLD = 0;
	public static int BACKGROUND_REMOVAL_THRESHOLD = 0;
	public static int AVERAGE_CELL_PIXELS_FLOOR = 0;
	public static int CELL_MOTION_THRESHOLD = 0;
	public static int MOTION_OBJECT_PADDING = 0;
	public static int MIN_CROWD_OBJECT_SIZE = 0;
	public static int MAX_CROWD_OBJECT_SIZE = 0;


	public static int MAX_ATTACK_DURATION = 0;
	public static int MIN_ATTACK_DURATION = 0;
	public static int EVENT_DETECTION_INTERVAL = 0;

	public static int WHEN_ATTACK_DONE_THEN = 0; // 0 do nothing; 1 hard scan surrounding for items
	public static int HARD_SCAN_SURROUNDING_FOR_ITEMS_FLAG = 0;
	public static int MAX_STAY_AT_SAME_LOCATION_DURATION = 0;
	public static int WHEN_STAY_AT_SAME_LOCATION_REACHED_ITS_LIMIT_THEN = 0;
	public static int MAX_IDLE_DURATION = 0;
	public static int WHEN_IDLE_REACHED_ITS_LIMIT_THEN = 0; // 1:teleport 2:sit 3:move 4:random teleport move
	
	protected void onLoaded() {
		
		DARK_PIXEL_REMOVAL_THRESHOLD 		= getInt("dark_pixel_removal_threshold", 50);
		BACKGROUND_REMOVAL_THRESHOLD 		= getInt("background_removal_threshold", 50);
		AVERAGE_CELL_PIXELS_FLOOR 	 		= getInt("average_cell_pixels_floor", 20);
		CELL_MOTION_THRESHOLD 		 		= getInt("cell_motion_threshold", 20);
		MOTION_OBJECT_PADDING 		 		= getInt("motion_object_padding", 2);
		MIN_CROWD_OBJECT_SIZE 		 		= getInt("min_crowd_object_size", 18);
		MAX_CROWD_OBJECT_SIZE 		 		= getInt("max_crowd_object_size", 9999);

		MAX_ATTACK_DURATION    		 		= getInt("max_attack_duration", 20000);
		MIN_ATTACK_DURATION    		 		= getInt("min_attack_duration", 500);
		EVENT_DETECTION_INTERVAL     		= getInt("event_detection_interval", 1000);

		WHEN_ATTACK_DONE_THEN     	 		 = getInt("when_attack_done_then", 1);
		HARD_SCAN_SURROUNDING_FOR_ITEMS_FLAG = getInt("hard_scan_surrounding_for_items_flag", 1);
		MAX_STAY_AT_SAME_LOCATION_DURATION   = getInt("max_stay_at_same_location_duration", 120000);
		WHEN_STAY_AT_SAME_LOCATION_REACHED_ITS_LIMIT_THEN 
											 = getInt("when_stay_at_same_location_reached_its_limit_then", 1);
		MAX_IDLE_DURATION     				 = getInt("max_idle_duration", 2000);
		WHEN_IDLE_REACHED_ITS_LIMIT_THEN     = getInt("when_idle_reached_its_limit_then", 1);
		
	}
	
}
