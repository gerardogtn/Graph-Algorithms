package com.gerardogtn.graphalgorithms.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.gerardogtn.graphalgorithms.R;
import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.data.model.Node;
import com.gerardogtn.graphalgorithms.ui.fragment.GraphFragment;
import com.gerardogtn.graphalgorithms.ui.view.GraphView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements GraphView.OnStopAnimationListener{

    public static final String TAG = MainActivity.class.getSimpleName();

    private GraphFragment mFragment;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.spnr_modes)
    Spinner mSpinner;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private boolean isAlgorithmActive = false;
    private boolean mIsStepActive = false;
    private boolean needsToBeCleared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpLayout();
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
        } else if (id == R.id.action_speed){
            if (mIsStepActive){
                mIsStepActive = false;
                item.setIcon(R.drawable.ic_action_fast_forward);
                Snackbar.make(mFab, "Fast forward mode activated", Snackbar.LENGTH_SHORT).show();
            } else {
                mIsStepActive = true;
                item.setIcon(R.drawable.ic_action_play);
                Snackbar.make(mFab, "Step by step mode activated", Snackbar.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_clear){
            mFragment.clearGraph();
            mFab.show();
        } else if (id == R.id.action_share){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");

//            ContentValues values = new ContentValues();
//            values.put(MediaStore.Images.Media.)
//            Uri uri = getContentResolver().
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    void fabClick(){
        if (!needsToBeCleared) {
            mFragment.addNode(new Node(0));
        } else {
            mFragment.resetGraph();
            mFab.setImageResource(R.drawable.ic_add_white_24dp);
            needsToBeCleared = false;
        }
    }

    @OnClick(R.id.img_go)
    void animateAlgorithm(){

        if (Graph.getEdgesSize() >= 1){
            mFab.hide();
            isAlgorithmActive = true;
            int selectedAlgorithm = mSpinner.getSelectedItemPosition();

            if (selectedAlgorithm == 2 || selectedAlgorithm == 3){
                Snackbar.make(mFab,
                        "This algorithm assumes that the graph is undirected",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
            mFragment.executeAlgorithm(selectedAlgorithm, mIsStepActive);
        } else {
            Snackbar.make(mFab, R.string.one_edge, Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public void stopAnimation(boolean showClearButton) {
        this.isAlgorithmActive = false;
        mFab.show();

        if (showClearButton){
            mFab.setImageResource(R.drawable.ic_action_done);
        } else {
            mFab.setImageResource(R.drawable.ic_add_white_24dp);
        }

        this.needsToBeCleared = showClearButton;
    }

}
