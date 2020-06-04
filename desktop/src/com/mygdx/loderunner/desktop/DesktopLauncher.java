package com.mygdx.loderunner.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.loderunner.LodeRunner;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "LodeRunner";
		config.width = 1280;
		config.height = 1020;
		new LwjglApplication(new LodeRunner(), config);
	}
}
