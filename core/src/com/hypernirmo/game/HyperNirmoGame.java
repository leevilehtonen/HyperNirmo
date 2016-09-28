package com.hypernirmo.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hypernirmo.game.screens.MenuScreen;
import com.hypernirmo.game.utils.AssetsManager;
import com.hypernirmo.game.utils.GameManager;

public class HyperNirmoGame extends Game {

	public final float WIDTH = 1080;
	public final float HEIGHT = 1920;

	public AssetsManager mAssetsManager;
	public GameManager mGameManager;

	private MenuScreen mMenuScreen;

	
	@Override
	public void create () {
		// Game created log message
		Gdx.app.log("Framework", "Hyper Nirmo app created");

		// Create utils managers
		mAssetsManager = new AssetsManager();
		mGameManager = new GameManager();

		// Observe back key
		Gdx.input.setCatchBackKey(true);

		// Create ___ screen
		mMenuScreen = new MenuScreen(this);

		// Set the screen active
		this.setScreen(mMenuScreen);
	}

	@Override
	public void render () {

		// Pass rendering back to super class
		super.render();
	}
	
	@Override
	public void dispose () {

		// Pass disposing back to super class
		super.dispose();

		// Dispose assets
		mAssetsManager.dispose();

		// Dispose game data
		mGameManager.dispose();


	}
}
