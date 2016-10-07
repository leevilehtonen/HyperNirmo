package com.hypernirmo.game.screens;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hypernirmo.game.HyperNirmoGame;
import com.hypernirmo.game.objects.Background;
import com.hypernirmo.game.objects.BackgroundItem;

public class AboutScreen implements Screen {

    private HyperNirmoGame mGame;
    private OrthographicCamera mAboutCam;
    private FitViewport mViewport;

    private Stage mStage;
    private Table mTable;
    private Skin mSkin;

    private ShapeRenderer mShapeRenderer;
    private SpriteBatch mSpriteBatch;

    private Background mBackground;

    private Array<TextureRegion> mCloudTextures;
    private Array<Sprite> mClouds;
    private int mCloudSpawnTimer;

    private static final int CLOUD_SPAWN_LOCATION = 1080;
    private static final int CLOUD_SPAWN_POINT = 1000;
    private static final float CLOUD_SPEED_MODIFIER = 10f;

    public AboutScreen(HyperNirmoGame hyperNirmoGame) {

        //Initialize variables
        mGame = hyperNirmoGame;

        //Create camera
        mAboutCam = new OrthographicCamera();
        mAboutCam.setToOrtho(false, mGame.WIDTH, mGame.HEIGHT);
        mViewport = new FitViewport(mGame.WIDTH, mGame.HEIGHT, mAboutCam);

        //Load assets
        mGame.mAssetsManager.loadBackground();
        mGame.mAssetsManager.loadFonts();
        mGame.mAssetsManager.loadSounds();
        mGame.mAssetsManager.loadUIElements();

        //Create stage
        mStage = new Stage(mViewport);
        Gdx.input.setInputProcessor(mStage);

        //Get the skin from asstesmanager
        mSkin = mGame.mAssetsManager.mSkin;

        //Create renderers
        mSpriteBatch = new SpriteBatch();
        mSpriteBatch.setProjectionMatrix(mAboutCam.combined);
        mShapeRenderer = new ShapeRenderer();
        mShapeRenderer.setProjectionMatrix(mAboutCam.combined);

        // Create background
        createBackground();

        // Create start scene and clouds
        createScene();

        // Create UI
        createUI();

    }

    private void createUI() {

        // Create table
        mTable = new Table();
        mTable.setPosition(mGame.WIDTH / 2, mGame.HEIGHT / 2);
        mStage.addActor(mTable);

        //Create title
        Image titleImg = new Image(mSkin.getDrawable("Title"));
        mTable.add(titleImg).size(1080, 256).pad(100).padBottom(500);
        mTable.row();

        //Create buttonstyle
        ButtonStyle menuButtonStyle = new ButtonStyle();
        menuButtonStyle.up = mSkin.getDrawable("MenuBtn");

        //Create button
        Button menuBtn = new Button(menuButtonStyle);
        mTable.add(menuBtn).padBottom(400).padTop(400);
        mTable.row();

        menuBtn.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                //Back to menu
                playClickSound();
                fadeTo("menu");
            }
        });

    }

    @Override
    public void render(float delta) {

        if (mGame.mAssetsManager.mAssetManager.update()) {

            //Clear screen
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            //Render background
            mShapeRenderer.begin(ShapeType.Filled);
            mShapeRenderer.setColor(221.0f / 255.0f, 250.0f / 255.0f, 255.0f / 255.0f, 1f);
            mShapeRenderer.rect(0, 0, mGame.WIDTH, mGame.HEIGHT);
            mShapeRenderer.end();

            //Render background layers
            mSpriteBatch.begin();
            for (Sprite backgroundLayer : mBackground.getSpritesToRender()) {
                backgroundLayer.draw(mSpriteBatch);
            }

            //Render clouds
            for (Sprite cloud : mClouds) {
                cloud.draw(mSpriteBatch);
            }
            mSpriteBatch.end();

            //Render text background
            Gdx.gl.glEnable(GL20.GL_BLEND);
            mShapeRenderer.begin(ShapeType.Filled);
            mShapeRenderer.setColor(0, 0, 0, 0.8f);
            mShapeRenderer.rect(mGame.WIDTH / 2 - 500, mGame.HEIGHT / 2 - 280,
                    1000, 800);
            mShapeRenderer.end();

            //Render texts
            renderTexts(delta);

            //Update background
            mBackground.update(delta);

            mCloudSpawnTimer++;

            //Spawn new cloud
            if (mCloudSpawnTimer > CLOUD_SPAWN_POINT) {
                spawnClouds(CLOUD_SPAWN_LOCATION);
                mCloudSpawnTimer = 0;
            }

            //Update clouds (also remove)
            Iterator<Sprite> iterClouds = mClouds.iterator();
            while (iterClouds.hasNext()) {

                Sprite mItem = iterClouds.next();
                mItem.setPosition(mItem.getX() - CLOUD_SPEED_MODIFIER * delta, mItem.getY());

                if (mItem.getX() <= -mItem.getWidth()) {
                    iterClouds.remove();
                }
            }

            // Update and draw the stage
            mStage.act();
            mStage.draw();

        }

        // Check for back button
        if (Gdx.input.isKeyJustPressed(Keys.BACK)) {
            playClickSound();
            fadeTo("menu");
        }
    }

    private void renderTexts(float delta) {

        // Begin text rendering
        mSpriteBatch.begin();

        // First paragraph
        mGame.mAssetsManager.mSmallFont.draw(mSpriteBatch,
                "Hyper Nirmo is a 2D physics-based jumping ",
                mGame.WIDTH / 2 - 480, mGame.HEIGHT / 2 + 510);
        mGame.mAssetsManager.mSmallFont.draw(mSpriteBatch,
                "game. Your goal is to jump from box to box.",
                mGame.WIDTH / 2 - 480, mGame.HEIGHT / 2 + 460);
        mGame.mAssetsManager.mSmallFont.draw(mSpriteBatch,
                "Show your skills and beat the record! Try to get",
                mGame.WIDTH / 2 - 480, mGame.HEIGHT / 2 + 410);
        mGame.mAssetsManager.mSmallFont.draw(mSpriteBatch,
                "as far as possible without hitting the spikes. It",
                mGame.WIDTH / 2 - 480, mGame.HEIGHT / 2 + 360);
        mGame.mAssetsManager.mSmallFont.draw(mSpriteBatch,
                "may feel easy but be aware, the speed is",
                mGame.WIDTH / 2 - 480, mGame.HEIGHT / 2 + 310);
        mGame.mAssetsManager.mSmallFont.draw(mSpriteBatch,
                "increasing, which makes it a lot more difficult.",
                mGame.WIDTH / 2 - 480, mGame.HEIGHT / 2 + 260);
        mGame.mAssetsManager.mSmallFont.draw(mSpriteBatch,
                "Also be aware the game is really addictive.",
                mGame.WIDTH / 2 - 480, mGame.HEIGHT / 2 + 210);

        // Good luck
        mGame.mAssetsManager.mLargeFont.draw(mSpriteBatch, "Good luck!",
                mGame.WIDTH / 2 - 120, mGame.HEIGHT / 2 + 90);

        // Libgdx powered
        mGame.mAssetsManager.mAboutFont.draw(mSpriteBatch,
                "Game is powered by libGDX game engine", mGame.WIDTH / 2 - 480,
                mGame.HEIGHT / 2 + -90);


        // End text rendering
        mSpriteBatch.end();
    }

    private void createBackground() {

        //Create background system
        mBackground = new Background();

        //Create background layers
        BackgroundItem layerZero = new BackgroundItem(new TextureRegion(this.mGame.mAssetsManager.mBackgroundAtlas.findRegion("Layer0Brown")), 180f, -100);
        BackgroundItem layerOne = new BackgroundItem(new TextureRegion(this.mGame.mAssetsManager.mBackgroundAtlas.findRegion("Layer1Green")), 180f, 292);
        BackgroundItem layerTwo = new BackgroundItem(new TextureRegion(this.mGame.mAssetsManager.mBackgroundAtlas.findRegion("Layer2Green")), 120f, 352);
        BackgroundItem layerThree = new BackgroundItem(new TextureRegion(this.mGame.mAssetsManager.mBackgroundAtlas.findRegion("Layer3Green")), 70f, 402);
        BackgroundItem layerFour = new BackgroundItem(new TextureRegion(this.mGame.mAssetsManager.mBackgroundAtlas.findRegion("Layer4Buildings")), 40f, 542);

        //Add the layers to the background system
        mBackground.addBackgroundLayer(layerFour);
        mBackground.addBackgroundLayer(layerThree);
        mBackground.addBackgroundLayer(layerTwo);
        mBackground.addBackgroundLayer(layerOne);
        mBackground.addBackgroundLayer(layerZero);
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

    }

    private void spawnClouds(int cloudSpawnLocation) {

        // Spawn clouds (random height and random type)
        int y = MathUtils.random(900, 1800);
        int type = MathUtils.random(0, 3);

        // Create new sprite for cloud
        Sprite cloud = new Sprite(mCloudTextures.get(type));
        cloud.setPosition(cloudSpawnLocation, y);
        mClouds.add(cloud);

    }

    @Override
    public void resize(int width, int height) {

        mViewport.update(width, height, true);

    }

    private void fadeTo(final String newScreen) {

        //Create black texture
        Pixmap black = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        black.setColor(0, 0, 0, 1);
        black.fill();
        Texture blackTexture = new Texture(black);

        //Black image actor
        Image mFadeImage = new Image(blackTexture);
        mFadeImage.setSize(mGame.WIDTH, mGame.HEIGHT);
        mStage.addActor(mFadeImage);

        //Alpha out
        AlphaAction initialAlphaOut = new AlphaAction();
        initialAlphaOut.setAlpha(0.0f);
        initialAlphaOut.setDuration(0.001f);

        //Alpha in
        AlphaAction alphaIn = new AlphaAction();
        alphaIn.setAlpha(1);
        alphaIn.setDuration(0.5f);
        alphaIn.setInterpolation(Interpolation.fade);

        // Start changing back to the menu
        Action loader = new Action() {
            @Override
            public boolean act(float delta) {

                if (newScreen.equals("menu")) {
                    // Dispose about screen
                    dispose();
                    // Set menu screen back
                    mGame.setScreen(new MenuScreen(mGame));
                }
                return false;
            }
        };

        // Combine actions
        SequenceAction finalSequence = new SequenceAction();
        finalSequence.addAction(initialAlphaOut);
        finalSequence.addAction(alphaIn);
        finalSequence.addAction(loader);
        mFadeImage.addAction(finalSequence);
    }

    private void playClickSound() {

        // Play click sound
        mGame.mAssetsManager.click.play(0.5f, 1.0f, 0);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {

        // Dispose everything possible
        mBackground.dispose();
        mShapeRenderer.dispose();
        mSpriteBatch.dispose();
        mStage.dispose();

    }

}
