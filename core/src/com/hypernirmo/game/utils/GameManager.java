package com.hypernirmo.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameManager {

    public Preferences mPreferences;
    public float mGameSpeed;
    public float mScore = 0;
    public float mHighScore;
    public boolean mGameOver;
    public boolean mGamePaused;
    public boolean mCanPressBackButton;

    public GameManager() {

        // Reset everything and load preferences
        mPreferences = Gdx.app.getPreferences("NirmoPreferences");
        mGameSpeed = 120f;
        mScore = 0;
        mHighScore = mPreferences.getFloat("NirmoHighscore", 0);

        mGameOver = false;
        mGamePaused = false;
        mCanPressBackButton = true;
    }

    public void reset() {

        // Reset everything and update highscore
        mGameSpeed = 120f;
        mScore = 0;
        mHighScore = mPreferences.getFloat("NirmoHighscore", 0);
        mGameOver = false;
        mGamePaused = false;
        mCanPressBackButton = true;
    }

    public boolean checkNewHighScore() {

        // Check for new highscore (called outside)
        boolean newHighscore = false;

        if (mScore >= mHighScore) {
            mHighScore = mScore;
            mPreferences.putFloat("NirmoHighscore", mHighScore);
            flushPrefs();
            newHighscore = true;
        }

        return newHighscore;
    }

    // Flush prefs
    public void flushPrefs() {
        mPreferences.flush();
    }

    // Make sure the flush has happened
    public void dispose() {
        mPreferences.flush();
    }

    // Get the speed to display
    public String getDisplayableSpeed() {
        return String.format("%.0f%n", (mGameSpeed / 20)).replace(",", ".");
    }

    // Get the score to display
    public String getDisplayableScore() {
        return String.format("%.0f%n", mScore).replace(",", ".");
    }

    // Get the highscore to display
    public String getDisplayableHighScore() {
        return String.format("%.0f%n", mHighScore).replace(",", ".");

    }
}
