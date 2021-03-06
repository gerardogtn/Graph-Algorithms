package com.gerardogtn.graphalgorithms.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gerardogtn.graphalgorithms.data.local.GraphDbHandler;
import com.gerardogtn.graphalgorithms.data.model.Edge;
import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.data.model.Node;
import com.gerardogtn.graphalgorithms.ui.activity.MainActivity;
import com.gerardogtn.graphalgorithms.util.constant.GraphViewPaint;
import com.gerardogtn.graphalgorithms.util.file.GraphImageWriter;
import com.gerardogtn.graphalgorithms.util.task.AlgorithmAnimationTask;
import com.gerardogtn.graphalgorithms.util.task.DatabaseHandlerTask;

import java.util.Iterator;


/**
 * Created by gerardogtn on 11/1/15.
 */
public class GraphView extends View implements Graph.OnGraphUpdateListener {

    public static final String TAG = GraphView.class.getSimpleName();

    private boolean mWasMoved = false;
    private boolean mIsDialogActive = false;
    private boolean mShowConnections = false;

    private Graph mGraph;
    private Node mCurrentNode;
    private Node mPreviousNode;

    public static float sDensity;
    private OnEventListener insertListener;
    private GraphDbHandler mDbHandler;
    private OnStopAnimationListener mStopListener;
    private DatabaseHandlerTask mDatabaseTask;
    private AlgorithmAnimationTask mAlgorithmAnimationTask;


    public GraphView(Context context) {
        this(context, null);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Context mContext = context.getApplicationContext();
        mGraph = Graph.getInstance(true);
        mGraph.setOnGraphUpdateListener(this);
        mDbHandler = new GraphDbHandler(mContext);
        sDensity = getResources().getDisplayMetrics().density;
        mDatabaseTask = new DatabaseHandlerTask(this, mGraph, mDbHandler);
        mAlgorithmAnimationTask = new AlgorithmAnimationTask(mGraph, 0, mStopListener);
        loadGraph();
    }

    public void setIsDialogActive(boolean isDialogActive) {
        mIsDialogActive = isDialogActive;
    }

    // ASSUMES: If graph is directed all edges are directed.
    // REQUIRES: None.
    // MODIFIES: Database.
    // EFFECTS: Clear graph database and set to values in graph singleton
    public void loadGraph() {
        mDatabaseTask.setLoad();
        Thread thread = new Thread(mDatabaseTask);
        thread.start();
    }

    // REQUIRES: None.
    // MODIFIES: Database.
    // EFFECTS: Clear graph singleton and set to values in database.
    public void writeGraph() {
        mDatabaseTask.setWrite();
        Thread thread = new Thread(mDatabaseTask);
        thread.start();
    }

    public void setEventListener(OnEventListener edgeListener) {
        this.insertListener = edgeListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(GraphViewPaint.BACKGROUND);
        drawEdges(canvas);
        if (mShowConnections) {
            drawConnections(canvas);
        }
        drawNodes(canvas);
    }


    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Draws nodes to screen from first added to last added.
    public void drawNodes(Canvas canvas) {
        Iterator<Node> iterator = mGraph.getNodes().descendingIterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            Paint paint = (node.wasVisited() ? GraphViewPaint.NODE_VISITED : GraphViewPaint.NODE);
            paint = (node.isActive() ? GraphViewPaint.NODE_ACTIVE : paint);

            canvas.drawCircle(node.getX(),
                    node.getY(),
                    Node.RADIUS * sDensity,
                    paint);

            canvas.drawText(String.valueOf(node.getId()), node.getX(), node.getY(), GraphViewPaint.NODE_TEXT);
        }
    }


    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Draws edges.
    public void drawEdges(Canvas canvas) {
        for (Edge edge : mGraph.getEdges()) {
            Node origin = edge.getOrigin();
            Node destination = edge.getDestination();

            Paint paint = (edge.isActive() ? GraphViewPaint.EDGE_ACTIVE : GraphViewPaint.EDGE);
            paint = (edge.isIdle() ? GraphViewPaint.EDGE_IDLE : paint);

            canvas.drawLine(origin.getX(), origin.getY(), destination.getX(), destination.getY(), paint);
            if (edge.isDirected()) {
                Point circle = getPosPoint(origin.getX(), origin.getY(), destination.getX(), destination.getY());
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(circle.x, circle.y, 10 * sDensity, paint);
            }
            if (!edge.isIdle()) {
                drawTextOnLine(canvas, edge.getWeight() + "", GraphViewPaint.EDGE_TEXT,
                        origin.getX(), destination.getX(),
                        origin.getY(), destination.getY());
            }

        }
    }

    private Point getPosPoint(float x0, float y0, float x1, float y1) {

        Point point = new Point();

        if (Math.abs(x1 - x0) < 40) {
            point.x = (int) x1;
            if (y0 < y1) {
                point.y = (int) y1 - 50;
            } else {
                point.y = (int) y1 + 50;
            }
        } else {
            float m = (y1 - y0) / (x1 - x0);
            float b = y0 - m * x0;
            float totalLength = (float) Math.sqrt(Math.pow((y1 - y0), 2) + Math.pow((x1 - x0), 2));
            float xEval = (totalLength - 50) * (float) Math.cos(Math.atan((y1 - y0) / (x1 - x0)));
            point.x = (int) (x0 - xEval);
            if (x0 < x1) {
                if (!(x0 < point.x && point.x < x1)) point.x = (int) (x0 + xEval);
            } else {
                if (!(x1 < point.x && point.x < x0)) point.x = (int) (x0 + xEval);
            }
            point.y = (int) (m * point.x + b);
        }
        return point;
    }

    // REQUIRES: None.
    // MODIFIES: canvas.
    // EFFECTS : If node has a parent not null and not equal to itself and with a distance less than
    // max, then draw a line connecting node to parent with a text showing its distance.
    public void drawConnections(Canvas canvas) {
        for (Node node : mGraph.getNodes()) {
            Node parent = node.getParent();
            if (node.getDistance() != Integer.MAX_VALUE && parent != null && parent.getId() != node.getId()) {
                canvas.drawLine(node.getX(), node.getY(), parent.getX(), parent.getY(), GraphViewPaint.CONNECTION);

                drawTextOnLine(canvas, node.getDistance() + "", GraphViewPaint.CONNECTION_TEXT,
                        node.getX(), parent.getX(),
                        node.getY(), parent.getY());
            }
        }
    }

    // REQUIRES: None.
    // MODIFIES: canvas.
    // EFFECTS : Draws text on perpendicular to line with offset.
    private void drawTextOnLine(Canvas canvas, String text, Paint paint,
                                float x0, float x1,
                                float y0, float y1) {

        int offset = 30;
        float length = (float) Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
        float xLen = x0 - x1;
        float yLen = y0 - y1;
        float xMid = (x0 + x1) / 2;
        float yMid = (y0 + y1) / 2;

        canvas.drawText(text, xMid + yLen * offset / length, yMid - xLen * offset / length, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mWasMoved = false;
                handleNewEdge(current);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentNode != null && !mIsDialogActive) {
                    moveNode(current);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mWasMoved) {
                    mCurrentNode = null;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mCurrentNode = null;
                break;
        }

        return true;
    }

    // REQUIRES: None.
    // MODIFIES: this.
    // EFFECTS: Adds node to mNodes and redraw.
    public void addNode(final Node node) {
        Graph.addNode(node);
        mCurrentNode = null;
        invalidate();
    }


    // REQUIRES: None.
    // MODIFIES: this.
    // EFFECTS:  Updates mCurrentNode position and redraws.
    private void moveNode(PointF current) {
        boolean cantMove = current.x > getWidth() || current.x < 0 ||
                current.y > getHeight() || current.y < 0;
        if (!cantMove) {
            mCurrentNode.updatePosition(current);
            mWasMoved = true;
            invalidate();
        }
    }

    // REQUIRES: None.
    // MODIFIES: this.
    // EFFECTS:  Sets up attributes and shows edge dialog if necessary.
    private void handleNewEdge(PointF point) {
        mPreviousNode = mCurrentNode;
        mCurrentNode = mGraph.findNextNode(point);
        setUpEdgeDialog();
    }

    // REQUIRES: insertListener is not null.
    // MODIFIES: this.
    // EFFECTS:  If mCurrentNode and mPreviousNode are not null and are not equal to each other,
    // then display AddEdgeDialog.
    private void setUpEdgeDialog() {
        if (mCurrentNode != null && mPreviousNode != null && !mCurrentNode.equals(mPreviousNode)) {
            insertListener.showEdgeDialog();
            mIsDialogActive = true;
        }
    }

    // ASSUMES:  mCurrentNode is not null and mPreviousNode is not null.
    // MODIFIES: this.
    // EFFECTS:  Adds edge to GraphView and redraws.
    public void addEdge(int weight) {
        mGraph.addEdge(mPreviousNode, mCurrentNode, weight);
        mCurrentNode = null;
        mPreviousNode = null;
        invalidate();
        mIsDialogActive = false;
    }

    @Override
    public void redraw() {
        this.post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    @Override
    public void connectNodes() {
        this.mShowConnections = true;
        redraw();
    }


    // REQUIRES: None
    // MODIFIES: this
    // EFFECTS:  Initializes mIndex, and starts a new thread.
    public void executeAlgorithm(int index) {
        mAlgorithmAnimationTask.setIndex(index);
        Thread thread = new Thread(mAlgorithmAnimationTask);
        thread.start();
    }


    public void resetGraph() {
        mGraph.reset();
        invalidate();
    }

    public void clearNodes() {
        mDbHandler.clearNodes();
        Graph.clearNodes();
        Node.resetCounter();
    }

    public void clearEdges() {
        mDbHandler.clearEdges();
        Graph.clearEdges();
    }

    public void setOnStopListener(MainActivity onStopListener) {
        this.mStopListener = onStopListener;
        mAlgorithmAnimationTask.setListener(onStopListener);
    }

    // REQUIRES: None.
    // MODIFIES: this.
    // EFFECTS:  Saves graphview as JPEG, if save was unsuccesful return false, otherwise true.
    public boolean writeGraphImage() {
        GraphImageWriter imageWriter = new GraphImageWriter(this, mShowConnections);
        return imageWriter.writeImage();
    }

    public interface OnEventListener {
        void showEdgeDialog();

        void showNodeDialog();
    }

    public interface OnStopAnimationListener {
        void stopAnimation(boolean showClearButton);
    }

}
