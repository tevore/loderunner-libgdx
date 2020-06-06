package com.mygdx.loderunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

public class GameScreen implements Screen {


    final LodeRunner game;

    TiledMap testMap;
    TiledMapRenderer renderedMap;

    TextureAtlas textureAtlas;

    OrthographicCamera camera;

    World world;
    Box2DDebugRenderer debugRenderer;

    Sprite king;
    Body kingBody;
    float kingSpeed = 40f;

    public GameScreen(LodeRunner game) {
        this.game = game;

        testMap = new TmxMapLoader().load("inca_map_1.tmx");
        textureAtlas = new TextureAtlas("char_pack.atlas");
        king = new Sprite(textureAtlas.createSprite("king_2"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 200, 150); //200, 150

        renderedMap = new OrthogonalTiledMapRenderer(testMap);


        Box2D.init();

        world = new World(new Vector2(0, 0), true);
//        world.setContinuousPhysics(true);
//        World.setVelocityThreshold(0);
//        breakoutContactListener = new BreakoutContactListener();
//        world.setContactListener(breakoutContactListener);
        debugRenderer = new Box2DDebugRenderer();

        setupKingBox();

    }

    private void setupKingBox() {
//        bola.setPosition(pad.getX(), pad.getY() + 5);

        BodyDef kingBD = new BodyDef();
        kingBD.type = BodyDef.BodyType.DynamicBody;
        //to not rotate around the axis
//        bolaBodyDef.fixedRotation = true;
        //TODO review this positional code
        kingBD.position.set(king.getX()+48, king.getY()+37); //y was 20

        kingBody = world.createBody(kingBD);

        //Useful for linking movement between rendered sprite and attached physics component
//        padShape.setAsBox(9.5f, 2);
        PolygonShape kingShape = new PolygonShape();
        kingShape.setAsBox(8, 8);

        FixtureDef kingFD = new FixtureDef();
        kingFD.shape = kingShape;
        kingFD.density = 0.5f;
        kingFD.friction = 0.2f;
        kingFD.restitution = 0f;

        kingBody.createFixture(kingFD);

//        ball = new Ball(bola.getX(), bola.getY(), bola, bolaBody, false);
//        bolaBody.setUserData(ball);

        kingShape.dispose();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();

        kingBody.setLinearVelocity(new Vector2(0, 0));

        //movements
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            kingBody.setLinearVelocity(kingSpeed, 0);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            kingBody.setLinearVelocity(-kingSpeed, 0);
        }

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        game.batch.begin();

        //tmx render
        renderedMap.setView(camera);
        renderedMap.render();



        king.setPosition(kingBody.getPosition().x-8, kingBody.getPosition().y+3);
        king.setOriginCenter();

        king.draw(game.batch);

        game.batch.end();

        debugRenderer.render(world, camera.combined);

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
