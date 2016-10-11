package com.hypernirmo.game.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.hypernirmo.game.utils.Box2DManager;

public class Box2DNirmo {

    private Box2DManager mBox2dManager;
    private Vector2 position;
    private Body mBody;
    private Boolean jumped = false;

    public Box2DNirmo(Box2DManager mBox2dManager, float x, float y) {

        // Pass the parameters
        this.mBox2dManager = mBox2dManager;
        this.position = new Vector2(x, y);

        // Create bodydef
        BodyDef mBodyDef = new BodyDef();
        mBodyDef.type = BodyDef.BodyType.DynamicBody;
        mBodyDef.position.set(position.x * Box2DManager.WORLD_TO_BOX,
                position.y * Box2DManager.WORLD_TO_BOX);

        // Create body
        mBody = mBox2dManager.getPhysicsWorld().createBody(mBodyDef);
        mBody.setBullet(true);
        mBody.setSleepingAllowed(false);

        // Create shape
        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(35f * Box2DManager.WORLD_TO_BOX,
                150f * Box2DManager.WORLD_TO_BOX);

        // Create FixtureDef
        FixtureDef mFixtureDef = mBox2dManager.createFixtureDef(playerShape);
        mFixtureDef.filter.categoryBits = 0x0001;
        mBody.createFixture(mFixtureDef);

        // Dispose shapes
        playerShape.dispose();

    }


    public Vector2 getPosition() {
        Vector2 position = new Vector2();
        position.x = mBody.getPosition().x * Box2DManager.BOX_TO_WORLD - 95;
        position.y = mBody.getPosition().y * Box2DManager.BOX_TO_WORLD - 160;
        return position;
    }

    public void setJumped(boolean jumped) {
        this.jumped = jumped;
    }

    public void jump(float jumpCount) {
        // Jump (apply pulse with given force)
        mBody.applyLinearImpulse(new Vector2(0, jumpCount),
                mBody.getPosition(), true);

        // Set jumped to true
        jumped = true;
    }

    // Getters and setters
    public Boolean getJumped() {
        return jumped;
    }

    public void setJumped(Boolean jumped) {
        this.jumped = jumped;
    }

    public void dispose() {

        // Dispose body
        if (!mBody.equals(null)) {
            mBox2dManager.getPhysicsWorld().destroyBody(this.mBody);
        }

    }
}
