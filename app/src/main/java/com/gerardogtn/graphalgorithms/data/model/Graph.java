package com.gerardogtn.graphalgorithms.data.model;

import android.graphics.PointF;
import android.support.annotation.Nullable;

import com.gerardogtn.graphalgorithms.util.NodeIdNotFoundException;

import java.util.Collections;
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

    public static final int NODE_VISITED_ANIMATION_TIME = 700;
    public static final int NODE_ACTIVE_ANIMATION_TIME = 700;
    public static final int EDGE_ACTIVE_ANIMATION_TIME = 400;
    public static final int EDGE_IDLE_ANIMATION_TIME = 400;

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

    public Set<Edge> getEdges() {
        return mEdges;
    }

    public LinkedList<Node> getNodes() {
        return mNodes;
    }

    public static LinkedList<Edge> getEdgesFromNode(Node node) {
        LinkedList<Edge> output = new LinkedList<>();

        for (Edge edge : mEdges) {
            if (edge.getOrigin().equals(node)) {
                output.push(edge);
            }
        }

        Collections.sort(output);
        return output;
    }

    public static int getNodesSize(){
        return mNodes.size();
    }

    public static int getEdgesSize(){
        return mEdges.size();
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
            node.setSet(-1);
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
        stack.push(node);

        makeNodeVisited(node);

        boolean found;
        while (!stack.empty()) {
            Node currentNode = stack.peek();
            found = false;

            for (Edge edge : getEdgesFromNode(currentNode)) {
                if (!edge.getDestination().wasVisited()) {
                    makeEdgeActive(edge, true);

                    Node destination = edge.getDestination();
                    stack.push(destination);
                    makeNodeVisited(destination);

                    found = true;
                    break;
                }
                makeEdgeActive(edge, false);
            }

            if (!found) {
                stack.pop();
            }
        }
    }

    public synchronized void bfs() throws InterruptedException {
        Queue<Node> queue = new LinkedList<>();
        Node node = mNodes.getLast();

        makeNodeVisited(node);

        queue.add(node);
        while (queue.peek() != null) {
            for (Edge edge : getEdgesFromNode(queue.remove())) {
                if (!edge.getDestination().wasVisited()) {
                    makeEdgeActive(edge, true);

                    Node destination = edge.getDestination();
                    queue.add(edge.getDestination());

                    makeNodeVisited(destination);
                }
                makeEdgeActive(edge, false);
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
        makeNodeVisited(origin);

        while (!notVisited.isEmpty()) {
            Collections.sort(notVisited);
            Node node = notVisited.getFirst();
            makeNodeVisited(node);

            notVisited.removeFirst();

            for (Edge edge : getEdgesFromNode(node)) {
                makeEdgeActive(edge, true);
                Node destination = edge.getDestination();
                int weight = edge.getWeight();

                if (destination.getDistance() > node.getDistance() + weight) {
                    edge.setIdle(true);
                    destination.setDistance(node.getDistance() + weight);
                    destination.setParent(node);
                    listener.connectNodes();
                    Thread.sleep(700);
                }
                makeEdgeIdle(edge, true);
            }
        }
    }

    public synchronized void bellmanFord() throws InterruptedException{
        boolean relax = true;
        Node origin = mNodes.getLast();
        origin.setDistance(0);
        makeNodeVisited(origin);

        while(relax) {
            relax = false;
            for (Edge edge : mEdges) {
                makeEdgeActive(edge, true);
                Node destination = edge.getDestination();
                Node originNode = edge.getOrigin();

                if (originNode.getDistance() + edge.getWeight() < destination.getDistance()) {
                    makeNodeVisited(destination);
                    destination.setDistance(originNode.getDistance() + edge.getWeight());
                    destination.setParent(originNode);
                    listener.connectNodes();
                    Thread.sleep(700);
                    relax = true;
                }

                makeEdgeIdle(edge, true);
            }
        }

    }

    public synchronized void prim() throws InterruptedException {
        Node origin = mNodes.getLast();
        makeNodeVisited(origin);
        LinkedList<Edge> queue = new LinkedList<>();

        for (Edge edge : getEdgesFromNode(origin)){
            queue.add(edge);
        }

        while (!queue.isEmpty()){
            Edge edge = queue.pop();
            makeEdgeActive(edge, true);

            if (!edge.getDestination().wasVisited()) {
                Node node = edge.getDestination();
                makeNodeVisited(node);

                for (Edge current : getEdgesFromNode(node)){
                    queue.push(current);
                    Collections.sort(queue);
                }

                makeEdgeActive(edge, false);
            } else {
                makeEdgeIdle(edge, true);
            }

        }
    }

    public synchronized void kruskal() throws InterruptedException{
        LinkedList<Edge> queue = new LinkedList<>();

        for (Edge edge : mEdges){
            queue.add(edge);
        }
        Collections.sort(queue);

        int currentGroup = 0;
        int groups = mNodes.size();
        while (groups != 1 && !queue.isEmpty()) {
            Edge currentEdge = queue.pop();
            makeEdgeActive(currentEdge, true);

            Node origin = currentEdge.getOrigin();
            Node destination = currentEdge.getDestination();
            makeNodeVisited(origin);
            makeNodeVisited(destination);

            int originSet = origin.getSet();
            int destinationSet = destination.getSet();

            if ((originSet == -1 && destinationSet == -1) || originSet != destinationSet) {
                groups--;
                for (Node node : mNodes){
                    if (node.getSet() != -1 && (node.getSet() == originSet || node.getSet() == destinationSet)){
                        node.setSet(currentGroup);
                    }
                }
                origin.setSet(currentGroup);
                destination.setSet(currentGroup++);

                makeEdgeActive(currentEdge, false);
            } else {
                makeEdgeIdle(currentEdge, true);
            }

        }

        for (Edge edge : queue){
            makeEdgeIdle(edge, true);
        }

    }

    public static int[][] getAdjacencyMatrix(){
        int size = mNodes.size();
        int[][] adjacencyMatrix = new int[size][size];
        int weight = Node.MAX_VALUE;
        for (Node node : mNodes){
            for (Node node2 : mNodes){
                for(Edge edge : getEdgesFromNode(node)){
                    if(edge.getDestination() == node2){
                        weight = edge.getWeight();
                    }
                }
                if(node == node2){
                    weight = 0;
                }
                adjacencyMatrix[node.getId()-1][node2.getId()-1] = weight;
                weight = Node.MAX_VALUE;
            }
        }
        return adjacencyMatrix;
    }



    // REQUIRES: Is not called on UI thread.
    // MODIFIES: node.
    // EFFECTS:  Sets node to active, redraws, and waits.
    private synchronized void makeNodeActive(Node node, boolean isActive) throws InterruptedException {
        node.setActive(isActive);
        listener.redraw();
        Thread.sleep(NODE_ACTIVE_ANIMATION_TIME);
    }

    // REQUIRES: Is not called on UI thread.
    // MODIFIES: node
    // EFFECTS:  Sets node to visited, redraws and waits.
    private synchronized void makeNodeVisited(Node node) throws InterruptedException {
        node.setVisited(true);
        listener.redraw();
        Thread.sleep(NODE_VISITED_ANIMATION_TIME);
    }

    // REQUIRES: Is not called on UI thread.
    // MODIFIES: edge
    // EFFECTS:  Makes edge.isActive to isActive, redraws and waits.
    private synchronized void makeEdgeActive(Edge edge, boolean isActive) throws InterruptedException {
        if (edge.isIdle()){
            edge.setIdle(false);
        }
        edge.setActive(isActive);
        listener.redraw();
        Thread.sleep(EDGE_ACTIVE_ANIMATION_TIME);
    }

    // REQUIRES: Is not called on UI thread.
    // MODIFIES: edge
    // EFFECTS: If making edge idle, makes edge inactive and idle and redraws and wait. Else make edge not
    // idle and redraw and wait.
    private synchronized void makeEdgeIdle(Edge edge, boolean isIdle) throws InterruptedException {
        if (isIdle){
            edge.setActive(false);
        }
        edge.setIdle(isIdle);
        listener.redraw();
        Thread.sleep(EDGE_IDLE_ANIMATION_TIME);
    }


    public interface OnGraphUpdateListener {
        void redraw();
        void connectNodes();
    }

}
