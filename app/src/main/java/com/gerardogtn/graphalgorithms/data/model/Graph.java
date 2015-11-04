package com.gerardogtn.graphalgorithms.data.model;

import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gerardogtn.graphalgorithms.util.NodeIdNotFoundException;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by gerardogtn on 11/3/15.
 */

// TODO: Implement iterable to avoid getEdges and getNodes.
public class Graph {

    public static final String TAG = "Graph";

    private boolean isDirected;

    private static Graph mInstance;

    private static Set<Edge> mEdges;
    private static LinkedList<Node> mNodes;

    private Graph(boolean isDirected){
        mEdges = new LinkedHashSet<>();
        mNodes = new LinkedList<>();
        this.isDirected = isDirected;
    }

    public static<Type> Graph getInstance(boolean isDirected){
        if (mInstance == null){
            mInstance = new Graph(isDirected);
        }
        return mInstance;
    }

    public void addEdge(int originId, int destinationId, int weight){
        Edge edge = new Edge(originId, destinationId, weight, isDirected);
        mEdges.add(edge);
    }

    public void addEdge(Edge edge){
        mEdges.add(edge);
    }

    public void addEdge(Node origin, Node destination, int weight){
        Edge edge = new Edge(origin, destination, weight, isDirected);
        Log.d(TAG, "Edge with origin: "
                + origin.getId()
                + ", destination: "
                + destination.getId()
                + ", and weight: "
                + weight
                + " was created!");
        mEdges.add(edge);
    }

    public void addNode(Node node){
        mNodes.push(node);
    }

    public int getNodesSize(){
        return mNodes.size();
    }

    public int getEdgesSize(){
        return mEdges.size();
    }

    public Set<Edge> getmEdges() {
        return mEdges;
    }

    public LinkedList<Node> getmNodes() {
        return mNodes;
    }


    // REQUIRES: PointF is valid.
    // MODIFIES: None.
    // EFFECTS: Returns the first node found that encompasses PointF. Returns null if not found.
    @Nullable
    public Node findNextNode(PointF point){
        for (Node node : mNodes){
            boolean belongs = Math.pow(point.x - node.getX(), 2)
                    +  Math.pow(point.y - node.getY(), 2)
                    < Node.RADIUS_SQUARED;

            if (belongs){
                return node;
            }
        }
        return null;
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Returns node with matching id or throws NodeIdNotFoundException if id was not found.
    // TODO: Change to binary search.
    public static Node findNodeById(int id){
        for(Node node : mNodes){
            if (node.getId() == id){
                return node;
            }
        }
        throw new NodeIdNotFoundException(id);
    }
}
