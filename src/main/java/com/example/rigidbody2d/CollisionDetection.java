package com.example.rigidbody2d;

import kotlin.NotImplementedError;

import java.util.ArrayList;


public class CollisionDetection {

    public static boolean isIntersecting(Rectangle rectangle, Vector3 point){

        ArrayList<Vector3> polygon = rectangle.getVertices();
        ArrayList<Vector3> axes = getAxes(polygon, rectangle.position);

        for(Vector3 axis : axes){

            Pair<Double, Double> minMaxPolygonOnAxis = projectOntoAxis(axis, polygon);

            double pVal = Vector3.dot(axis, point);

            if(pVal < minMaxPolygonOnAxis.key() || pVal > minMaxPolygonOnAxis.value()){
                // found a separating axis
                return false;
            }
        }

        return true;
    }

    public static boolean isIntersecting(Circle circle, Vector3 point){

        double d = Vector3.sub(circle.position, point).norm();

        return d <= circle.radius;
    }

    public static Pair<ArrayList<Vector3>, Double> getContactPointsDirected(Rectangle r1, Rectangle r2, Vector3 contactNormal){

        ArrayList<Vector3> vertices1 = r1.getVertices();
        ArrayList<Vector3> vertices2 = r2.getVertices();

        int n = vertices1.size();

        ArrayList<Vector3> contactPoints = new ArrayList<>();
        double minDistance = Double.MAX_VALUE;

        for(int i = 0; i < n; i++){
            Vector3 vStart = vertices2.get(i);
            Vector3 vEnd = vertices2.get((i + 1) % n);

            Vector3 tangent = Vector3.sub(vEnd, vStart);

            boolean fitsToContactNormal = Utils.isClose(Vector3.dot(tangent, contactNormal), 0);

            if(fitsToContactNormal){

                for(Vector3 vertex1 : vertices1){

                    Vector3 projectedVertex1 = projectPointOntoLine(vStart, vEnd, vertex1);

                    double distanceToObject = Vector3.sub(projectedVertex1, vertex1).norm();

                    if(Utils.isClose(distanceToObject, minDistance)){
                        contactPoints.add(vertex1);
                    }
                    else if(distanceToObject < minDistance){

                        minDistance = distanceToObject;

                        contactPoints.clear();
                        contactPoints.add(vertex1);
                    }
                }
            }
        }

        return new Pair<>(contactPoints, minDistance);
    }

    public static ArrayList<Vector3> getContactPoints(RigidBody r1, RigidBody r2, Vector3 contactNormal){

        if(r1 instanceof Rectangle rect1 && r2 instanceof Rectangle rect2) {

            Pair<ArrayList<Vector3>, Double> contactPoints1On2 = getContactPointsDirected(rect1, rect2, contactNormal);
            Pair<ArrayList<Vector3>, Double> contactPoints2On1 = getContactPointsDirected(rect2, rect1, contactNormal);

            if(Utils.isClose(contactPoints1On2.value(), contactPoints2On1.value())){
                contactPoints1On2.key().addAll(contactPoints2On1.key());
                return contactPoints1On2.key();
            }
            else if(contactPoints1On2.value() < contactPoints2On1.value()){
                return contactPoints1On2.key();
            }
            else{
                return contactPoints2On1.key();
            }

        }
        throw new NotImplementedError();
    }

    public static Vector3 projectPointOntoLine(Vector3 vStart, Vector3 vEnd, Vector3 p){

        Vector3 dir = Vector3.sub(vEnd, vStart);
        double length = dir.norm();

        dir.normalize();

        Vector3 startToP = Vector3.sub(p, vStart);

        double projectedLength = Vector3.dot(startToP, dir);
        double t = projectedLength / length;

        // project to end points of the line segment if necessary
        t = Math.min(1, Math.max(0, t));

        return Vector3.add(vStart, Vector3.mul(t, Vector3.sub(vEnd, vStart)));
    }

    public static Pair<Vector3, Double> isColliding(RigidBody r1, RigidBody r2){

        double collisionTolerance = 1e-5;

        if(r1 instanceof Rectangle rect1 && r2 instanceof Rectangle rect2){

            // TODO: add AABB test
            ArrayList<Vector3> vertices1 = rect1.getVertices();
            ArrayList<Vector3> vertices2 = rect2.getVertices();

            ArrayList<Vector3> axes = getAxes(vertices1, rect1.position);
            axes.addAll(getAxes(vertices2, rect2.position));

            Vector3 axisLeastOverlap = null;
            double leastOverlap = Double.MAX_VALUE;

            for(Vector3 axis : axes){

                Pair<Double, Double> minMaxOnAxisRect1 = projectOntoAxis(axis, vertices1);
                double r1Left = minMaxOnAxisRect1.key();
                double r1Right = minMaxOnAxisRect1.value();

                Pair<Double, Double> minMaxOnAxisRect2 = projectOntoAxis(axis, vertices2);
                double r2Left = minMaxOnAxisRect2.key();
                double r2Right = minMaxOnAxisRect2.value();

                double overlap = Math.min(r1Right, r2Right) - Math.max(r1Left, r2Left);
                overlap = Math.max(0, overlap);

                if(overlap < collisionTolerance){

                    // found separating axis
                    return new Pair<>(null, null);
                }

                if(overlap < leastOverlap){
                    leastOverlap = overlap;
                    axisLeastOverlap = axis;
                }
            }

            // make sure collision normal points from 2 to 1
            Vector3 rect2ToRect1 = Vector3.sub(rect1.position, rect2.position);

            if(Vector3.dot(rect2ToRect1, axisLeastOverlap) < 0){
                axisLeastOverlap = Vector3.mul(-1, axisLeastOverlap);
            }

            return new Pair<>(axisLeastOverlap, leastOverlap);
        }

        return new Pair<>(null, null);
    }

    public static Pair<Double, Double> projectOntoAxis(Vector3 axis, ArrayList<Vector3> polygon){

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for(Vector3 p : polygon){

            double pVal = Vector3.dot(axis, p);

            min = Math.min(pVal, min);
            max = Math.max(pVal, max);
        }

        return new Pair<>(min, max);
    }

    public static ArrayList<Vector3> getAxes(ArrayList<Vector3> polygon, Vector3 centerOfPolygon){
        ArrayList<Vector3> axes = new ArrayList<>();

        int n = polygon.size();

        for(int i = 0; i < n; i++){

            Vector3 vStart = polygon.get(i);
            Vector3 vEnd = polygon.get((i + 1) % n);

            Vector3 tangent = Vector3.sub(vEnd, vStart);
            Vector3 normal = new Vector3(-tangent.y, tangent.x, 0);
            normal.normalize();

            // second vector (corner - center) points outside by definition
            Vector3 pointOutside = Vector3.mul(0.5, Vector3.add(vStart, vEnd));
            Vector3 directionToOutside = Vector3.sub(pointOutside, centerOfPolygon);

            if(Vector3.dot(normal, directionToOutside) < 0){
                normal = Vector3.mul(-1, normal);
            }

            axes.add(normal);
        }

        return axes;
    }
}
