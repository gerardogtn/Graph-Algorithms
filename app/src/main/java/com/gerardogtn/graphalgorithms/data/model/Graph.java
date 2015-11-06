package com.gerardogtn.graphalgorithms.data.model;

import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gerardogtn.graphalgorithms.util.NodeIdNotFoundException;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

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

    private OnGraphUpdateListener listener;

    private Graph(boolean isDirected){
        mEdges = new LinkedHashSet<>();
        mNodes = new LinkedList<>();
        this.isDirected = isDirected;
    }

    public static Graph getInstance(boolean isDirected){
        if (mInstance == null){
            mInstance = new Graph(isDirected);
        }
        return mInstance;
    }

    public synchronized void addEdge(int originId, int destinationId, int weight){
        Edge edge = new Edge(originId, destinationId, weight, isDirected);
        mEdges.add(edge);
    }

    public synchronized void addEdge(Edge edge){
        mEdges.add(edge);
    }

    public synchronized void addEdge(Node origin, Node destination, int weight){
        Edge edge = new Edge(origin, destination, weight, isDirected);
        mEdges.add(edge);
    }

    public synchronized void addNode(Node node){
        mNodes.push(node);
    }

    public void setOnGraphUpdateListener(OnGraphUpdateListener listener){
        this.listener = listener;
    }

    public int getNodesSize(){
        return mNodes.size();
    }

    public int getEdgesSize(){
        return mEdges.size();
    }

    public Set<Edge> getEdges() {
        return mEdges;
    }

    public LinkedList<Node> getNodes() {
        return mNodes;
    }

    public LinkedList<Edge> getEdgesFromNode(Node node){
        LinkedList<Edge> output = new LinkedList<>();

        for (Edge edge : mEdges){
            if (edge.getOrigin().equals(node)){
                output.push(edge);
            }
        }

        Collections.sort(output);
        return output;
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

    // REQUIRES: None.
    // MODIFIES: this.
    // EFFECTS:  Sets all nodes to not visited
    public synchronized void clearVisited(){
        for (Node node : mNodes){
            node.setVisited(false);
        }
    }


    // TODO: Handle synchronized locks to avoid skipping frames.
    public synchronized void dfs() throws InterruptedException{
        Node node = mNodes.getLast();
        Stack<Node> stack = new Stack<>();
        node.setVisited(true);
        stack.push(node);
        listener.redraw();
        Thread.sleep(700);

        boolean found;
        while(!stack.empty()){
            Node currentNode = stack.peek();
            found = false;

            for(Edge edge : getEdgesFromNode(currentNode)){
                if(!edge.getDestination().wasVisited()){
                    edge.setActive(true);
                    listener.redraw();
                    Thread.sleep(200);
                    edge.getDestination().setVisited(true);
                    stack.push(edge.getDestination());
                    found = true;
                    listener.redraw();
                    Thread.sleep(700);
                    break;
                }
                edge.setActive(false);
                listener.redraw();
                Thread.sleep(200);
            }

            if(!found){
                stack.pop();
            }
        }
    }

    public interface OnGraphUpdateListener{
        void redraw();
    }

}
