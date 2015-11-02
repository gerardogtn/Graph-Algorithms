package com.gerardogtn.graphalgorithms.data.model;

/**
 * Created by gerardogtn on 11/1/15.
 */
public class Edge {

    private Node origin;
    private Node destination;
    private int weight;

    public Edge(Node origin, Node destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public Edge(Node origin, Node destination, int weight) {
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
    }

    public Node getDestination() {
        return destination;
    }
}
