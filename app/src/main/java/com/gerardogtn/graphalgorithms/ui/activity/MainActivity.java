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

    private GraphFragment mFragment;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.spnr_modes)
    Spinner mSpinner;

    private static FloatingActionButton mFab;

    private boolean isAlgorithmActive = false;
    private boolean mIsStepActive = false;
    private boolean needsToBeCleared = false;

    private UpdateFabTask mUpdateFabTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpLayout();
        mUpdateFabTask = new UpdateFabTask();
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
    }

    private void setUpLayout() {
        setSupportActionBar(mToolbar);
        setUpSpinner();
        setUpGraphFragment();
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.modes_array, R.layout.partial_spinnner_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }

    private void setUpGraphFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mFragment = GraphFragment.newInstance();
        ft.add(R.id.fgmt_main, mFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_speed) {
            changeAnimationMode(item);
        } else if (id == R.id.action_clear) {
            mFragment.clearGraph();
            mFab.show();
        } else if (id == R.id.action_share) {
            shareGraphImage();
        } else if (id == R.id.action_export) {
            exportGraph();
        }

        return super.onOptionsItemSelected(item);
    }

    private void exportGraph() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/simple");
        if (Graph.writeGraphml()) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(FileConstants.GRAPHML_PATH)));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.export_graph)));
        } else {
            Snackbar.make(mFab, "Error exporting graph", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void shareGraphImage() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        if (mFragment.writeGraphImage()) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(FileConstants.GRAPH_IMAGE_PATH)));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_graph)));
        }
    }

    private void changeAnimationMode(MenuItem item) {
        if (mIsStepActive) {
            abstractChangeAnimationMode(item, R.drawable.ic_action_fast_forward, "Fast forward mode activated");
        } else {
            abstractChangeAnimationMode(item, R.drawable.ic_action_play, "Step by step mode activated");
        }
    }

    private void abstractChangeAnimationMode(MenuItem item, int drawableResource, String message) {
        mIsStepActive = !mIsStepActive;
        item.setIcon(drawableResource);
        Snackbar.make(mFab, message, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.img_go)
    void animateAlgorithm() {
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
                    "This algorithm assumes that the graph is undirected",
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
