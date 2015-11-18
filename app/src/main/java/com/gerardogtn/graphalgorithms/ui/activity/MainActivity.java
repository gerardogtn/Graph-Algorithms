package com.gerardogtn.graphalgorithms.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.gerardogtn.graphalgorithms.R;
import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.data.model.Node;
import com.gerardogtn.graphalgorithms.ui.fragment.GraphFragment;
import com.gerardogtn.graphalgorithms.ui.view.GraphView;
import com.gerardogtn.graphalgorithms.util.file.FileConstants;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements GraphView.OnStopAnimationListener, View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String KEY_IS_DIRECTED = "is_directed";

    private GraphFragment mFragment;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.spnr_modes)
    Spinner mSpinner;

    private static FloatingActionButton mFab;

    private boolean isAlgorithmActive = false;
    private boolean mIsStepActive     = false;
    private boolean needsToBeCleared  = false;
    private static boolean isDirected = false;

    private UpdateFabTask mUpdateFabTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (savedInstanceState != null){
            isDirected = savedInstanceState.getBoolean(KEY_IS_DIRECTED);
        }
        setUpLayout();
    }

    private void setUpLayout() {
        setSupportActionBar(mToolbar);
        setUpSpinner();
        setUpGraphFragment();
        setUpFab();
    }

    private void setUpFab() {
        mUpdateFabTask = new UpdateFabTask();
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.modes_array, R.layout.partial_spinnner_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }

    private void setUpGraphFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mFragment = new GraphFragment();
        ft.add(R.id.fgmt_main, mFragment);
        ft.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_DIRECTED, isDirected);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        if (isAlgorithmActive){
            Snackbar.make(mFab, "Please wait for the animation to stop", Snackbar.LENGTH_SHORT)
                    .show();
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_speed) {
            changeAnimationMode(item);
            return true;
        } else if (id == R.id.action_clear) {
            mFragment.clearGraph();
            mFab.show();
            return true;
        } else if (id == R.id.action_share) {
            shareGraphImage();
            return true;
        } else if (id == R.id.action_export) {
            exportGraph();
            return true;
        } else if (id == R.id.action_is_directed){

            Snackbar.make(mFab, "We need to delete the graph before changing the type", Snackbar.LENGTH_LONG)
                    .setAction("CONTINUE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mFragment.clearGraph();
                            isDirected = !item.isChecked();
                            item.setChecked(isDirected);
                            Graph.setDirected(isDirected);
                        }
                    })
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void exportGraph() {
        Intent.ACTION_GET_CONTENT
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/simple");
        if (Graph.writeGraphml()) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(FileConstants.GRAPHML_PATH)));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.action_export_graph)));
        } else {
            Snackbar.make(mFab, "Error exporting graph", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void shareGraphImage() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        if (mFragment.writeGraphImage()) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(FileConstants.GRAPH_IMAGE_PATH)));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share_graph)));
        }
    }

    private void changeAnimationMode(MenuItem item) {
        if (mIsStepActive) {
            abstractChangeAnimationMode(item, R.drawable.ic_action_fast_forward, R.string.fast_forward_mode);
        } else {
            abstractChangeAnimationMode(item, R.drawable.ic_action_play, R.string.step_mode_activated);
        }
    }

    private void abstractChangeAnimationMode(MenuItem item, int drawableResource, int messageId) {
        mIsStepActive = !mIsStepActive;
        item.setIcon(drawableResource);
        Snackbar.make(mFab, messageId, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.img_go)
    void animateAlgorithm() {

        if (isAlgorithmActive){
            return;
        }

        Graph.setOnStopListener(this);
        Graph.setStepByStep(mIsStepActive);

        if (Graph.getEdgesSize() >= 1) {
            UpdateFabTask.setIsAlgorithmActive(true);
            updateFab();
            isAlgorithmActive = true;
            int selectedAlgorithm = mSpinner.getSelectedItemPosition();
            notifyUndirectedGraphAssumption(selectedAlgorithm);
            mFragment.executeAlgorithm(selectedAlgorithm, mIsStepActive);
        } else {
            Snackbar.make(mFab, R.string.one_edge, Snackbar.LENGTH_SHORT).show();
        }

    }

    private void notifyUndirectedGraphAssumption(int selectedAlgorithm) {
        if (selectedAlgorithm == 2 || selectedAlgorithm == 3) {
            Snackbar.make(mFab,
                    R.string.undirected_graph_assumption,
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void updateFab() {
        if (!mIsStepActive) {
            mFab.hide();
        } else {
            UpdateFabTask.setStepActive(true);
            runOnUiThread(mUpdateFabTask);
        }
    }

    @Override
    public void stopAnimation(boolean showClearButton) {
        this.isAlgorithmActive = false;
        mFab.show();
        UpdateFabTask.setNeedsToBeCleared(showClearButton);
        UpdateFabTask.setIsAlgorithmActive(false);
        runOnUiThread(mUpdateFabTask);
        this.needsToBeCleared = showClearButton;
    }

    @Override
    public void onClick(View view) {
        if (isAlgorithmActive && mIsStepActive) {
            nextStep();
        } else if (!needsToBeCleared) {
            mFragment.addNode(new Node(0));
        } else {
            resetGraph();
        }
    }

    private void resetGraph() {
        mFragment.resetGraph();
        mFab.setImageResource(R.drawable.ic_add_white_24dp);
        needsToBeCleared = false;
    }

    private void nextStep() {
        Graph graph = Graph.getInstance(true);
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (graph) {
            if (Graph.getStepByStep()) {
                Graph.setNextStep(false);
                graph.notify();
            }
        }
    }

    private static class UpdateFabTask implements Runnable {

        private static boolean mNeedsToBeCleared = false;
        private static boolean mStepActive = false;
        private static boolean mIsAlgorithmActive = false;

        public UpdateFabTask() {

        }

        public static void setNeedsToBeCleared(boolean mNeedsToBeCleared) {
            UpdateFabTask.mNeedsToBeCleared = mNeedsToBeCleared;
        }

        public static void setStepActive(boolean mStepActive) {
            UpdateFabTask.mStepActive = mStepActive;
        }

        public static void setIsAlgorithmActive(boolean isAlgorithmActive) {
            UpdateFabTask.mIsAlgorithmActive = isAlgorithmActive;
        }

        @Override
        public void run() {
            if (mStepActive && mIsAlgorithmActive) {
                mFab.setImageResource(R.drawable.ic_action_play);
            } else if (mNeedsToBeCleared) {
                mFab.setImageResource(R.drawable.ic_action_done);
            } else {
                mFab.setImageResource(R.drawable.ic_add_white_24dp);
            }

        }
    }

}
