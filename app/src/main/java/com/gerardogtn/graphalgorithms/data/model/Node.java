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
    public static final int COLOR_VISITED = 0xFFFF5722;
    public static final float RADIUS =  50;
    public static final float RADIUS_SQUARED = (float) Math.pow(RADIUS, 2);

    private float x;
    private float y;

    private boolean mWasVisited;
    private boolean mIsActive;

    private static int sCounter = 0;
    private int id;
    private int mData;

    public Node(int mData) {
        id = ++sCounter;
        this.mData = mData;
        this.x = 100;
        this.y = 100;
        this.mIsActive = false;
        this.mWasVisited = false;
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

    public void setData(int mData) {
        this.mData = mData;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setActive(boolean mIsActive) {
        this.mIsActive = mIsActive;
    }

    public void setVisited(boolean mWasVisited) {
        this.mWasVisited = mWasVisited;
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
        return result;
    }
}
