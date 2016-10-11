package com.hypernirmo.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hypernirmo.game.HyperNirmoGame;
import com.hypernirmo.game.objects.Box;
import com.hypernirmo.game.screens.GameScreen;

public class GameRenderer {

    private HyperNirmoGame mGame;
    private GameScreen mGameScreen;
    private GameWorld mGameWorld;

    private SpriteBatch mSpriteBatch;
    private ShapeRenderer mShapeRenderer;

    public GameRenderer(HyperNirmoGame mGame, GameScreen mGameScreen, GameWorld mGameWorld) {

        //Initialize variables
        this.mGame = mGame;
        this.mGameScreen = mGameScreen;
        this.mGameWorld = mGameWorld;

        //Create spritebatch
        mSpriteBatch = new SpriteBatch();
        mSpriteBatch.setProjectionMatrix(mGameScreen.getCamera().combined);

        //Crete shaperenderer
        mShapeRenderer = new ShapeRenderer();
        mShapeRenderer.setProjectionMatrix(mGameScreen.getCamera().combined);


    }

    public void render(float delta) {

        //Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        //Enable blend
        Gdx.gl.glEnable(GL20.GL_BLEND);
        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //Render background
        mShapeRenderer.setColor(221.0f / 255.0f, 250.0f / 255.0f, 255.0f / 255.0f, 1f);
        mShapeRenderer.rect(0, 0, mGame.WIDTH, mGame.HEIGHT);
        mShapeRenderer.end();

        //Render background layers
        mSpriteBatch.begin();
        for (Sprite backgroundLayer : mGameWorld.getBackground().getSpritesToRender()) {
            backgroundLayer.draw(mSpriteBatch);
        }

        //Render clouds
        for (Sprite cloud : mGameWorld.getClouds()) {
            cloud.draw(mSpriteBatch);
        }

        //Render character
        mGameWorld.getNirmo().getSprite().draw(mSpriteBatch);

        //Render boxes
        for (Box box : mGameWorld.getBoxes()) {
            box.getSprite().draw(mSpriteBatch);
        }

        mSpriteBatch.end();

        //Enable blend
        Gdx.gl.glEnable(GL20.GL_BLEND);

        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //Info box
        if (mGameWorld.isGameBeginning()) {
            mShapeRenderer.setColor(0, 0, 0, 0.8f);
            mShapeRenderer.rect(mGame.WIDTH / 2 - 440, mGame.HEIGHT / 2, 880,
                    400);
        }

        //Gui box
        if (!mGame.mGameManager.mGameOver && !mGame.mGameManager.mGamePaused) {
            mShapeRenderer.setColor(0, 0, 0, 0.6f);
            mShapeRenderer.rect(0, mGame.HEIGHT - 122, mGame.WIDTH,
                    mGame.HEIGHT);
        }

        //Paused darken
        if (mGame.mGameManager.mGamePaused) {
            mShapeRenderer.setColor(0, 0, 0, 0.6f);
            mShapeRenderer.rect(0, 0, mGame.WIDTH, mGame.HEIGHT);
        }

        //Gameover darken
        if (mGame.mGameManager.mGameOver) {
            mShapeRenderer.setColor(0, 0, 0, 0.90f);
            mShapeRenderer.rect(0, 0, mGame.WIDTH, mGame.HEIGHT);
        }
        mShapeRenderer.end();

        //Info box text
        if (mGameWorld.isGameBeginning()) {
            mSpriteBatch.begin();
            mGame.mAssetsManager.mSmallFont.draw(mSpriteBatch,
                    "TAP TO JUMP, THE LONGER YOU", mGame.WIDTH / 2 - 320,
                    mGame.HEIGHT / 2 + 350);
            mGame.mAssetsManager.mSmallFont.draw(mSpriteBatch,
                    "HOLD, THE HIGHER NIRMO WILL JUMP", mGame.WIDTH / 2 - 380,
                    mGame.HEIGHT / 2 + 300);

            mGame.mAssetsManager.mSmallFont.draw(mSpriteBatch, "GOOD LUCK!",
                    mGame.WIDTH / 2 - 130, mGame.HEIGHT / 2 + 150);

            mSpriteBatch.end();
        }

    }

    public void dispose() {
    }
}
