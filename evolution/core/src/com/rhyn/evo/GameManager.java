package com.rhyn.evo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.rhyn.evo.view.GameScreen;

public class GameManager extends Game{
	
	public static int width, height;
	public static float scaleX, scaleY;

	private Screen gameScreen;	
	
	public GameManager(int width, int height){
		GameManager.width = width;
		GameManager.height = height;
	}
	
	public void create() {
		gameScreen = new GameScreen();
		setScreen(gameScreen);
	}

}
