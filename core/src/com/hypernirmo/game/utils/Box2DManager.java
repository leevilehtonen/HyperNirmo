package com.hypernirmo.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.hypernirmo.game.HyperNirmoGame;
import com.hypernirmo.game.objects.Box2DBox;
import com.hypernirmo.game.objects.Box2DNirmo;
import com.hypernirmo.game.world.GameWorld;

public class Box2DManager {

    public static final float WORLD_TO_BOX = 0.0025f;
    public static final float BOX_TO_WORLD = 400f;

    private HyperNirmoGame mGame;
    private World mPhysicsWorld;
    private GameWorld mGameWorld;

    public Box2DManager(HyperNirmoGame mGame, GameWorld mGameWorld) {

        //Initialize variables
        this.mGame = mGame;
        this.mGameWorld = mGameWorld;

    }

    public void createPhyicsWorldAndCollisionListener() {

        //Create physics world
        mPhysicsWorld = new World(new Vector2(0, -9.81f), true);

        //Set new contact listener to the world
        mPhysicsWorld.setContactListener(new ContactListener() {

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

            @Override
            public void endContact(Contact contact) {

                //Get fixtures
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                //Check character leaving ground
                if ((fixtureA.getFilterData().categoryBits == 0x0002 && fixtureB
                        .getFilterData().categoryBits == 0x0001)
                        || (fixtureB.getFilterData().categoryBits == 0x0002 && fixtureA
                        .getFilterData().categoryBits == 0x0001)) {

                    //Set nirmo not on ground
                    mGameWorld.getNirmo().setOnGround(false);

                    //Play jump sound
                    if (mGameWorld.getNirmo().getBox2dNirmo().getJumped()) {
                        mGame.mAssetsManager.jump.play(1.0f, 1.2f, 0);
                    }

                }
            }

            @Override
            public void beginContact(Contact contact) {

                //Get fixtures
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                //Check for ground collision
                if ((fixtureA.getFilterData().categoryBits == 0x0002 && fixtureB
                        .getFilterData().categoryBits == 0x0001)
                        || (fixtureB.getFilterData().categoryBits == 0x0002 && fixtureA
                        .getFilterData().categoryBits == 0x0001)) {

                    //Back on ground
                    mGameWorld.getNirmo().setOnGround(true);

                    //Check for spike collisions
                } else if ((fixtureA.getFilterData().categoryBits == 0x0004 && fixtureB
                        .getFilterData().categoryBits == 0x0001)
                        || (fixtureB.getFilterData().categoryBits == 0x0004 && fixtureA
                        .getFilterData().categoryBits == 0x0001)) {

                    //Gameover
                    mGameWorld.gameOver();
                }
            }
        });
    }

    public void createStaticObjects() {

        //Create ground
        createStaticGround();

        //Create sky
        createStaticSky();
    }

    private void createStaticGround() {

        //Create BodyDef for ground
        BodyDef mGroundDef = new BodyDef();
        mGroundDef.type = BodyDef.BodyType.StaticBody;
        mGroundDef.position.set(mGame.WIDTH / 2 * WORLD_TO_BOX, 400 * WORLD_TO_BOX);

        //Create body from BodyDef
        Body mGroundBody = mPhysicsWorld.createBody(mGroundDef);

        //Create shape for ground
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(mGame.WIDTH / 2 * WORLD_TO_BOX, 10 * WORLD_TO_BOX);

        //Create FixtureDef
        FixtureDef mFixtureDefGround = createFixtureDef(groundShape);
        mFixtureDefGround.filter.categoryBits = 0x0002;

        //Add FixtureDef to Body
        mGroundBody.createFixture(mFixtureDefGround);

        //Dispose shapes
        groundShape.dispose();
    }

    private void createStaticSky() {

        //Create BodyDef for sky
        BodyDef mSkyDef = new BodyDef();
        mSkyDef.type = BodyDef.BodyType.StaticBody;
        mSkyDef.position.set(mGame.WIDTH / 2 * WORLD_TO_BOX, (mGame.HEIGHT + 340) * WORLD_TO_BOX);

        //Create body from BodyDef
        Body mSkyBody = mPhysicsWorld.createBody(mSkyDef);

        //Create shape for s
        PolygonShape skyShape = new PolygonShape();
        skyShape.setAsBox(mGame.WIDTH / 2 * WORLD_TO_BOX, 10 * WORLD_TO_BOX);

        //Add FixtureDef to Body
        mSkyBody.createFixture(createFixtureDef(skyShape));

        //Dispose shapes
        skyShape.dispose();
    }

    public FixtureDef createFixtureDef(Shape shape) {

        // Create fixturedef using given shape
        FixtureDef mFixtureDef = new FixtureDef();
        mFixtureDef.shape = shape;
        mFixtureDef.density = 0f;
        mFixtureDef.friction = 0f;
        mFixtureDef.restitution = 0f;
        return mFixtureDef;

    }

    public void update(float delta) {
        mPhysicsWorld.step(1 / 60f, 6, 2);
    }

    //Getters and setters
    public World getPhysicsWorld() {
        return mPhysicsWorld;
    }

    //Physics object for character
    public Box2DNirmo createPhysicsNirmo(float x, float y) {
        return new Box2DNirmo(this, x, y);
    }

    //Physics object for box
    public Box2DBox createPhysicsBox(float x, float y, float speed) {
        return new Box2DBox(this, x, y, speed);
    }

    //Dispose world
    public void dispose() {
        mPhysicsWorld.dispose();

    }

}
