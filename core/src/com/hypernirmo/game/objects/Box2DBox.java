package com.hypernirmo.game.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.hypernirmo.game.utils.Box2DManager;

public class Box2DBox {

    private Box2DManager mBox2dManager;
    private Vector2 position;
    private Body mSideBody, mUpBody;
    private float mSpeed;

    public Box2DBox(Box2DManager box2dManager, float x, float y, float speed) {

        //Initialize variables
        this.mBox2dManager = box2dManager;
        this.mSpeed = speed;
        this.position = new Vector2(x, y);

        //Create BodyDefs
        BodyDef bodyDefUp = new BodyDef();
        bodyDefUp.type = BodyDef.BodyType.StaticBody;
        bodyDefUp.position.set((x + 66) * Box2DManager.WORLD_TO_BOX, (y + 98) * Box2DManager.WORLD_TO_BOX);

        BodyDef bodyDefSide = new BodyDef();
        bodyDefSide.type = BodyDef.BodyType.StaticBody;
        bodyDefSide.position.set((x + 20) * Box2DManager.WORLD_TO_BOX, (position.y + 52) * Box2DManager.WORLD_TO_BOX);

        //Create bodies
        mUpBody = mBox2dManager.getPhysicsWorld().createBody(bodyDefUp);
        mSideBody = mBox2dManager.getPhysicsWorld().createBody(bodyDefSide);

        //Create shapes
        PolygonShape boxup = new PolygonShape();
        boxup.setAsBox(48f * Box2DManager.WORLD_TO_BOX, 2f * Box2DManager.WORLD_TO_BOX);

        PolygonShape boxside = new PolygonShape();
        boxside.setAsBox(2f * Box2DManager.WORLD_TO_BOX, 40f * Box2DManager.WORLD_TO_BOX);

        //Create FixtureDefs
        FixtureDef fixtureDefUp = mBox2dManager.createFixtureDef(boxup);
        fixtureDefUp.filter.categoryBits = 0x0002;
        mUpBody.createFixture(fixtureDefUp);

        FixtureDef fixtureDefSide = mBox2dManager.createFixtureDef(boxside);
        fixtureDefSide.filter.categoryBits = 0x0004;
        mSideBody.createFixture(fixtureDefSide);

        //Dispose shapes
        boxup.dispose();
        boxside.dispose();

    }

    public void update(float delta) {

        //Move the x position with current speed (1. upside, 2. side)
        float x = mUpBody.getPosition().x * Box2DManager.BOX_TO_WORLD;
        x = x - mSpeed * delta;
        mUpBody.setTransform(x * Box2DManager.WORLD_TO_BOX, mUpBody.getPosition().y, 0);

        x = this.mSideBody.getPosition().x * Box2DManager.BOX_TO_WORLD;
        x = x - mSpeed * delta;
        mSideBody.setTransform(x * Box2DManager.WORLD_TO_BOX, mSideBody.getPosition().y, 0);

        //Update position
        position.set(mUpBody.getPosition().x * Box2DManager.BOX_TO_WORLD - 76, mUpBody.getPosition().y * Box2DManager.BOX_TO_WORLD - 98);

    }

    public void increaseSpeed(float addition) {

        //Increase Box2D objects speed
        mSpeed = mSpeed + addition;
    }

    //Getters and setters
    public Vector2 getPosition() {
        return position;
    }

    public void dispose() {

        //Dispose the boxes
        if (!mUpBody.equals(null)) {
            mBox2dManager.getPhysicsWorld().destroyBody(this.mUpBody);
        }

        if (!mSideBody.equals(null)) {
            mBox2dManager.getPhysicsWorld().destroyBody(this.mSideBody);

        }
    }
}
