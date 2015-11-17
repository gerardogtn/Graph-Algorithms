package com.gerardogtn.graphalgorithms.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gerardogtn.graphalgorithms.R;
import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.ui.activity.MainActivity;
import com.gerardogtn.graphalgorithms.ui.adapter.FloydWarshallAdapter;
import com.gerardogtn.graphalgorithms.ui.view.GraphView;

/**
 * Created by gerardogtn on 11/7/15.
 */
public class FloydWarshallDialog extends DialogFragment implements View.OnClickListener {

    public static final String KEY_STEP_ACTIVE = "step_active";
    private static final int ANIMATION_TIME = 800;

    private boolean mIsActive = false;
    private boolean mIsFinished = false;
    private boolean mStepAnimation = true;
    private boolean mNextStep = false;

    private RecyclerView mRecyclerView;
    private FloydWarshallAdapter mAdapter;

    private Thread mAnimationThread;

    private GraphView.OnStopAnimationListener mOnStopListener;

    public FloydWarshallDialog(){
        mAnimationThread = new Thread(new FloydWarshallTask());
    }

    public static FloydWarshallDialog newInstance(boolean isStepActive) {
        Bundle args = new Bundle();
        args.putBoolean(KEY_STEP_ACTIVE, isStepActive);
        FloydWarshallDialog fragment = new FloydWarshallDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mOnStopListener = (GraphView.OnStopAnimationListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() +
                    " must implement OnStopAnimationListener in" +
                    ((Activity) context).getClass().getSimpleName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mStepAnimation = getArguments().getBoolean(KEY_STEP_ACTIVE);
    }

    // TODO: Refactor.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();
        setUpRecyclerView(activity);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        setUpBuilder(builder);

        final AlertDialog dialog = builder.create();
        setUpDialogWindow(dialog);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button buttonNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mIsFinished && !mIsActive) {
                            mIsActive = true;
                            mAnimationThread.start();
                        }
                    }
                });

                buttonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mIsActive) {
                            mOnStopListener.stopAnimation();
                            dialog.dismiss();
                        } else {
                            Snackbar.make(mRecyclerView, R.string.cant_dismiss_while_active, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });

                if (mStepAnimation) {
                    Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                    neutralButton.setOnClickListener(FloydWarshallDialog.this);

                }
            }
        });

        return dialog;
    }

    private void setUpBuilder(AlertDialog.Builder builder) {
        builder.setTitle(R.string.floyd_warshall);
        builder.setView(mRecyclerView);
        builder.setPositiveButton(R.string.start, null);
        builder.setNegativeButton(android.R.string.cancel, null);
        if (mStepAnimation){
            builder.setNeutralButton(R.string.next, null);
        }
    }

    private void setUpRecyclerView(MainActivity activity) {
        mRecyclerView = (RecyclerView) LayoutInflater.from(activity).inflate(R.layout.dialog_floyd_warshall, null);

        GridLayoutManager layoutManager = new GridLayoutManager(activity, Graph.getNodesSize() + 1);
        mRecyclerView.setLayoutManager(layoutManager);

        FloydWarshallAdapter adapter = new FloydWarshallAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);
        mAdapter = adapter;
    }

    public void setUpDialogWindow(AlertDialog dialog) {
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int height = (int) ((32 + 65 * (Graph.getNodesSize() + 1)) * metrics.density);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
    }


    // REQUIRES: Is not run on main thread.
    // MODIFIES: mAdapter.
    // EFFECTS:  Executes floyd warshall and redraws.
    public  void floydWarshall() throws InterruptedException {
        int size = Graph.getNodesSize();
        int[][] adjMatrix = new int[size][size];

        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                adjMatrix[i][j] = Graph.getAdjacencyMatrix()[i][j];
            }
        }

        for (int i = 0; i < size; i++){
            makeRowColumnActive(i, true);

            for (int j = 0; j < size; j++){
                for (int k = 0; k < size; k++){
                    int newValue = adjMatrix[j][k];
                    if (newValue > adjMatrix[j][i] + adjMatrix[i][k]){
                        newValue = adjMatrix[j][i] + adjMatrix[i][k];
                        updateValueAtPosition(j, k, newValue);
                        handleStepper();
                        mAdapter.makeModified(j, k, false);
                        Thread.sleep(ANIMATION_TIME);
                    }
                }
            }

            makeRowColumnActive(i, false);
        }
    }

    private void makeRowColumnActive(int i, boolean isActive) throws InterruptedException {
        mAdapter.makeRowActive(i, isActive);
        mAdapter.makeColumnActive(i, isActive);
        Thread.sleep(ANIMATION_TIME);
    }

    private void handleStepper() throws InterruptedException {
        if (mStepAnimation) {
            mNextStep = true;
            synchronized (this) {
                while (mNextStep) {
                    wait();
                }
            }
        }
    }

    private void updateValueAtPosition(int row, int column, int newValue) throws InterruptedException {
        mAdapter.makeModified(row, column, true);
        Thread.sleep(ANIMATION_TIME);
        mAdapter.updateElement(row, column, newValue);
        Thread.sleep(ANIMATION_TIME);
    }

    @Override
    public void onClick(View view) {
        if (mIsActive){
            synchronized (FloydWarshallDialog.this) {
                if (mNextStep){
                    mNextStep = false;
                    notify();
                }
            }
        }
    }

    private class FloydWarshallTask implements Runnable{

        public FloydWarshallTask(){

        }

        @Override
        public void run() {
            try {
                floydWarshall();
                mIsActive = false;
                mIsFinished = true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}


