package com.hypernirmo.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

import java.util.Iterator;

public class MenuScreen implements Screen{

    private static final int CLOUD_SPAWN_LOCATION = 1080;
    private static final int CLOUD_SPAWN_POINT = 1000;
    private static final float CLOUD_SPEED_MODIFIER = 10f;
    private HyperNirmoGame mGame;
    private OrthographicCamera mCamera;
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

    public MenuScreen(HyperNirmoGame hyperNirmoGame) {

        //Initialize variables
        this.mGame = hyperNirmoGame;

        //Create camera and viewport
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false, mGame.WIDTH, mGame.HEIGHT);
        mViewport = new FitViewport(mGame.WIDTH, mGame.HEIGHT, mCamera);

        //Load assets
        mGame.mAssetsManager.loadBackground();
        mGame.mAssetsManager.loadFonts();
        mGame.mAssetsManager.loadSounds();
        mGame.mAssetsManager.loadUIElements();

        //Create a new stage
        mStage = new Stage(mViewport);
        Gdx.input.setInputProcessor(mStage);

        //Initialize skin
        mSkin = mGame.mAssetsManager.mSkin;

        //Create renderers
        mSpriteBatch = new SpriteBatch();
        mShapeRenderer = new ShapeRenderer();
        mSpriteBatch.setProjectionMatrix(mCamera.combined);
        mShapeRenderer.setProjectionMatrix(mCamera.combined);

        //Create background
        createBackground();

        //Create scene
        createScene();

        //Create UI
        createUI();


    }

    private void createUI() {

        //Create table
        mTable = new Table();
        mTable.setPosition(mGame.WIDTH / 2, mGame.HEIGHT / 2);
        mStage.addActor(mTable);

        //Create title
        Image titleImg = new Image(mSkin.getDrawable("Title"));
        mTable.add(titleImg).size(1080, 256).pad(100);
        mTable.row();

        //Create button styles
        ButtonStyle playButtonStyle = new ButtonStyle();
        ButtonStyle aboutButtonStyle = new ButtonStyle();
        ButtonStyle quitButtonStyle = new ButtonStyle();
        playButtonStyle.up = mSkin.getDrawable("PlayBtn");
        aboutButtonStyle.up = mSkin.getDrawable("AboutBtn");
        quitButtonStyle.up = mSkin.getDrawable("QuitBtn");

        //Create buttons
        Button playBtn = new Button(playButtonStyle);
        Button aboutBtn = new Button(aboutButtonStyle);
        Button quitBtn = new Button(quitButtonStyle);
        mTable.add(playBtn).pad(10);
        mTable.row();
        mTable.add(aboutBtn).pad(10);
        mTable.row();
        mTable.add(quitBtn).pad(10).padBottom(200);
        mTable.row();

        playBtn.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                // Play click
                playClickSound();

                // Fade to game
                fadeTo("game");
            }
        });

        aboutBtn.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                // Play click
                playClickSound();

                // Fade to about screen
                fadeTo("about");
            }
        });
        quitBtn.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                // Play click
                playClickSound();

                // Fade to quit
                fadeTo("exit");
            }
        });

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

        //Random type and random location
        int y = MathUtils.random(900, 1800);
        int type = MathUtils.random(0, 3);

        //Create a cloud
        Sprite cloud = new Sprite(mCloudTextures.get(type));
        cloud.setPosition(cloudSpawnLocation, y);
        mClouds.add(cloud);
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

    private void fadeTo(final String destination) {

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

        //Action for loading new screen
        Action loader = new Action() {
            @Override
            public boolean act(float delta) {

                //Switch between destinations
                if (destination.equals("game")) {
                    dispose();
                    mGame.mAssetsManager.loadGameAnimations();
                    mGame.mGameManager.reset();
                    mGame.setScreen(new GameScreen(mGame));

                } else if (destination.equals("about")) {
                    dispose();
                    mGame.setScreen(new AboutScreen(mGame));

                } else if (destination.equals("exit")) {
                    Gdx.app.exit();
                }
                return false;
            }
        };

        //Combine actions
        SequenceAction finalSequence = new SequenceAction();
        finalSequence.addAction(initialAlphaOut);
        finalSequence.addAction(alphaIn);
        finalSequence.addAction(loader);
        mFadeImage.addAction(finalSequence);

    }

    private void playClickSound() {
        mGame.mAssetsManager.click.play(0.5f, 1.0f, 0);
    }




    @Override
    public void render(float delta) {

        if (mGame.mAssetsManager.mAssetManager.update()) {

            //Clear screen
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            //Render background
            mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
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

            //Update and render stage
            mStage.act();
            mStage.draw();

            //Check back button
            if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {

                //Exit
                playClickSound();
                fadeTo("exit");
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        mViewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        // Dispose everything
        mBackground.dispose();
        mShapeRenderer.dispose();
        mSpriteBatch.dispose();
        mStage.dispose();

    }
}
