package com.mygdx.loderunner;

import com.badlogic.gdx.math.Rectangle;

public class Ladder {

    private Rectangle rectangle;

    public Ladder(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}
