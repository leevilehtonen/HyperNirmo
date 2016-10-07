package com.hypernirmo.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader.TextureAtlasParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetsManager {

    public AssetManager mAssetManager;
    public TextureAtlas mBackgroundAtlas;
    public TextureAtlas mNirmoWalkAtlas;
    public TextureAtlas mNirmoJumpAtlas;
    public TextureAtlas mUIElementsAtlas;
    public BitmapFont mLargeFont;
    public BitmapFont mSmallFont;
    public BitmapFont mAboutFont;
    public Skin mSkin;
    public Sound click;
    public Sound jump;
    public Sound lose;

    public AssetsManager() {

        // Create new GDX assets manager
        mAssetManager = new AssetManager();
    }

    // Load background assets
    public void loadBackground() {

        TextureAtlasParameter textureAtlasParameter = new TextureAtlasParameter(false);
        if (!mAssetManager.isLoaded("Background.pack")) {
            mAssetManager.load("Background.pack", TextureAtlas.class, textureAtlasParameter);
            mAssetManager.finishLoading();
        }
        mBackgroundAtlas = mAssetManager.get("Background.pack", TextureAtlas.class);
    }

    // Load character animations
    public void loadGameAnimations() {

        TextureAtlasParameter textureAtlasParameter = new TextureAtlasParameter(false);
        if (!mAssetManager.isLoaded("Walk.pack")) {
            mAssetManager.load("Walk.pack", TextureAtlas.class, textureAtlasParameter);
            mAssetManager.finishLoading();
        }
        if (!mAssetManager.isLoaded("Jump.pack")) {
            mAssetManager.load("Jump.pack", TextureAtlas.class, textureAtlasParameter);
            mAssetManager.finishLoading();
        }
        mNirmoWalkAtlas = mAssetManager.get("Walk.pack", TextureAtlas.class);
        mNirmoJumpAtlas = mAssetManager.get("Jump.pack", TextureAtlas.class);

        for (Texture texture : mNirmoWalkAtlas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Linear, TextureFilter.Linear);
        }
        for (Texture texture : mNirmoJumpAtlas.getTextures()) {
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }

    }

    // Load three size of fonts
    public void loadFonts() {

        FreeTypeFontGenerator freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("HyperNirmoFont.ttf"));
        FreeTypeFontParameter freeTypeFontParameter = new FreeTypeFontParameter();
        freeTypeFontParameter.magFilter = TextureFilter.Linear;
        freeTypeFontParameter.minFilter = TextureFilter.Linear;
        freeTypeFontParameter.size = 60;
        mLargeFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
        freeTypeFontParameter.size = 48;
        mSmallFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
        freeTypeFontParameter.size = 24;
        mAboutFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
        freeTypeFontGenerator.dispose();
    }

    // Load sound effects
    public void loadSounds() {

        mAssetManager.load("Click.wav", Sound.class);
        mAssetManager.load("Jump.wav", Sound.class);
        mAssetManager.load("Lose.wav", Sound.class);
        mAssetManager.finishLoading();

        click = mAssetManager.get("Click.wav", Sound.class);
        jump = mAssetManager.get("Jump.wav", Sound.class);
        lose = mAssetManager.get("Lose.wav", Sound.class);
    }

    // Load UI elements and skin
    public void loadUIElements() {

        TextureAtlasParameter textureAtlasParameter = new TextureAtlasParameter(false);

        if (!mAssetManager.isLoaded("UIElements.pack")) {
            mAssetManager.load("UIElements.pack", TextureAtlas.class, textureAtlasParameter);
            mAssetManager.finishLoading();
        }
        mUIElementsAtlas = mAssetManager.get("UIElements.pack", TextureAtlas.class);

        for (Texture texture : mUIElementsAtlas.getTextures()) {
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }
        mSkin = new Skin(mUIElementsAtlas);

    }

    // Unload character animations
    public void unloadGameAnimations() {

        if (mAssetManager.isLoaded("Walk.pack", TextureAtlas.class)) {
            mAssetManager.unload("Walk.pack");
            mNirmoWalkAtlas.dispose();
        }
        if (mAssetManager.isLoaded("Jump.pack", TextureAtlas.class)) {
            mAssetManager.unload("Jump.pack");
            mNirmoJumpAtlas.dispose();
        }

    }

    // Unload fonts
    public void unloadFonts() {

        mLargeFont.dispose();
        mSmallFont.dispose();
        mAboutFont.dispose();
    }

    // Unload sounds
    public void unloadSounds() {

        if (mAssetManager.isLoaded("Click.wav", Sound.class)) {
            mAssetManager.unload("Click.wav");
            click.dispose();
        }
        if (mAssetManager.isLoaded("Jump.wav", Sound.class)) {
            mAssetManager.unload("Jump.wav");
            jump.dispose();
        }
        if (mAssetManager.isLoaded("Lose.wav", Sound.class)) {
            mAssetManager.unload("Lose.wav");
            lose.dispose();
        }
    }

    // Unload background
    public void unloadBackground() {

        if (mAssetManager.isLoaded("Background.pack", TextureAtlas.class)) {
            mAssetManager.unload("Background.pack");
            mBackgroundAtlas.dispose();
        }
    }

    // Unload UI elements
    public void unloadUIElements() {

        if (mAssetManager.isLoaded("UIElements.pack", TextureAtlas.class)) {
            mAssetManager.unload("UIElements.pack");
            mUIElementsAtlas.dispose();
        }
    }

    // Dispose everything if not disposed already
    public void dispose() {

        unloadGameAnimations();
        unloadFonts();
        unloadSounds();
        unloadUIElements();
        unloadBackground();
        mAssetManager.dispose();
        mSkin.dispose();
    }
}
