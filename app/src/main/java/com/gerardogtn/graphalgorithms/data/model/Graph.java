package com.gerardogtn.graphalgorithms.data.model;

import android.graphics.PointF;
import android.support.annotation.Nullable;

import com.gerardogtn.graphalgorithms.util.NodeIdNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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

    private Graph(boolean isDirected) {
        mEdges = new LinkedHashSet<>();
        mNodes = new LinkedList<>();
        this.isDirected = isDirected;
    }

    public static Graph getInstance(boolean isDirected) {
        if (mInstance == null) {
            mInstance = new Graph(isDirected);
        }
        return mInstance;
    }

    public synchronized void addEdge(Edge edge) {
        mEdges.add(edge);
    }

    public synchronized void addEdge(Node origin, Node destination, int weight) {
        Edge edge = new Edge(origin, destination, weight, isDirected);
        mEdges.add(edge);
    }

    public synchronized void addNode(Node node) {
        mNodes.push(node);
    }

    public synchronized void addNodes(List<Node> nodes) {
        for (Node node : nodes) {
            addNode(node);
        }
    }

    public synchronized void addEdges(List<Edge> edges) {
        for (Edge edge : edges) {
            addEdge(edge);
        }
    }

    public synchronized void addNodesReverse(LinkedList<Node> nodes) {
        Iterator<Node> iterator = nodes.descendingIterator();
        while (iterator.hasNext()) {
            addNode(iterator.next());
        }
    }

    public void setOnGraphUpdateListener(OnGraphUpdateListener listener) {
        this.listener = listener;
    }

    public int getNodesSize() {
        return mNodes.size();
    }

    public int getEdgesSize() {
        return mEdges.size();
    }

    public Set<Edge> getEdges() {
        return mEdges;
    }

    public LinkedList<Node> getNodes() {
        return mNodes;
    }

    public LinkedList<Edge> getEdgesFromNode(Node node) {
        LinkedList<Edge> output = new LinkedList<>();

        for (Edge edge : mEdges) {
            if (edge.getOrigin().equals(node)) {
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
    public Node findNextNode(PointF point) {
        for (Node node : mNodes) {
            boolean belongs = Math.pow(point.x - node.getX(), 2)
                    + Math.pow(point.y - node.getY(), 2)
                    < Node.RADIUS_SQUARED;

            if (belongs) {
                return node;
            }
        }
        return null;
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Returns node with matching id or throws NodeIdNotFoundException if id was not found.
    // TODO: Change to binary search.
    public static Node findNodeById(int id) {
        for (Node node : mNodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        throw new NodeIdNotFoundException(id);
    }

    // REQUIRES: None.
    // MODIFIES: this.
    // EFFECTS : Sets all nodes to not visited and resets node's parent and distance values.
    public void reset(){
        for (Node node : mNodes){
            node.setParent(null);
            node.setDistance(Node.MAX_VALUE);
            node.setActive(false);
            node.setVisited(false);
        }

        for (Edge edge : mEdges){
            edge.setActive(false);
            edge.setIdle(false);
        }
    }

    // REQUIRES: None.
    // MODIFIES: this.
    // EFFECTS:  Sets all nodes to not visited
    public synchronized void clearVisited() {
        for (Node node : mNodes) {
            node.setVisited(false);
        }
    }

    public synchronized void clearNodes() {
        mNodes = new LinkedList<>();
    }

    public void clearEdges() {
        mEdges = new LinkedHashSet<>();
    }

    public synchronized void dfs() throws InterruptedException {
        Node node = mNodes.getLast();
        Stack<Node> stack = new Stack<>();
        node.setVisited(true);
        stack.push(node);
        listener.redraw();
        Thread.sleep(700);

        boolean found;
        while (!stack.empty()) {
            Node currentNode = stack.peek();
            found = false;

            for (Edge edge : getEdgesFromNode(currentNode)) {
                if (!edge.getDestination().wasVisited()) {
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

            if (!found) {
                stack.pop();
            }
        }
    }

    public synchronized void bfs() throws InterruptedException {
        Queue<Node> queue = new LinkedList<Node>();
        Node node = mNodes.getLast();
        node.setVisited(true);
        listener.redraw();
        Thread.sleep(700);

        queue.add(node);
        while (queue.peek() != null) {
            for (Edge edge : getEdgesFromNode(queue.remove())) {
                if (!edge.getDestination().wasVisited()) {
                    edge.setActive(true);
                    listener.redraw();
                    Thread.sleep(200);
                    edge.getDestination().setVisited(true);
                    queue.add(edge.getDestination());
                    listener.redraw();
                    Thread.sleep(700);
                }
                edge.setActive(false);
                listener.redraw();
                Thread.sleep(200);
            }
        }
    }

    public synchronized void dijkstra() throws InterruptedException {
        Node origin = mNodes.getLast();

        LinkedList<Node> notVisited = new LinkedList<Node>();

        for (Node node : mNodes) {
            notVisited.add(node);
        }

        origin.setDistance(0);
        origin.setVisited(true);
        listener.redraw();
        Thread.sleep(700);

        while (!notVisited.isEmpty()) {
            Collections.sort(notVisited);
            Node node = notVisited.getFirst();
            node.setVisited(true);
            listener.redraw();
            Thread.sleep(700);
            notVisited.removeFirst();


            for (Edge edge : getEdgesFromNode(node)) {
                edge.setActive(true);
                listener.redraw();
                Thread.sleep(200);
                Node destination = edge.getDestination();
                int weight = edge.getWeight();

                if (destination.getDistance() > node.getDistance() + weight) {
                    edge.setIdle(true);
                    destination.setDistance(node.getDistance() + weight);
                    destination.setParent(node);
                    listener.connectNodes();
                    Thread.sleep(700);
                }
                listener.redraw();
                Thread.sleep(200);
            }
        }
    }

    public interface OnGraphUpdateListener {
        void redraw();
        void connectNodes();
    }

}
