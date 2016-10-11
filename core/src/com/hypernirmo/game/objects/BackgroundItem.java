package com.hypernirmo.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class BackgroundItem {

    private Array<Sprite> backgroundLayerSprites;
    private TextureRegion mTextureRegion;
    private float mSpeed;
    private float y;
    private boolean canCreate = true;

    public BackgroundItem(TextureRegion texture, float speed, float y) {

        //Initialize variables
        this.setSpeed(speed);
        this.y = y;
        this.mTextureRegion = texture;

        //Set filter and wrap for the textures
        this.mTextureRegion.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.mTextureRegion.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        //Create array to hold same type of sprites
        this.backgroundLayerSprites = new Array<Sprite>();

        //Create first sprite
        Sprite firstSprite = new Sprite(texture);
        firstSprite.setPosition(0f, y);

        //Add it to array
        this.backgroundLayerSprites.add(firstSprite);

    }

    public void update(float delta) {

        //Move the background and delete the ones not visible using iterator
        Iterator<Sprite> iterSprites = backgroundLayerSprites.iterator();
        while (iterSprites.hasNext()) {

            //Move sprite
            Sprite mItem = iterSprites.next();
            float x = mItem.getX();
            x = x - mSpeed * delta;
            mItem.setPosition(x, y);

            //Delete sprite
            if (x <= -1440f) {
                iterSprites.remove();
                canCreate = true;

                //Create new sprite
            } else if (x <= 1080f - 1440f && canCreate) {
                createNewSprite();
                canCreate = false;
            }
        }

    }

    private void createNewSprite() {

        //Create new sprite
        Sprite newSprite = new Sprite(mTextureRegion);

        //Set x position out of the screen and y position to the defined
        newSprite.setPosition(1080f, y);

        //Add it to the array holding all sprites
        backgroundLayerSprites.add(newSprite);
    }

    //Getter and setters
    public Array<Sprite> getSprites() {
        return backgroundLayerSprites;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float mSpeed) {
        this.mSpeed = mSpeed;
    }

    public void dispose() {

        //Clear all the sprites in use in current layer
        backgroundLayerSprites.clear();
    }

}

