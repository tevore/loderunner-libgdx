package com.mygdx.loderunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MainMenuScreen implements Screen {

    private final LodeRunner game;
    private final OrthographicCamera camera;
//    private final BitmapFont gameFont;


    public MainMenuScreen(LodeRunner game) {
        this.game = game;
        camera = new OrthographicCamera();

        camera.setToOrtho(false, 200, 200);

//        gameFont = new BitmapFont(Gdx.files.classpath("raw/font-title.fnt"));
//        gameFont.setColor(Color.WHITE);
//        gameFont.getData().setScale(0.50f, 0.37f);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

//        gameFont.draw(game.batch, "BREAKOUT", 50, 100);
//        gameFont.draw(game.batch, "Tap anywhere to begin...", 50, 90);

        game.font.draw(game.batch, "Welcome to LodeRunner!!! ", 25, 25);
        game.font.draw(game.batch, "Tap anywhere to begin!", 20, 20);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }

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
