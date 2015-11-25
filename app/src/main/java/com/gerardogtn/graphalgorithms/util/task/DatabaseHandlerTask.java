package com.gerardogtn.graphalgorithms.util.task;

import com.gerardogtn.graphalgorithms.data.local.GraphDbHandler;
import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.ui.view.GraphView;

/**
 * Created by gerardogtn on 11/24/15.
 */
public class DatabaseHandlerTask implements Runnable{

    private boolean mWrite;
    private Graph mGraph;
    private GraphDbHandler mDbHandler;
    private GraphView mView;

    public DatabaseHandlerTask(GraphView view, Graph graph, GraphDbHandler handler) {
        mWrite = false;
        mGraph = graph;
        mDbHandler = handler;
        mView = view;
    }

    public void setWrite() {
        this.mWrite = true;
    }

    public void setLoad(){
        this.mWrite = false;
    }

    // REQUIRES: None
    // MODIFIES: None
    // EFFECTS:  If mWrite then write Graph nodes to database. Else load nodes from database.
    @Override
    public synchronized void run() {
        if (mWrite){
            writeToDatabase();
        } else {
            loadDatabase();
        }
    }

    // REQUIRES: None.
    // MODIFIES: Database.
    // EFFECTS:  Clear graph database and set to values in graph singleton.
    private synchronized void writeToDatabase() {
        mDbHandler.clearEdges();
        mDbHandler.writeEdges(mGraph.getEdges());
        mDbHandler.clearNodes();
        mDbHandler.writeNodes(mGraph.getNodes());
    }

    // ASSUMES:  If graph is directed all edges are directed.
    // REQUIRES: None.
    // MODIFIES: Database.
    // EFFECTS:  Clear graph singleton and set to values in database.
    private synchronized void loadDatabase() {
        Graph.clearGraph();
        Graph.addNodesReverse(mDbHandler.getNodes());
        Graph.addEdges(mDbHandler.getEdges());
        if (!mDbHandler.getEdges().isEmpty()) {
            Graph.setDirected(mDbHandler.getEdges().getFirst().isDirected());
        }
        mView.redraw();
    }
}
