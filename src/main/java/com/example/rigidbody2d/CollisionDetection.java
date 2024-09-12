package com.example.rigidbody2d;

import java.util.ArrayList;


public class CollisionDetection {

    public static boolean isIntersecting(ArrayList<Vector3> polygon, Vector3 point){

        ArrayList<Vector3> axes = getAxes(polygon);

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

    public static ArrayList<Vector3> getAxes(ArrayList<Vector3> polygon){
        ArrayList<Vector3> axes = new ArrayList<>();

        int n = polygon.size();

        for(int i = 0; i < n; i++){

            Vector3 axis = Vector3.sub(polygon.get((i + 1) % n), polygon.get(i));
            axes.add(axis);
        }

        return axes;
    }
}
