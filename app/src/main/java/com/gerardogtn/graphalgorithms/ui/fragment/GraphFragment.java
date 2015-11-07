package com.gerardogtn.graphalgorithms.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gerardogtn.graphalgorithms.R;
import com.gerardogtn.graphalgorithms.data.local.GraphDbHandler;
import com.gerardogtn.graphalgorithms.data.model.Node;
import com.gerardogtn.graphalgorithms.ui.dialog.AddEdgeDialog;
import com.gerardogtn.graphalgorithms.ui.dialog.AddNodeDialog;
import com.gerardogtn.graphalgorithms.ui.view.GraphView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment implements GraphView.ShowDialogListener,
        AddEdgeDialog.OnCreateEdgeListener{

    public static final String TAG_NODE_DIALOG = "NodeDialog";
    public static final String TAG_EDGE_DIALOG = "EdgeDialog";

    private GraphView graphView;

    public GraphFragment() {

    }

    public static GraphFragment newInstance() {
        Bundle args = new Bundle();

        GraphFragment fragment = new GraphFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_graph, container, false);
        graphView = (GraphView) v;
        graphView.setEventListener(this);

        return v;
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

    public void executeAlgorithm(int index) {
        graphView.executeAlgorithm(index);
    }

    public void clearVisited(){
        graphView.clearVisited();
    }

    public void clearGraph() {
        graphView.clearEdges();
        graphView.clearNodes();
        graphView.redraw();
    }
}
