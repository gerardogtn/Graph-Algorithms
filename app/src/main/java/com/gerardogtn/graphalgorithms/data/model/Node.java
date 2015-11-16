package com.gerardogtn.graphalgorithms.data.model;

import android.graphics.PointF;
import android.support.annotation.NonNull;

/**
 * Created by gerardogtn on 11/1/15.
 */
public class Node implements Comparable<Node>{

    private int id;

    public static final int MAX_VALUE = 0x0FFFFFFF;

    public static final int COLOR = 0xFF3F51B5;
    public static final int COLOR_VISITED = 0xFFFF5722;
    public static final int COLOR_ACTIVE = 0xFFF44336;
    public static final float RADIUS =  50;
    public static final float RADIUS_SQUARED = (float) Math.pow(RADIUS, 2);

    private static int sCounter = 0;

    private float x;
    private float y;

    private boolean mWasVisited;
    private boolean mIsActive;

    private int mData;

    // Used for dijkstra's algorithm.
    private int mDistance;
    private Node mParent;

    // Used for kruskal algorithm.
    private int mSet = -1;

    public Node(int mData) {
        id = ++sCounter;
        this.mData = mData;
        this.x = 100;
        this.y = 100;
        this.mIsActive = false;
        this.mWasVisited = false;
        this.mDistance = MAX_VALUE;
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

    public boolean wasVisited() {
        return mWasVisited;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public int getDistance() {
        return mDistance;
    }

    public Node getParent() {
        return mParent;
    }

    public int getSet() {
        return mSet;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setData(int mData) {
        this.mData = mData;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setActive(boolean mIsActive) {
        this.mIsActive = mIsActive;
    }

    public void setVisited(boolean mWasVisited) {
        this.mWasVisited = mWasVisited;
    }

    public void setDistance(int distance) {
        this.mDistance = distance;
    }

    public void setParent(Node parent) {
        this.mParent = parent;
    }

    public void setSet(int set) {
        this.mSet = set;
    }

    public void updatePosition(PointF current) {
        this.x = current.x;
        this.y = current.y;
    }

    public static void resetCounter(){
        sCounter = 0;
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
        return result;
    }

    @Override
    public int compareTo(@NonNull Node node) {
        return this.mDistance - node.getDistance();
    }
}
