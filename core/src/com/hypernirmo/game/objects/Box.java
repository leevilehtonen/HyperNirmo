package com.hypernirmo.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hypernirmo.game.world.GameWorld;

public class Box {
    private Box2DBox mBox2dBox;
    private Sprite mSprite;

    public Box(GameWorld mWorld, TextureRegion mBoxTexture, float x, float y,
               float speed) {

        //Create Box2D object
        mBox2dBox = mWorld.getBox2dManager().createPhysicsBox(x, y, speed);

        //Create box sprite
        mSprite = new Sprite(mBoxTexture);

        //Set position
        mSprite.setPosition(x, y);
    }

    public void update(float delta) {

        //Update Box2D box
        mBox2dBox.update(delta);

        //Update the sprite's position with the values from BOX2D box
        mSprite.setPosition(mBox2dBox.getPosition().x, mBox2dBox.getPosition().y);
    }

    public void increaseSpeed(float addition) {

        //Increase box speed
        mBox2dBox.increaseSpeed(addition);
    }

    //Getters and setters
    public Sprite getSprite() {
        return mSprite;
    }

    public void dispose() {

        //Dispose BOX2D box
        mBox2dBox.dispose();

        //Set active sprite to null
        mSprite = null;
    }

}
