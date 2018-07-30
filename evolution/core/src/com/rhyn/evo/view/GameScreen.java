package com.rhyn.evo.view;

import com.badlogic.gdx.Screen;
import com.rhyn.evo.GameManager;
import com.rhyn.evo.control.Controller;
import com.rhyn.evo.utils.InputManager;

public class GameScreen implements Screen{
	
	private Renderer r;
	private Controller c;

	@Override
	public void show() {
		r = new Renderer();
		c = new Controller();
	}

	@Override
	public void render(float delta) {
		InputManager.update();
		if(c.skip == 0)
			r.render();
		c.update(delta);
	}

	@Override
	public void resize(int width, int height) {
		System.out.println("Size of screen W: " + width + ", H: " + height);
		System.out.println("Size of game W: " + GameManager.width + ", H: " + GameManager.height);
		GameManager.scaleX = (float)width / (float)GameManager.width;
		GameManager.scaleY = (float)height / (float)GameManager.height;
		System.out.println("scaleX: " + GameManager.scaleX + ", scaleY: " + GameManager.scaleY);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		r.dispose();
		c.dispose();
	}
	
}
