package com.gerardogtn.graphalgorithms.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setUpSpinner();
        setUpGraphFragment();
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
        } else if (id == R.id.action_done){
            if (isAlgorithmActive){
                Snackbar.make(mFab, "Can't clear, an algorithm is being animated", Snackbar.LENGTH_SHORT).show();
            } else {
                mFragment.resetGraph();
                mFab.show();
            }
        } else if (id == R.id.action_clear){
            mFragment.clearGraph();
            mFab.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    void addNode(){
        mFragment.addNode(new Node(0));
    }

    @OnClick(R.id.img_go)
    void animateAlgorithm(){
        mFab.hide();
        isAlgorithmActive = true;
        mFragment.executeAlgorithm(mSpinner.getSelectedItemPosition());
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
    public void stopAnimation() {
        this.isAlgorithmActive = false;
    }
}
