package com.gerardogtn.graphalgorithms.ui.activity;

import android.app.ProgressDialog;
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
import com.gerardogtn.graphalgorithms.util.exception.NodeIdNotFoundException;
import com.gerardogtn.graphalgorithms.util.exception.ParseGexfException;
import com.gerardogtn.graphalgorithms.util.file.FileConstants;
import com.gerardogtn.graphalgorithms.util.parser.GexfParser;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

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

    public static FloatingActionButton mFab;
    public static Menu mMenu;

    public static boolean isAlgorithmActive = false;
    private boolean mIsStepActive     = false;
    private boolean needsToBeCleared  = false;
    private static boolean isDirected = false;
    private static boolean mIsReadingFile = false;

    private ProgressDialog mProgressDialog;
    private Thread mLoadThread;
    private LoadGraphTask mLoadGraphTask;
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

        String type = getIntent().getType();
        if (Intent.ACTION_SEND.equals(getIntent().getAction()) && type != null){
            if (type.equals("text/xml")) {
                showProgressDialog();
            }
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        handleSendIntent(getIntent());
        return true;
    }

    private void handleSendIntent(Intent intent) {
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(intent.getAction()) && type != null){
            if (type.equals("text/xml")){
                try {
                    Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
                    mIsReadingFile = true;
                    assert uri != null;
                    mLoadGraphTask = new LoadGraphTask(getContentResolver().openInputStream(uri));
                    mLoadThread = new Thread(mLoadGraphTask);
                    mLoadThread.start();
                } catch (FileNotFoundException e) {
                    Snackbar.make(mFab, "Error reading file", Snackbar.LENGTH_SHORT).show();
                    e.printStackTrace();
                    if (mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                }
            }
        }
    }

    private void showProgressDialog() {
        mProgressDialog = ProgressDialog.show(this, "Importing graph",
                "Your graph will be ready shortly", true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_DIRECTED, isDirected);
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
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/xml");
        if (Graph.writeGexf()) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(FileConstants.GEXF_PATH)));
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

        int selectedAlgorithm = mSpinner.getSelectedItemPosition();

        if (selectedAlgorithm == 6 && Graph.getNodesSize() > 7){
            Snackbar.make(mFab, R.string.floyd_warshall_max, Snackbar.LENGTH_LONG).show();
            return;
        }
        Graph.setOnStopListener(this);
        Graph.setStepByStep(mIsStepActive);

        if (Graph.getEdgesSize() >= 1) {
            UpdateFabTask.setIsAlgorithmActive(true);
            updateFab();
            isAlgorithmActive = true;
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

    private class LoadGraphTask implements Runnable{

        private InputStream inputStream;

        public LoadGraphTask(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            writeGexf(inputStream);
            parseGexf();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                    mFragment.resetGraph();
                    mMenu.findItem(R.id.action_is_directed).setChecked(Graph.getDirected());
                }
            });
        }

        private void writeGexf(InputStream inputStream) {

            File file = new File(FileConstants.GEXF_PATH);
            OutputStream output = null;
            try {
                output = new FileOutputStream(file);
                try {
                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                    int read;

                    while ((read = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    Snackbar.make(mFab, "Error loading file", Snackbar.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void parseGexf(){
            try {
                XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
                GexfParser handler = new GexfParser();
                reader.setContentHandler(handler);
                reader.parse(Uri.fromFile(new File(FileConstants.GEXF_PATH)).toString());
            } catch (SAXException | ParserConfigurationException | IOException e) {
                Snackbar.make(mFab, "Error reading file :(", Snackbar.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (ParseGexfException | NodeIdNotFoundException e){
                Snackbar.make(mFab, "Error parsing", Snackbar.LENGTH_LONG).show();
                Graph.clearGraph();
            }
        }
    }



}
