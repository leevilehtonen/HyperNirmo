package com.hypernirmo.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class Background {

    private Array<BackgroundItem> mBackgroundItemLayers;

    public Background() {

        //Create array for layers
        mBackgroundItemLayers = new Array<BackgroundItem>();
    }

    public void addBackgroundLayer(BackgroundItem mBackgroundItem) {

        //Add new background layer
        mBackgroundItemLayers.add(mBackgroundItem);
    }

    public void update(float delta) {

        //Update layers
        Iterator<BackgroundItem> backgroundLayersIter = mBackgroundItemLayers
                .iterator();
        while (backgroundLayersIter.hasNext()) {
            BackgroundItem mItem = backgroundLayersIter.next();
            mItem.update(delta);
        }
    }

    public void increaseSpeed(float addition) {

        //Increase speeds of the layers
        for (BackgroundItem mItem : mBackgroundItemLayers) {
            mItem.setSpeed(addition + mItem.getSpeed());
        }
    }

    public Array<Sprite> getSpritesToRender() {

        //Create one array holding all the sprites to render
        Array<Sprite> spritesToRender = new Array<Sprite>();

        //Get the sprites for render array
        for (BackgroundItem mItem : mBackgroundItemLayers) {
            spritesToRender.addAll(mItem.getSprites());
        }

        //Return the render array
        return spritesToRender;
    }

    public void dispose() {

        //Dispose items
        for (BackgroundItem bgItem : mBackgroundItemLayers) {
            bgItem.dispose();
        }

        //Clear the array
        mBackgroundItemLayers.clear();
    }

}

