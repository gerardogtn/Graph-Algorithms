package com.gerardogtn.graphalgorithms.data.model;

import android.graphics.PointF;

import com.gerardogtn.graphalgorithms.R;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by gerardogtn on 11/1/15.
 */
public class Node {

    public static final int COLOR = 0xFF3F51B5;
    public static final float RADIUS =  50;
    public static final float RADIUS_SQUARED = (float) Math.pow(RADIUS, 2);

    private static int sCounter = 0;

    private float x;
    private float y;

    private int id;
    private int mData;


    // TODO: Change to private and implement iterable.
    public Set<Edge> edges;

    public Node(){
        this(-1);
    }

    public Node(int mData) {
        id = ++sCounter;
        this.mData = mData;
        this.x = 100;
        this.y = 100;
        edges = new LinkedHashSet<>();
    }

    public int getId() {
        return id;
    }

    public int getData() {
        return mData;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setData(int mData) {
        this.mData = mData;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    // TODO: Redundancy in storing in a node an edge with reference to itself.
    public void addEdge(Node target){
        edges.add(new Edge(this, target));
    }

    public void updatePosition(PointF current) {
        this.x = current.x;
        this.y = current.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + id;
        result = 31 * result + mData;
        return result;
    }
}
