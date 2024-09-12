package com.example.rigidbody2d;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApplication extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private Canvas canvas;
    private GraphicsContext gc;

    private double mouseX;
    private double mouseY;
    private boolean mousePressed;

    private World world;

    private boolean RPressed, EPressed, TPressed, ShiftPressed;

    @Override
    public void start(Stage stage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        world = new World(WIDTH, HEIGHT, 100, gc);
        world.add(new Rectangle(2.0, 2.0, 1.0, 1.0, 1, false, Color.rgb(255, 0, 0)));

        // Mouse event handlers
        scene.setOnMouseDragged(this::updateMousePosition);
        scene.setOnMouseMoved(this::updateMousePosition);
        scene.setOnMousePressed(this::handleMousePressed);
        scene.setOnMouseReleased(this::handleMouseReleased);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    RPressed = true;
                    break;
                case E:
                    EPressed = true;
                    break;
                case T:
                    TPressed = true;
                    break;
                case SHIFT:
                    ShiftPressed = true;
                    break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case R:
                    RPressed = false;
                    break;
                case E:
                    EPressed = false;
                    break;
                case T:
                    TPressed = false;
                    break;
                case SHIFT:
                    ShiftPressed = false;
                    break;
            }
        });

        stage.setTitle("RigidBody 2D Physics");
        stage.setScene(scene);
        stage.show();

        // Start the game loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        }.start();
    }

    private void updateMousePosition(MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
    }

    private void handleMousePressed(MouseEvent event) {
        mousePressed = true;
    }

    private void handleMouseReleased(MouseEvent event){
        mousePressed = false;
    }

    private void update() {
       world.update(mouseX, mouseY, mousePressed, RPressed, EPressed, TPressed, ShiftPressed);
    }

    private void render() {
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        world.draw();
    }

    public static void main(String[] args) {
        launch();
    }
}