package com.rhyn.evo.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.rhyn.evo.GameManager;

public abstract class InputManager{
	
	public static float mouseX;
	public static float mouseY;
	
	public static boolean mouseLeft = false;
	public static boolean mouseLeftClicked = false;
	
	public static void update(){
		mouseX = Gdx.input.getX() / GameManager.scaleX;
		mouseY = (GameManager.height * GameManager.scaleY - Gdx.input.getY()) / GameManager.scaleY;
		mouseLeftClicked = false;
		if(Gdx.input.isButtonPressed(Buttons.LEFT)){
			if(!mouseLeft)
				mouseLeftClicked = true;
			mouseLeft = true;
		}
		else
			mouseLeft = false;
	}
	
}
