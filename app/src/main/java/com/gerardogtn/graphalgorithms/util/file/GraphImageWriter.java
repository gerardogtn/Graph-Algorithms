package com.gerardogtn.graphalgorithms.util.file;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;

import com.gerardogtn.graphalgorithms.ui.view.GraphView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by gerardogtn on 11/30/15.
 */
public class GraphImageWriter {

    private Paint mBackgroundPaint;
    private GraphView mGraph;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private boolean mShowConnections;

    public GraphImageWriter(GraphView graphView, boolean showConnections) {
        mGraph = graphView;
        mShowConnections = showConnections;
        mBitmap = Bitmap.createBitmap(graphView.getWidth(),
                graphView.getHeight(),
                Bitmap.Config.RGB_565);
        mCanvas = new Canvas(mBitmap);
    }


    public boolean writeImage(){
        drawToCanvas();

        File file = new File(FileConstants.GRAPH_IMAGE_PATH);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(mGraph, "Couldn't save image", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void drawToCanvas() {
        mCanvas.drawPaint(mBackgroundPaint);
        mGraph.drawEdges(mCanvas);
        if (mShowConnections) {
            mGraph.drawConnections(mCanvas);
        }
        mGraph.drawNodes(mCanvas);
    }
}
