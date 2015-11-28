package com.gerardogtn.graphalgorithms.util.task;

import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.ui.view.GraphView;

/**
 * Created by gerardogtn on 11/27/15.
 */
public class AlgorithmAnimationTask implements Runnable {

    private Graph mGraph;
    private int mIndex;
    private GraphView.OnStopAnimationListener mStopListener;

    public AlgorithmAnimationTask(Graph graph, int index, GraphView.OnStopAnimationListener listener) {
        this.mGraph = graph;
        this.mIndex = index;
        this.mStopListener = listener;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    public void setListener(GraphView.OnStopAnimationListener listener){
        this.mStopListener = listener;
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Executes appropiate algorithm if  0 <= mIndex <= 5, else if mIndex =  6 throws
    // IllegalArgumentException, else trows IndexOutOfBoundsException.
    @Override
    public void run() {

        try {
            if (mIndex == 0) {
                mGraph.dfs();
            } else if (mIndex == 1) {
                mGraph.bfs();
            } else if (mIndex == 2) {
                mGraph.prim();
            } else if (mIndex == 3) {
                mGraph.kruskal();
            } else if (mIndex == 4) {
                mGraph.dijkstra();
            } else if (mIndex == 5) {
                mGraph.bellmanFord();
            } else if (mIndex == 6) {
                throw new IllegalArgumentException("Floyd Warshall is not implemented in GraphView");
            } else {
                throw new IndexOutOfBoundsException();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        mStopListener.stopAnimation(true);
    }
}
