package com.hypernirmo.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.hypernirmo.game.HyperNirmoGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Hyper Nirmo";
		config.x = -1;
		config.y = -1;
		config.width = 576;
		config.height = 1024;
		config.samples = 10;
		config.vSyncEnabled = true;
		config.resizable = true;
		new LwjglApplication(new HyperNirmoGame(), config);
	}
}
