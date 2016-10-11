package com.hypernirmo.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hypernirmo.game.utils.AssetsManager;


public class CharacterAnimator {

    private Animation mWalkAnimation;
    private TextureRegion[] mWalkFrames;

    private Animation mJumpAnimation;
    private TextureRegion[] mJumpFrames;

    public CharacterAnimator(AssetsManager manager) {

        //Create array holding the 30 textureregions
        mWalkFrames = new TextureRegion[30];

        //Fill the array using the animation frames from atlas
        int i = 0;
        for (TextureAtlas.AtlasRegion region : manager.mNirmoWalkAtlas.getRegions()) {
            mWalkFrames[i] = region;
            i++;
        }

        //Create animation using the animation frames
        mWalkAnimation = new Animation(1 / 30f, mWalkFrames);

        //Create array holding the 30 textureregions
        mJumpFrames = new TextureRegion[30];

        //Fill the array using the animation frames from atlas
        int j = 0;
        for (TextureAtlas.AtlasRegion region : manager.mNirmoJumpAtlas.getRegions()) {
            mJumpFrames[j] = region;
            j++;
        }

        //Create animation using the animation frames
        mJumpAnimation = new Animation(1 / 30f, mJumpFrames);

    }

    //Getters for both animations
    public Animation getWalkAnimation() {
        return this.mWalkAnimation;
    }

    public Animation getJumpAnimation() {
        return mJumpAnimation;
    }
}
