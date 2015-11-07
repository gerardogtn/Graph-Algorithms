package com.gerardogtn.graphalgorithms.data.model;

import android.support.annotation.NonNull;


/**
 * Created by gerardogtn on 11/1/15.
 */
public class Edge implements Comparable<Edge>{

    private int id;
    private static int sCounter = 0;

    private Node origin;
    private Node destination;

    private boolean mIsActive = false;
    private boolean isDirected;
    private int weight;

    public Edge(Node origin, Node destination, boolean isDirected) {
        this(origin, destination, 0, isDirected);
    }

    public Edge(Node origin, Node destination, int weight, boolean isDirected) {
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
        this.isDirected = isDirected;
        this.id = ++sCounter;
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

    public void setOrigin(Node origin) {
        this.origin = origin;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    public boolean isDirected() {
        return isDirected;
    }

    public void setDirected(boolean isDirected) {
        this.isDirected = isDirected;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setActive(boolean isActive) {
        this.mIsActive = isActive;
    }

    @Override
    public int compareTo(@NonNull Edge edge) {
        return this.weight - edge.getWeight();
    }
}
