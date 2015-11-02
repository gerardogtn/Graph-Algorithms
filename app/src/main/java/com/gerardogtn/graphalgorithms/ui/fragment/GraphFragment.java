package com.gerardogtn.graphalgorithms.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gerardogtn.graphalgorithms.R;
import com.gerardogtn.graphalgorithms.data.model.Node;
import com.gerardogtn.graphalgorithms.ui.view.GraphView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    GraphView graphView;

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
        return v;
    }

    public void addNode(Node node){
        graphView.addNode(node);
    }

}
