package net.obviam.starassault;

import net.obviam.starassault.screens.GameScreen;

import com.badlogic.gdx.Game;

public class StarAssault extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen());
	}

}
