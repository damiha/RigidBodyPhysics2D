package com.example.rigidbody2d;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Vector;

public class World {

    int canvasWidth, canvasHeight, pixelPerWorldUnit;
    GraphicsContext gc;

    ArrayList<RigidBody> rigidBodies;

    Vector3 lastMousePosInWorldCoords;
    Vector3 mousePosInWorldCoords;

    boolean drawVertices = true;
    boolean drawMousePosition = true;

    DisplayMode displayMode = DisplayMode.WIREFRAME;
    boolean drawNormals = false;
    boolean drawContactPoints = true;

    boolean isPhysicsActive = false;

    // acceleration due to gravity
    double g = -1;

    ArrayList<Vector3> allContactPoints;

    public World(int canvasWidth, int canvasHeight, int pixelPerWorldUnit, GraphicsContext gc){
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.pixelPerWorldUnit = pixelPerWorldUnit;
        this.gc = gc;

        rigidBodies = new ArrayList<>();
        allContactPoints = new ArrayList<>();
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

        if(drawContactPoints){

            gc.save();
            gc.setFill(Color.rgb(245, 150, 65)); // orange

            for(Vector3 contactPoint : allContactPoints){
                Vector3 contactPointOnScreen = toScreenCoords(contactPoint);

                gc.fillOval(contactPointOnScreen.x, contactPointOnScreen.y, 10, 10);
            }

            gc.restore();
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

    public void togglePhysics(){
        isPhysicsActive = !isPhysicsActive;
    }

    public void update(double dt, double mouseX, double mouseY,  boolean mousePressed,
                       boolean RPressed, boolean EPressed, boolean TPressed, boolean ShiftPressed){

        allContactPoints.clear();

        mousePosInWorldCoords = toWorldCoords(new Vector3(mouseX, mouseY, 0));

        // user interaction
        if(mousePressed){

            for(RigidBody rigidBody : rigidBodies){
               if(rigidBody instanceof Rectangle rectangle){

                    if(CollisionDetection.isIntersecting(rectangle, mousePosInWorldCoords)){

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

        // add all the forces
        if(isPhysicsActive) {

            for (RigidBody rigidBody : rigidBodies) {
                rigidBody.acceleration = new Vector3(0, -5, 0);
            }

            // integration
            for (RigidBody rigidBody : rigidBodies) {

                if(!rigidBody.isStatic){
                    rigidBody.angle += (dt * rigidBody.angularVelocity);
                    rigidBody.velocity = Vector3.add(rigidBody.velocity, Vector3.mul(dt, rigidBody.acceleration));
                    rigidBody.position = Vector3.add(rigidBody.position, Vector3.mul(dt, rigidBody.velocity));
                }
            }

            // collision detection and response
            for (int i = 0; i < rigidBodies.size(); i++) {
                for (int j = i + 1; j < rigidBodies.size(); j++) {

                    RigidBody r1 = rigidBodies.get(i);
                    RigidBody r2 = rigidBodies.get(j);

                    Pair<Vector3, Double> result = CollisionDetection.isColliding(r1, r2);
                    Vector3 contactNormal = result.key();
                    Double penetrationDepth = result.value();

                    if (contactNormal == null) {
                        continue;
                    }

                    // separate the objects, then calculate the contact points
                    double r1MovesBy = 0.0;
                    double r2MovesBy = 0.0;

                    if(!r1.isStatic && !r2.isStatic){
                        double m1 = 1.0 / r1.inverseMass;
                        double m2 = 1.0 / r2.inverseMass;
                        double M = m1 + m2;

                        r1MovesBy = penetrationDepth * (m2 / M);
                        r2MovesBy = penetrationDepth * (m1 / M);
                    }
                    else if(!r1.isStatic){
                        r1MovesBy = penetrationDepth;
                    }
                    else if(!r2.isStatic){
                        r2MovesBy = penetrationDepth;
                    }

                    r1.position.add(Vector3.mul(r1MovesBy, contactNormal));
                    r2.position.add(Vector3.mul(-r2MovesBy, contactNormal));

                    // now calculate the contact points and do the dynamic collision response
                    ArrayList<Vector3> contactPoints = CollisionDetection.getContactPoints(r1, r2, contactNormal);
                    allContactPoints.addAll(contactPoints);

                    Vector3 r1AdditionalVelocity = new Vector3();
                    Vector3 r2AdditionalVelocity = new Vector3();

                    double r1AdditionalAngularVelocity = 0;
                    double r2AdditionalAngularVelocity = 0;

                    for(Vector3 contactPoint : contactPoints){

                        Vector3 rA = Vector3.sub(contactPoint, r1.position);
                        Vector3 rB = Vector3.sub(contactPoint, r2.position);

                        // we only care about the z component for these two vectors (others are empty anyway)
                        Vector3 rAxN = Vector3.cross(rA, contactNormal);
                        Vector3 rBxN = Vector3.cross(rB, contactPoint);

                        double leftHandSide = r1.inverseMass + r2.inverseMass +
                                ((rAxN.z * rAxN.z) * r1.inverseMomentOfInertia) + ((rBxN.z * rBxN.z) * r2.inverseMomentOfInertia);

                        // coefficient of restitution
                        double C = 0.2;
                        double rightHandSide = (-1 - C) * (
                                Vector3.dot(r1.velocity, contactNormal) - Vector3.dot(r2.velocity, contactNormal) +
                                r1.angularVelocity * rAxN.z - r2.angularVelocity * rBxN.z);

                        double impulseMagnitude = rightHandSide / leftHandSide;

                        // the impulse should be distributed between the contact points
                        impulseMagnitude /= contactPoints.size();

                        Vector3 normalImpulse = Vector3.mul(impulseMagnitude, contactNormal);

                        // J / m = v
                        r1AdditionalVelocity.add(Vector3.mul(r1.inverseMass, normalImpulse));
                        // apply impulse in opposite direction
                        r2AdditionalVelocity.add(Vector3.mul(-r2.inverseMass, normalImpulse));

                        r1AdditionalAngularVelocity += (impulseMagnitude * r1.inverseMomentOfInertia * rAxN.z);
                        r2AdditionalAngularVelocity -= (impulseMagnitude * r2.inverseMomentOfInertia * rBxN.z);

                        // the friction impulse
                        // |F_R| = mu * |F_n|
                        // |J_r| = mu * |J_n|
                        double frictionCoefficient = 0.5;
                        double impulseMagnitudeFrictionImpulse = frictionCoefficient * impulseMagnitude / contactPoints.size();

                        // relative velocity (needs velocities at the points)
                        Vector3 vA = Vector3.add(r1.velocity, Vector3.cross(new Vector3(0, 0, r1.angularVelocity), rA));
                        Vector3 vB = Vector3.add(r2.velocity, Vector3.cross(new Vector3(0, 0, r2.angularVelocity), rB));

                        Vector3 relativeVelocity = Vector3.sub(vA, vB);
                        Vector3 tangentialDirection = Vector3.sub(relativeVelocity,
                                Vector3.mul(Vector3.dot(relativeVelocity, contactNormal), contactNormal));

                        if(Utils.isClose(0, tangentialDirection.norm())){
                            continue;
                        }

                        tangentialDirection.normalize();

                        // acts in the direction of A
                        Vector3 frictionImpulse = Vector3.mul(-impulseMagnitudeFrictionImpulse, tangentialDirection);

                        r1AdditionalVelocity.add(Vector3.mul(r1.inverseMass, frictionImpulse));
                        // apply impulse in opposite direction
                        r2AdditionalVelocity.add(Vector3.mul(-r2.inverseMass, frictionImpulse));

                        double rAxFrictionImpulse = Vector3.cross(rA, frictionImpulse).z;
                        double rBxFrictionImpulse = Vector3.cross(rB, frictionImpulse).z;

                        r1AdditionalAngularVelocity += (r1.inverseMomentOfInertia * rAxFrictionImpulse);
                        r2AdditionalAngularVelocity -= (r2.inverseMomentOfInertia * rBxFrictionImpulse);
                    }

                    r1.velocity.add(r1AdditionalVelocity);
                    r2.velocity.add(r2AdditionalVelocity);

                    r1.angularVelocity += r1AdditionalAngularVelocity;
                    r2.angularVelocity += r2AdditionalAngularVelocity;
                }
            }
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

            if(displayMode == DisplayMode.SOLID) {
                gc.setFill(rectangle.color);
                gc.fillRect(-rectangleWidth / 2, -rectangleHeight / 2, rectangleWidth, rectangleHeight);
            }
            else if(displayMode == DisplayMode.WIREFRAME){
                gc.setStroke(rectangle.color);
                gc.strokeRect(-rectangleWidth / 2, -rectangleHeight / 2, rectangleWidth, rectangleHeight);
            }
            gc.restore();

            // draw contact normals
            if(drawNormals){

                ArrayList<Vector3> vertices = rectangle.getVertices();

                ArrayList<Vector3> normals = CollisionDetection.getAxes(vertices, rectangle.position);

                for(int i = 0; i < vertices.size(); i++){

                    Vector3 vStart = vertices.get(i);
                    Vector3 vEnd = vertices.get((i + 1) % vertices.size());

                    Vector3 midPoint = Vector3.mul(0.5, Vector3.add(vStart, vEnd));
                    Vector3 endPoint = Vector3.add(midPoint, normals.get(i));

                    Vector3 midPointOnScreen = toScreenCoords(midPoint);
                    Vector3 endPointOnScreen = toScreenCoords(endPoint);

                    gc.strokeLine(midPointOnScreen.x, midPointOnScreen.y, endPointOnScreen.x, endPointOnScreen.y);
                }
            }
        }
    }
}
