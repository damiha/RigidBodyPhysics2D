package com.example.rigidbody2d;

import java.util.ArrayList;

public class Rectangle extends RigidBody{

    double width, height;
    boolean isStatic;

    static final double DENSITY = 1.0;

    ArrayList<Vector3> vertices;

    public Rectangle(double centerX, double centerY, double width, double height, double angle, boolean isStatic) {

        super(centerX, centerY, angle, 0.0, 0.0, isStatic);

        this.width = width;
        this.height = height;
        this.isStatic = isStatic;

        updatePhysicalProperties();
    }

    @Override
    public AABB getAABB() {
        ArrayList<Vector3> vertices = getVertices();

        double minX = 10E4;
        double minY = 10E4;
        double maxX = -10E4;
        double maxY = -10E4;

        for(Vector3 vertex : vertices){
            minX = Math.min(vertex.x, minX);
            maxX = Math.max(vertex.x, maxX);

            minY = Math.min(vertex.y, minY);
            maxY = Math.max(vertex.y, maxY);
        }
        return new AABB(minX, minY, maxX, maxY);
    }

    public void setWidth(double newWidth){
        width = newWidth;
        updatePhysicalProperties();
    }

    public void setHeight(double newHeight){
        height = newHeight;
        updatePhysicalProperties();
    }

    private void updatePhysicalProperties(){
        double mass = width * height * DENSITY;
        inverseMass = isStatic ? 0.0 : (1.0 / mass);
        inverseMomentOfInertia = isStatic ? 0.0 : (12.0 / (mass * ((width * width) + (height * height))));

        vertices = new ArrayList<>();
        vertices.add(new Vector3(width / 2, height / 2, 0));
        vertices.add(new Vector3(-width / 2, height / 2, 0));
        vertices.add(new Vector3(-width / 2, -height / 2, 0));
        vertices.add(new Vector3(width / 2, -height / 2, 0));
    }

    public ArrayList<Vector3> getVertices(){

        ArrayList<Vector3> currentVertices = new ArrayList<>();

        for(Vector3 vertex : this.vertices){

            Vector3 currentVertex = Vector3.add(position, Vector3.rotateAroundZAxis(vertex, this.angle));
            currentVertices.add(currentVertex);
        }

        return currentVertices;
    }
}
