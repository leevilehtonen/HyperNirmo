package com.hypernirmo.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.hypernirmo.game.world.GameWorld;

public class Nirmo {

    private Animation mWalkAnimation, mJumpAnimation;
    private Sprite mSprite;

    private Box2DNirmo mBox2dNirmo;

    private float mStateWalkTime = 0f;
    private float mStateJumpTime = 0f;
    private float animationSpeed = 1f;
    private float jumpAnimationSpeed = 1f;

    private boolean onGround = false;
    private boolean jumpAnimtaionPlaying = false;
    private boolean keyReleased = true;

    private float jumpCount = 0;

    public Nirmo(GameWorld mWorld, CharacterAnimator mAnimator, float x, float y) {

        //Initialize variables
        this.mWalkAnimation = mAnimator.getWalkAnimation();
        this.mJumpAnimation = mAnimator.getJumpAnimation();

        //Create box2d object
        mBox2dNirmo = mWorld.getBox2dManager().createPhysicsNirmo(x, y);

        //Initialize sprite with 1st keyframe
        mSprite = new Sprite(mWalkAnimation.getKeyFrame(1));

        //Set position
        mSprite.setPosition(x, y);

    }

    public void update(float delta) {

        //Check animation if jumping or walking
        checkCurrentAnimationAndUpdateSpriteTexture(delta);

        //Get the position of the physics box and assign it to the sprite
        mSprite.setPosition(mBox2dNirmo.getPosition().x, mBox2dNirmo.getPosition().y);

    }

    private void checkCurrentAnimationAndUpdateSpriteTexture(float delta) {

        //Update animation
        mStateWalkTime += delta;

        //Check if on ground
        if (isOnGround() && keyReleased) {

            //Check if jump input (within play area)
            if ((Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched())
                    && Gdx.input.getY() > 65) {

                //Start the jump animation
                jumpAnimtaionPlaying = true;
                keyReleased = false;

            } else {

                if (mJumpAnimation.isAnimationFinished(mStateJumpTime))
                    jumpAnimtaionPlaying = false;
                jumpAnimationSpeed = 1.0f;

            }
        }

        // Check if jump animation is on
        if (jumpAnimtaionPlaying) {

            //Update animation
            mStateJumpTime += delta;

            //Update texture
            mSprite.setRegion(mJumpAnimation.getKeyFrame(mStateJumpTime * jumpAnimationSpeed, false));

            //Check if animation has passed 0.18 and jumping occurs
            if (mStateJumpTime > 0.18 && !mBox2dNirmo.getJumped()) {

                //If the jump button is still triggering make bigger jump
                if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) {
                    jumpCount = 6f;
                    jumpAnimationSpeed = 1f;
                }

                //If not, do the small jump
                else {
                    jumpCount = 4f;
                    jumpAnimationSpeed = 1.4f;
                }

                //Jump
                mBox2dNirmo.jump(jumpCount);

                //Reset jump amount
                jumpCount = 0;

            }

            // Check if animation is finished
            if (mJumpAnimation.isAnimationFinished(mStateJumpTime)) {

                //End jump animation
                jumpAnimtaionPlaying = false;

                //Reset the jump animation
                mStateJumpTime = 0;
                jumpAnimationSpeed = 1.0f;

                //Reset the physics jump state
                mBox2dNirmo.setJumped(false);

            }

        } else {

            //Update walk texture
            mSprite.setRegion(mWalkAnimation.getKeyFrame(mStateWalkTime * animationSpeed, true));

            //Reset the jump animation
            mStateJumpTime = 0;

            //Reset the physics jump state
            mBox2dNirmo.setJumped(false);

        }

        if (!Gdx.input.isKeyPressed(Input.Keys.SPACE) && !Gdx.input.isTouched()) {
            keyReleased = true;
        }

    }

    public void increaseSpeed(float addition) {

        //Increase animation speed
        animationSpeed = animationSpeed + addition;
    }

    //Getters and setters
    public Sprite getSprite() {
        return mSprite;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public Box2DNirmo getBox2dNirmo() {
        return mBox2dNirmo;
    }

    public void dispose() {

        //Dispose Box2D
        mBox2dNirmo.dispose();
        mJumpAnimation = null;
        mWalkAnimation = null;
        mSprite = null;
    }
}
