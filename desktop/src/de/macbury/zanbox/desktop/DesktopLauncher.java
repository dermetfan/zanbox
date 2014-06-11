package de.macbury.zanbox.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.macbury.zanbox.Zanbox;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.width  = 1280;
    config.height = 768;
    config.samples = 0;
		new LwjglApplication(new Zanbox(new DebugDesktop()), config);
	}
}
