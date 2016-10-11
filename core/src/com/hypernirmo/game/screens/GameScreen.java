package com.hypernirmo.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hypernirmo.game.HyperNirmoGame;
import com.hypernirmo.game.world.GameRenderer;
import com.hypernirmo.game.world.GameWorld;

public class GameScreen implements Screen {

    //Game variable
    private HyperNirmoGame mGame;

    //Viewport and camera variables
    private OrthographicCamera mCamera;
    private FitViewport mViewport;
    private Stage mStage;

    //Gameworld and renderer
    private GameWorld mGameWorld;
    private GameRenderer mGameRenderer;

    //Bitmapfonts
    private BitmapFont mLargeFont;
    private BitmapFont mSmallFont;

    private Skin mSkin;

    private Table guiTable, pauseTable, gameOverTable;

    private Label speedLabel, highScoreLabel, scoreLabel;

    private LabelStyle smallLabelStyle, largeLabelStyle;

    public GameScreen(final HyperNirmoGame hyperNirmoGame) {

        //Initialize variables
        mGame = hyperNirmoGame;

        //Create camera
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false, mGame.WIDTH, mGame.HEIGHT);
        mViewport = new FitViewport(mGame.WIDTH, mGame.HEIGHT, mCamera);

        //Create stage and set it to receive input
        mStage = new Stage();
        mStage.setViewport(mViewport);
        Gdx.input.setInputProcessor(mStage);

        //Create world and renderer
        mSkin = mGame.mAssetsManager.mSkin;
        mGameWorld = new GameWorld(mGame, this);
        mGameRenderer = new GameRenderer(mGame, this, mGameWorld);

        //Define fonts and labels in use
        mLargeFont = mGame.mAssetsManager.mLargeFont;
        mSmallFont = mGame.mAssetsManager.mSmallFont;

        // Create labelstyles from fonts
        largeLabelStyle = new LabelStyle(mLargeFont, Color.WHITE);
        smallLabelStyle = new LabelStyle(mSmallFont, Color.WHITE);

        //Create UI
        createUserInterface();

    }


    private void createUserInterface() {

        //Create table
        guiTable = new Table();
        guiTable.setPosition(mGame.WIDTH - 10, mGame.HEIGHT - 60);
        guiTable.right();
        mStage.addActor(guiTable);

        //Create labels
        scoreLabel = new Label("Score: " + mGame.mGameManager.getDisplayableScore(), largeLabelStyle);
        highScoreLabel = new Label("Highscore: " + mGame.mGameManager.getDisplayableHighScore(), smallLabelStyle);
        speedLabel = new Label("Speed: " + mGame.mGameManager.getDisplayableSpeed(), smallLabelStyle);

        //Add lables to table
        guiTable.add(scoreLabel).padLeft(35).padTop(20);
        guiTable.add(highScoreLabel).padLeft(35).padTop(25);
        guiTable.add(speedLabel).padLeft(35).padTop(25);

        //Create buttonstyle
        ButtonStyle pauseStyle = new ButtonStyle();
        pauseStyle.up = mSkin.getDrawable("PauseBtn");

        //Create button;
        Button pauseBtn = new Button(pauseStyle);
        guiTable.add(pauseBtn).padLeft(50);
        pauseBtn.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                //Play click sound
                playClickSound();

                //Pause game
                if (!mGame.mGameManager.mGamePaused) {
                    pauseGame();
                } else {
                    mGame.mGameManager.mGamePaused = false;

                    if (pauseTable != null)
                        pauseTable.clear();
                }
            }

        });
    }

    @Override
    public void render(float delta) {

        if (mGame.mAssetsManager.mAssetManager.update()) {

            //Render the screen
            mGameRenderer.render(delta);

            //Update game world
            if (!mGame.mGameManager.mGamePaused && !mGame.mGameManager.mGameOver) {
                mGameWorld.update(delta);

                //Update score
                scoreLabel.setText("Score: " + mGame.mGameManager.getDisplayableScore());
            }

            //Check back button
            if (mGame.mGameManager.mCanPressBackButton && Gdx.input.isKeyPressed(Keys.BACK)) {
                if (!mGame.mGameManager.mGamePaused && !mGame.mGameManager.mGameOver) {
                    pauseGame();
                }
            }
            //Update and render the stage
            mStage.act();
            mStage.draw();
        }

    }

    @Override
    public void resize(int width, int height) {

        mViewport.update(width, height, true);

    }

    private void pauseGame() {

        //Pause game
        mGame.mGameManager.mGamePaused = true;

        //Not catch backbutton
        mGame.mGameManager.mCanPressBackButton = false;

        // Flush preferences
        mGame.mGameManager.flushPrefs();

        // Create table
        pauseTable = new Table();
        pauseTable.setPosition(mGame.WIDTH / 2, mGame.HEIGHT / 2);
        mStage.addActor(pauseTable);

        //Create buttonstyles
        ButtonStyle continueButtonStyle = new ButtonStyle();
        continueButtonStyle.up = mSkin.getDrawable("ContinueBtn");
        ButtonStyle restartButtonStyle = new ButtonStyle();
        restartButtonStyle.up = mSkin.getDrawable("RestartBtn");
        ButtonStyle menuButtonStyle = new ButtonStyle();
        menuButtonStyle.up = mSkin.getDrawable("MenuBtn");

        //Create buttons
        Button continueBtn = new Button(continueButtonStyle);
        Button restartBtn = new Button(restartButtonStyle);
        Button menuBtn = new Button(menuButtonStyle);

        //Add buttons to table
        pauseTable.add(continueBtn).pad(20);
        pauseTable.row();
        pauseTable.add(restartBtn).pad(20);
        pauseTable.row();
        pauseTable.add(menuBtn).pad(20);
        pauseTable.row();

        continueBtn.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                //Play click
                playClickSound();

                //Clear the table
                pauseTable.clear();

                //Resume
                mGame.mGameManager.mGamePaused = false;

                //Catch back button again
                mGame.mGameManager.mCanPressBackButton = true;
            }
        });
        restartBtn.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                //Play click
                playClickSound();

                //Restart
                restart();

            }
        });
        menuBtn.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                //Play click
                playClickSound();

                //Fade to the menu
                fadeToMainScreen();
            }
        });

    }

    // Restart game
    public void restart() {

        //Flush prefs
        mGame.mGameManager.flushPrefs();

        //Reset game manager
        mGame.mGameManager.reset();

        //Dispose current world
        mGameWorld.dispose();

        //Dispose current renders
        mGameRenderer.dispose();

        //Create new world
        mGameWorld = new GameWorld(mGame, this);

        //Create new renderer
        mGameRenderer = new GameRenderer(mGame, this, mGameWorld);

        //Clear stage
        mStage.clear();

        //Create UI
        createUserInterface();
    }

    public void gameOver() {

        //Gameover
        mGame.mGameManager.mGameOver = true;
        mGame.mGameManager.mCanPressBackButton = false;
        mGame.mGameManager.flushPrefs();

        //Clear gui table
        if (guiTable != null)
            guiTable.clear();

        //Create game over table
        gameOverTable = new Table();
        gameOverTable.setPosition(mGame.WIDTH / 2, mGame.HEIGHT / 2);
        mStage.addActor(gameOverTable);

        //Check if new score is higher then the highscore
        if (mGame.mGameManager.checkNewHighScore()) {

            //Create new highscore label
            Label scoreLabel = new Label("New highscore: " + mGame.mGameManager.getDisplayableScore(), largeLabelStyle);
            gameOverTable.add(scoreLabel).pad(5);
            gameOverTable.row();

        } else if (!gameOverTable.hasChildren()) {

            //Create new core label
            Label scoreLabel = new Label("Score: " + mGame.mGameManager.getDisplayableScore(), largeLabelStyle);
            gameOverTable.add(scoreLabel).pad(5);
            gameOverTable.row();
        }

        //Create labels
        Label highScoreLabel = new Label("Highscore: " + mGame.mGameManager.getDisplayableHighScore(), smallLabelStyle);
        Label speedLabel = new Label("Speed: " + mGame.mGameManager.getDisplayableSpeed(), smallLabelStyle);
        gameOverTable.add(highScoreLabel).pad(5);
        gameOverTable.row();
        gameOverTable.add(speedLabel).padBottom(50);
        gameOverTable.row();

        //Create buttonstyles
        ButtonStyle restartButtonStyle = new ButtonStyle();
        restartButtonStyle.up = mSkin.getDrawable("RestartBtn");
        ButtonStyle menuButtonStyle = new ButtonStyle();
        menuButtonStyle.up = mSkin.getDrawable("MenuBtn");

        //Create buttons
        Button restartBtn = new Button(restartButtonStyle);
        Button menuBtn = new Button(menuButtonStyle);
        gameOverTable.add(restartBtn).pad(20);
        gameOverTable.row();
        gameOverTable.add(menuBtn).pad(20).padBottom(100);
        gameOverTable.row();

        restartBtn.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                //Restart
                playClickSound();
                restart();
            }
        });
        menuBtn.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                //Back to menu
                playClickSound();
                fadeToMainScreen();
            }
        });

    }

    private void fadeToMainScreen() {

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

                // Open menu
                openMenuScreen();
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

    private void openMenuScreen() {

        //Dispose game screen
        dispose();

        //Unload animations
        mGame.mAssetsManager.unloadGameAnimations();

        //Set menu screen
        mGame.setScreen(new MenuScreen(mGame));
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

        // Dispose current world
        mGameWorld.dispose();

        // Dispose current renders
        mGameRenderer.dispose();

        // Dispose stage
        mStage.dispose();

    }

    // Getters and setters
    public OrthographicCamera getCamera() {
        return mCamera;
    }

    // Update speed label
    public void updateSpeed() {
        speedLabel.setText("Speed: " + mGame.mGameManager.getDisplayableSpeed());
    }

}
