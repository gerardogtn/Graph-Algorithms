package com.gerardogtn.graphalgorithms.data.model;

import android.support.annotation.NonNull;


/**
 * Created by gerardogtn on 11/1/15.
 */
public class Edge implements Comparable<Edge>{

    private Node origin;
    private Node destination;

    private boolean mIsActive = false;
    private boolean isDirected;
    private boolean mIsIdle = false;
    private int weight;

    public Edge(Node origin, Node destination, boolean isDirected) {
        this(origin, destination, 0, isDirected);
    }

    public Edge(Node origin, Node destination, int weight, boolean isDirected) {
        this.origin      = origin;
        this.destination = destination;
        this.weight      = weight;
        this.isDirected  = isDirected;
    }

    public Edge(int originId, int destinationId, boolean isDirected){
        this(originId, destinationId, 0, isDirected);
    }

    public Edge(int originId, int destinationId, int weight, boolean isDirected){
        this.origin = Graph.findNodeById(originId);
        this.destination = Graph.findNodeById(destinationId);
        this.weight = weight;
        this.isDirected = isDirected;
    }

    public Node getOrigin() {
        return origin;
    }

    public Node getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public boolean isIdle() {
        return mIsIdle;
    }

    public boolean isDirected() {
        return isDirected;
    }

    public void setActive(boolean isActive) {
        this.mIsActive = isActive;
    }

    public void setIdle(boolean mIsIdle) {
        this.mIsIdle = mIsIdle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (isDirected != edge.isDirected) return false;
        if (weight != edge.weight) return false;
        if (!origin.equals(edge.origin)) return false;
        return destination.equals(edge.destination);

    }

    @Override
    public int hashCode() {
        int result = origin.hashCode();
        result = 31 * result + destination.hashCode();
        result = 31 * result + (isDirected ? 1 : 0);
        result = 31 * result + weight;
        return result;
    }

    @Override
    public int compareTo(@NonNull Edge edge) {
        return this.weight - edge.getWeight();
    }
}
