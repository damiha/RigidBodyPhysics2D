package com.example.rigidbody2d;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class World {

    int canvasWidth, canvasHeight, pixelPerWorldUnit;
    GraphicsContext gc;

    ArrayList<RigidBody> rigidBodies;

    Vector3 lastMousePosInWorldCoords;
    Vector3 mousePosInWorldCoords;

    boolean drawVertices = true;
    boolean drawMousePosition = true;

    public World(int canvasWidth, int canvasHeight, int pixelPerWorldUnit, GraphicsContext gc){
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.pixelPerWorldUnit = pixelPerWorldUnit;
        this.gc = gc;

        rigidBodies = new ArrayList<>();
    }

    public void add(RigidBody rigidBody){
        rigidBodies.add(rigidBody);
    }

    public void draw(){
        for(RigidBody rigidBody : rigidBodies){
            draw(rigidBody);
        }

        if(drawVertices){
            for(RigidBody rigidBody : rigidBodies){
                if(rigidBody instanceof Rectangle rectangle){

                    ArrayList<Vector3> vertices = rectangle.getVertices();

                    for(Vector3 vertex : vertices){
                        Vector3 vertexOnScreen = toScreenCoords(vertex);
                        gc.fillOval(vertexOnScreen.x, vertexOnScreen.y, 5, 5);
                    }
                }
            }
        }

        if(drawMousePosition) {
            Vector3 mouseOnScreen = toScreenCoords(mousePosInWorldCoords);
            gc.fillOval(mouseOnScreen.x, mouseOnScreen.y, 5, 5);
        }
    }

    private Vector3 toScreenCoords(Vector3 position){
        return new Vector3(position.x * pixelPerWorldUnit, canvasHeight - position.y * pixelPerWorldUnit, 0);
    }

    private double toScreenCoords(double worldCoord){
        return worldCoord * pixelPerWorldUnit;
    }

    private double toWorldCoords(double screenCoord){
        return screenCoord / pixelPerWorldUnit;
    }

    private Vector3 toWorldCoords(Vector3 screenCoords){
        return new Vector3(screenCoords.x / pixelPerWorldUnit, (canvasHeight - screenCoords.y) / pixelPerWorldUnit, 0);
    }

    private double toDegrees(double radians){
        return radians * (360.0 / (2 * Math.PI));
    }

    public void update(double mouseX, double mouseY,  boolean mousePressed,
                       boolean RPressed, boolean EPressed, boolean TPressed, boolean ShiftPressed){

        mousePosInWorldCoords = toWorldCoords(new Vector3(mouseX, mouseY, 0));

        // user interaction
        if(mousePressed){

            for(RigidBody rigidBody : rigidBodies){
               if(rigidBody instanceof Rectangle rectangle){

                    if(CollisionDetection.isIntersecting(rectangle.getVertices(), mousePosInWorldCoords)){
                        rectangle.suspended = true;

                        Vector3 dx = lastMousePosInWorldCoords != null ?
                                Vector3.sub(mousePosInWorldCoords, lastMousePosInWorldCoords) :
                                new Vector3(0, 0, 0);

                        rectangle.position = Vector3.add(rectangle.position, dx);

                        if(RPressed){
                            rectangle.angle += (ShiftPressed ? -0.01 : 0.01);
                        }

                        if(EPressed){
                            rectangle.setWidth(rectangle.width + (ShiftPressed ? -0.01 : 0.01));
                        }

                        if(TPressed){
                            rectangle.setHeight(rectangle.height + (ShiftPressed ? -0.01 : 0.01));
                        }
                    }
               }
            }
        }

        for(RigidBody rigidBody : rigidBodies){

        }

        lastMousePosInWorldCoords = mousePosInWorldCoords;
    }

    public void draw(RigidBody rigidBody){
        if(rigidBody instanceof Rectangle rectangle){

            Vector3 rectangleCenter = toScreenCoords(rectangle.position);
            double rectangleWidth = toScreenCoords(rectangle.width);
            double rectangleHeight = toScreenCoords(rectangle.height);

            gc.save();
            gc.translate(rectangleCenter.x, rectangleCenter.y);

            // mathematical direction of rotation = counterclockwise
            // javafx is clockwise by default
            gc.rotate(-toDegrees(rectangle.angle));
            gc.setFill(rectangle.color);
            gc.fillRect(-rectangleWidth / 2, -rectangleHeight / 2, rectangleWidth, rectangleHeight);
            gc.restore();
        }
    }
}
