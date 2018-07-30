package com.rhyn.evo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rhyn.evo.GameManager;

public class DesktopLauncher implements ApplicationConstants{
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = WIDTH;
		config.height = HEIGHT;
		config.fullscreen = FULLSCREEN;
		config.title = TITLE;
		new LwjglApplication(new GameManager(config.width, config.height), config);
	}
}
