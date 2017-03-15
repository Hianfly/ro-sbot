package com.hiandev.rosbot;

public class GlobalVar {

	public static final int GAME_STATE_LOGON = 3;
	public static final int GAME_STATE_DISCONNECT = 4;
	public static final int GAME_STATE_BATTLE = 5;
	public static final int GAME_STATE_RELOAD = 6;
	public static final int GAME_STATE_ON_THE_WAY = 7;
	public static final int GAME_STATE_INVENTORY_CHECK = 8;
	public static final int GAME_STATE_CHATTING = 9;
	private static int gameState = GAME_STATE_DISCONNECT;
	public static final int getGameState() {
		return gameState;
	}
	public static final void setGameState(int gameState) {
		GlobalVar.gameState = gameState;
	}
	
}
