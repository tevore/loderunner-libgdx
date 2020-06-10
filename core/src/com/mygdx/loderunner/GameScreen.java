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
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

/* TODO
   fix king body
   fix physics ( again )
   add pickups
   add ground destruction ability
 */
public class GameScreen implements Screen {


    final LodeRunner game;

    TiledMap gameMap;
    TiledMapRenderer renderedMap;

    TextureAtlas textureAtlas;

    OrthographicCamera camera;

    World world;
    Box2DDebugRenderer debugRenderer;

    Sprite king;
    Body kingBody;
    float kingSpeed = 40f;

    Ladder ladder;


    public GameScreen(LodeRunner game) {
        this.game = game;


        gameMap = new TmxMapLoader().load("inca_map_1.tmx");
        textureAtlas = new TextureAtlas("char_pack.atlas");
        king = new Sprite(textureAtlas.createSprite("king_2"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 256, 256); //200, 150

        renderedMap = new OrthogonalTiledMapRenderer(gameMap);


        Box2D.init();

        world = new World(new Vector2(0, -9.8f), true);
//        world.setContinuousPhysics(true);
//        World.setVelocityThreshold(0);
//        breakoutContactListener = new BreakoutContactListener();
//        world.setContactListener(breakoutContactListener);
        debugRenderer = new Box2DDebugRenderer();

        setupKingBox();

        setupStaticBoundsLayer();

        setupInteractLayer();

    }

    private void setupKingBox() {

        BodyDef kingBD = new BodyDef();
        kingBD.type = BodyDef.BodyType.DynamicBody;
        //to not rotate around the axis
//        bolaBodyDef.fixedRotation = true;

        kingBD.position.set(king.getX() + 10, king.getY() + 40); //y was 20

        kingBody = world.createBody(kingBD);

        //Useful for linking movement between rendered sprite and attached physics component
//        padShape.setAsBox(9.5f, 2);
        PolygonShape kingShape = new PolygonShape();
        kingShape.setAsBox(8, 12);

        FixtureDef kingFD = new FixtureDef();
        kingFD.shape = kingShape;
        kingFD.density = 1f;
        kingFD.friction = 0f;
        kingFD.restitution = 0f;

        kingBody.createFixture(kingFD);


        kingShape.dispose();
    }

    private void setupInteractLayer() {

        MapObjects mapObjects = gameMap.getLayers().get("InteractLayer").getObjects();
        for(MapObject m : mapObjects) {
            Rectangle rectangle = ((RectangleMapObject)m).getRectangle();

            ladder = new Ladder(rectangle);

        }
    }

    private void setupStaticBoundsLayer() {

        MapObjects mapObjects = gameMap.getLayers().get("CollideLayer").getObjects();
        for(MapObject mapObject : mapObjects) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

            BodyDef bodyDef = new BodyDef();
            bodyDef.fixedRotation = true;
            bodyDef.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(bodyDef);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(rectangle.width*0.5f, rectangle.height*0.5f);
            Fixture fixture = body.createFixture(shape,10f);
            fixture.setFriction(0.1F);

            body.setTransform(rectangle.getCenter(new Vector2(rectangle.getX(), rectangle.getY())), 0);

            shape.dispose();
        }

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

        kingBody.setLinearVelocity(new Vector2(0, kingBody.getLinearVelocity().y));

        //movements
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            kingBody.setLinearVelocity(kingSpeed, kingBody.getLinearVelocity().y);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            kingBody.setLinearVelocity(-kingSpeed, kingBody.getLinearVelocity().y);
        }

        if(ladder.getRectangle().overlaps(king.getBoundingRectangle())) {
            if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
                kingBody.setLinearVelocity(0, 40);
            }
        }

        RayCastCallback rayCastCallback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                System.out.println(fixture.getBody().getPosition());
                return 0;
            }
        };

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            //destroy adjacent block in direction you are facing
            //for now, we will do the space in front
            //add to breakable list?
            world.rayCast(rayCastCallback, kingBody.getPosition(), new Vector2(-1, -1));

        }

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        //render tmx map first
        renderedMap.setView(camera);
        renderedMap.render();

        game.batch.begin();

        king.setOriginCenter();
        king.setPosition(kingBody.getPosition().x-8, kingBody.getPosition().y-20);
//        king.setOriginCenter();

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
