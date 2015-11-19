package com.gerardogtn.graphalgorithms.ui.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gerardogtn.graphalgorithms.R;
import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.data.model.Node;
import com.gerardogtn.graphalgorithms.ui.activity.MainActivity;
import com.gerardogtn.graphalgorithms.ui.dialog.AddEdgeDialog;
import com.gerardogtn.graphalgorithms.ui.dialog.AddNodeDialog;
import com.gerardogtn.graphalgorithms.ui.dialog.FloydWarshallDialog;
import com.gerardogtn.graphalgorithms.ui.view.GraphView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment implements GraphView.OnEventListener,
        AddEdgeDialog.OnCreateEdgeListener{

    public static final String TAG_NODE_DIALOG = "NodeDialog";
    public static final String TAG_EDGE_DIALOG = "EdgeDialog";
    public static final String TAG_FLOYD_WARSHALL_DIALOG = "Floyd Warshall Dialog";

    private GraphView graphView;

    public GraphFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_graph, container, false);

        graphView = (GraphView) root;
        graphView.setEventListener(this);
        setOnStopListener();

        return root;
    }

    @Override
    public void onPause() {
        super.onStop();
        graphView.saveGraph();
    }

    @Override
    public void showNodeDialog() {
        FragmentManager manager = getFragmentManager();
        AddNodeDialog dialog = new AddNodeDialog();
        dialog.show(manager, TAG_NODE_DIALOG);
    }

    @Override
    public void showEdgeDialog() {
        FragmentManager manager = getFragmentManager();
        AddEdgeDialog dialog = new AddEdgeDialog();
        dialog.setOnClickListener(this);
        dialog.show(manager, TAG_EDGE_DIALOG);
    }

    @Override
    public void onCreateEdge(int weight) {
        graphView.addEdge(weight);
    }

    public void addNode(Node node){
        graphView.addNode(node);
    }


    // REQUIRES: Graph.mNodes size is less than 8 .
    // MODIFIES: graphview.
    // EFFECTS:  If index is 6, displays Floyd Warshall Dialog. Otherwise executes appropiate
    // algorithm on graphview.
    public void executeAlgorithm(int index, boolean isStepActive) {
        if (index == 6){
            showFloydWarshallDialog(isStepActive);
        } else {
            Graph.setStepByStep(isStepActive);
            graphView.executeAlgorithm(index);
        }
    }

    public void showFloydWarshallDialog(boolean isStepActive){
        FragmentManager manager = getFragmentManager();
        FloydWarshallDialog dialog = FloydWarshallDialog.newInstance(isStepActive);
        dialog.show(manager, TAG_FLOYD_WARSHALL_DIALOG);
    }

    // REQUIRES: None.
    // MODIFIES: graphview.
    // EFFECTS:  Resets graphview.
    public void resetGraph(){
        graphView.resetGraph();
    }


    // REQUIRES: None.
    // MODIFIES: graphview.
    // EFFECTS: Removes edges and nodes and redraws.
    public void clearGraph() {
        graphView.clearEdges();
        graphView.clearNodes();
        graphView.redraw();
    }

    public void setOnStopListener(){
        graphView.setOnStopListener((MainActivity) getActivity());
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS: Returns false if there was an error when saving, true otherwise.
    public boolean writeGraphImage() {
        return graphView.writeGraphImage();
    }
}
