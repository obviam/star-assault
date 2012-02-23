package net.obviam.starassault;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class StarAssaultDesktop {

	public static void main(String[] args) {
		new LwjglApplication(new StarAssault(), "Star Assault", 480, 320, true);
	}

}
