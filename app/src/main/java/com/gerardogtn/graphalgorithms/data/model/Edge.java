package com.gerardogtn.graphalgorithms.data.model;

/**
 * Created by gerardogtn on 11/1/15.
 */
public class Edge {

    private Node origin;
    private Node destination;

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

    public void setOrigin(Node origin) {
        this.origin = origin;
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


}
