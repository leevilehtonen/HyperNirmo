package com.hypernirmo.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.hypernirmo.game.HyperNirmoGame;
import com.hypernirmo.game.objects.*;
import com.hypernirmo.game.screens.GameScreen;
import com.hypernirmo.game.utils.Box2DManager;

import java.util.Iterator;


public class GameWorld {

    private static final int CLOUD_SPAWN_X_POSITION = 1080;
    private static final int CLOUD_SPAWN_TIME = 250;
    private static final int SPEED_INCREMENT = 10;
    private static final int SPEED_CONTROL_TARGET = 6;
    private static final float CHARACTER_SPEED_INCREMENT = 0.05f;
    private static final int GAME_BEGIN_SPEED_TARGET = 100;
    private HyperNirmoGame mGame;
    private GameScreen mGameScreen;

    private boolean mGameBeginning = true;

    private float mSpeedController = 0;

    private Background mBackground;

    private Array<TextureRegion> mCloudTextures;
    private Array<Sprite> mClouds;
    private int mCloudSpawnTimer = 590;

    private Nirmo mNirmo;

    private Array<Box> mBoxes;
    private TextureRegion mBoxTexture;
    private float mBoxSpawnTimer = 200f;
    private float mTimeToSpawnBox = 15f;
    private int previousBoxCount = 1;

    private Box2DManager mBox2dManager;

    public GameWorld(HyperNirmoGame mGame, GameScreen mGameScreen) {

        //Pass the parameters
        this.mGame = mGame;
        this.mGameScreen = mGameScreen;

        //Create and initialize Box2dManager
        mBox2dManager = new Box2DManager(mGame, this);
        mBox2dManager.createPhyicsWorldAndCollisionListener();
        mBox2dManager.createStaticObjects();

        //Create background
        creteBackground();

        //Create textures and start scene
        createScene();

        //Create character
        createCharacter();

    }

    private void createCharacter() {

        CharacterAnimator mAnimator = new CharacterAnimator(mGame.mAssetsManager);
        mNirmo = new Nirmo(this, mAnimator, 256, 900);
    }

    private void createScene() {

        //Create clouds
        mClouds = new Array<Sprite>();
        mCloudTextures = new Array<TextureRegion>();

        //Get the cloud textures
        for (int i = 1; i <= 4; i++) {
            TextureRegion cloudTextureRegion = new TextureRegion(this.mGame.mAssetsManager.mBackgroundAtlas.findRegion("Cloud" + i));
            cloudTextureRegion.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            mCloudTextures.add(cloudTextureRegion);
        }

        //Spawn initial clouds
        for (int i = 0; i < 4; i++) {
            spawnClouds(MathUtils.random(0, 824));
        }

        //Define box variables
        mBoxes = new Array<Box>();
        mBoxTexture = new TextureRegion(mGame.mAssetsManager.mBackgroundAtlas.findRegion("Box"));
        mBoxTexture.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    }

    private void creteBackground() {


        //Create background system
        mBackground = new Background();

        //Create background layers
        BackgroundItem layerZero = new BackgroundItem(new TextureRegion(mGame.mAssetsManager.mBackgroundAtlas.findRegion("Layer0Brown")), mGame.mGameManager.mGameSpeed, -100);
        BackgroundItem layerOne = new BackgroundItem(new TextureRegion(mGame.mAssetsManager.mBackgroundAtlas.findRegion("Layer1Green")), mGame.mGameManager.mGameSpeed, 292);
        BackgroundItem layerTwo = new BackgroundItem(new TextureRegion(mGame.mAssetsManager.mBackgroundAtlas.findRegion("Layer2Green")), mGame.mGameManager.mGameSpeed - 20f, 352);
        BackgroundItem layerThree = new BackgroundItem(new TextureRegion(mGame.mAssetsManager.mBackgroundAtlas.findRegion("Layer3Green")), mGame.mGameManager.mGameSpeed - 40f, 402);
        BackgroundItem layerFour = new BackgroundItem(new TextureRegion(mGame.mAssetsManager.mBackgroundAtlas.findRegion("Layer4Buildings")), mGame.mGameManager.mGameSpeed - 60f, 542);

        mBackground.addBackgroundLayer(layerFour);
        mBackground.addBackgroundLayer(layerThree);
        mBackground.addBackgroundLayer(layerTwo);
        mBackground.addBackgroundLayer(layerOne);
        mBackground.addBackgroundLayer(layerZero);

    }

    public void update(float delta) {

        //Update score
        if (!mGameBeginning) {
            mGame.mGameManager.mScore = mGame.mGameManager.mScore + 0.01f;
        }

        //Update Box2d manager
        mBox2dManager.update(delta);

        //Update background
        mBackground.update(delta);

        //Update character
        mNirmo.update(delta);

        //Spawn clouds
        mCloudSpawnTimer++;

        if (mCloudSpawnTimer > CLOUD_SPAWN_TIME) {
            spawnClouds(CLOUD_SPAWN_X_POSITION);
            mCloudSpawnTimer = 0;
        }

        //Increase speed
        mSpeedController = mSpeedController + delta;

        if (mSpeedController > SPEED_CONTROL_TARGET) {
            mSpeedController = 0;

            //Increase background speed
            mBackground.increaseSpeed(SPEED_INCREMENT);

            //Increase gamemanager speed
            mGame.mGameManager.mGameSpeed += SPEED_INCREMENT;

            //Increase boxes speed
            for (Box box : mBoxes) {
                box.increaseSpeed(SPEED_INCREMENT);
            }

            //Increase character speed
            mNirmo.increaseSpeed(CHARACTER_SPEED_INCREMENT);

            //Game begins after the speed goes above 100
            if (mGame.mGameManager.mGameSpeed > GAME_BEGIN_SPEED_TARGET) {
                mGameBeginning = false;
            }

            //Update game screen gui speed
            mGameScreen.updateSpeed();
        }

        //Spawn new boxes
        mBoxSpawnTimer = mBoxSpawnTimer + 10 * delta;

        if (mBoxSpawnTimer > mTimeToSpawnBox) {
            boxSpawnLogic(delta);
            mBoxSpawnTimer = 1;
        }

        //Remove off screen clouds
        Iterator<Sprite> iterClouds = mClouds.iterator();
        while (iterClouds.hasNext()) {

            Sprite mItem = iterClouds.next();
            float x = mItem.getX();
            x = x - (mGame.mGameManager.mGameSpeed - 80f) * delta;
            mItem.setPosition(x, mItem.getY());

            if (x <= -mItem.getWidth()) {
                iterClouds.remove();
            }
        }

        //Remove off screen boxes
        Iterator<Box> iterBoxes = mBoxes.iterator();
        while (iterBoxes.hasNext()) {

            Box mBox = iterBoxes.next();
            mBox.update(delta);

            float x = mBox.getSprite().getX();

            if (x <= -mBox.getSprite().getWidth()) {
                mBox.dispose();
                iterBoxes.remove();
            }
        }

    }

    private void boxSpawnLogic(float delta) {

        int boxCount = MathUtils.random(4);

        boxCount = boxCount + previousBoxCount - 2;

        if (boxCount <= 0) {
            boxCount = 1;
        }
        if (boxCount >= 10) {
            boxCount = 5;
        }
        if (previousBoxCount == boxCount) {
            boxCount++;
        }

        int delay = 10;

        for (int i = 0; i < boxCount; i++) {
            spawnBox(mGame.WIDTH + MathUtils.random(30) + delay, 406 + i * 100);
        }
        previousBoxCount = boxCount;

    }


    private void spawnClouds(int cloudSpawnLocation) {

        //Random type and random location
        int y = MathUtils.random(900, 1800);
        int type = MathUtils.random(0, 3);

        //Create a cloud
        Sprite cloud = new Sprite(mCloudTextures.get(type));
        cloud.setPosition(cloudSpawnLocation, y);
        mClouds.add(cloud);
    }

    private void spawnBox(float x, float y) {
        Box box = new Box(this, mBoxTexture, x, y, mGame.mGameManager.mGameSpeed);
        mBoxes.add(box);
    }

    // Gameover function
    public void gameOver() {

        mGame.mAssetsManager.lose.play(1.0f, 1.0f, 0);
        mGameScreen.gameOver();

    }

    // Getters and setters
    public Nirmo getNirmo() {
        return mNirmo;
    }

    public Background getBackground() {
        return mBackground;
    }

    public Array<Sprite> getClouds() {
        return mClouds;
    }

    public Array<Box> getBoxes() {
        return mBoxes;
    }

    public Box2DManager getBox2dManager() {
        return mBox2dManager;
    }

    public boolean isGameBeginning() {
        return mGameBeginning;
    }

    public void setGameBeginning(boolean mGameBeginning) {
        this.mGameBeginning = mGameBeginning;
    }

    // Dispose everything
    public void dispose() {
        mBackground.dispose();
        mNirmo.dispose();
        for (Box box : mBoxes) {
            box.dispose();
        }
        mBoxes.clear();
        mBox2dManager.dispose();
        mClouds.clear();
        mCloudTextures.clear();

    }
}
