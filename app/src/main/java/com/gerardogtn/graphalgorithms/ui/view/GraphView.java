package com.gerardogtn.graphalgorithms.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.gerardogtn.graphalgorithms.data.local.GraphDbHandler;
import com.gerardogtn.graphalgorithms.data.model.Edge;
import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.data.model.Node;

import java.util.Iterator;
import java.util.LinkedList;


/**
 * Created by gerardogtn on 11/1/15.
 */
public class GraphView extends View implements Graph.OnGraphUpdateListener, Runnable {

    public static final String TAG = GraphView.class.getSimpleName();

    public static final int BACKGROUND_COLOR = 0xFFF8EFE0;
    public static final int NODE_TEXT_COLOR = Color.WHITE;
    public static final int EDGE_TEXT_COLOR = 0xFFFF4081;

    private int mIndex = 0;
    private boolean mWasMoved = false;
    private boolean mIsDialogActive = false;

    private Paint mNodePaint;
    private Paint mNodeVisitedPaint;
    private Paint mEdgePaint;
    private Paint mEdgeActivePaint;
    private Paint mBackgroundPaint;
    private Paint mNodeTextPaint;
    private Paint mEdgeTextPaint;

    private Graph mGraph;
    private Node mCurrentNode;
    private Node mPreviousNode;

    private ShowDialogListener insertListener;
    private Context mContext;
    private GraphDbHandler mDbHandler;

    public GraphView(Context context) {
        this(context, null);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context.getApplicationContext();
        mGraph = Graph.getInstance(false);
        mGraph.setOnGraphUpdateListener(this);
        mDbHandler = new GraphDbHandler(mContext);
        setUpPaints();
        loadGraph();
    }

    public void loadGraph() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mGraph.addNodesReverse(mDbHandler.getNodes());
                mGraph.addEdges(mDbHandler.getEdges());
            }
        });
        thread.start();
        redraw();
    }

    public void saveGraph(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mDbHandler.clearNodes();
                mDbHandler.writeNodes(mGraph.getNodes());
                mDbHandler.clearEdges();
                mDbHandler.writeEdges(mGraph.getEdges());
            }
        });
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);
        drawEdges(canvas);
        drawNodes(canvas);
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

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mDbHandler.writeNode(node);
            }
        });
        thread.start();

        mGraph.addNode(node);
        mCurrentNode = null;
        invalidate();
    }

    public void setNodes(LinkedList<Node> nodes){
        for (Node node : nodes){
            mGraph.addNode(node);
        }
    }

    public void setEdges(LinkedList<Edge> edges){
        for (Edge edge : edges){
            mGraph.addEdge(edge);
        }
    }

    public void setEventListener(ShowDialogListener edgeListener) {
        this.insertListener = edgeListener;
    }

    private void setUpPaints() {
        setUpNodePaint();
        setUpEdgePaint();
        setUpBackgroundPaint();
        setUpTextPaint();
    }

    private void setUpTextPaint() {
        setUpNodeTextPaint();
        setUpEdgeTextPaint();
    }

    private void setUpNodeTextPaint() {
        mNodeTextPaint = new Paint();
        mNodeTextPaint.setColor(NODE_TEXT_COLOR);
        mNodeTextPaint.setStyle(Paint.Style.FILL);
        mNodeTextPaint.setTextSize(24);
        mNodeTextPaint.setAntiAlias(true);
    }

    private void setUpEdgeTextPaint() {
        mEdgeTextPaint = new Paint();
        mEdgeTextPaint.setColor(EDGE_TEXT_COLOR);
        mEdgeTextPaint.setStyle(Paint.Style.FILL);
        mEdgeTextPaint.setTextSize(24);
        mEdgeTextPaint.setAntiAlias(true);
    }

    private void setUpBackgroundPaint() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(BACKGROUND_COLOR);
    }

    private void setUpEdgePaint() {
        mEdgePaint = new Paint();
        mEdgePaint.setColor(Color.BLACK);
        mEdgePaint.setStyle(Paint.Style.STROKE);
        mEdgePaint.setStrokeWidth(5);
        mEdgePaint.setAntiAlias(true);

        mEdgeActivePaint = new Paint();
        mEdgeActivePaint.setColor(Color.RED);
        mEdgeActivePaint.setStyle(Paint.Style.STROKE);
        mEdgeActivePaint.setStrokeWidth(5);
        mEdgeActivePaint.setAntiAlias(true);
    }

    private void setUpNodePaint() {
        mNodePaint = new Paint();
        mNodePaint.setColor(Node.COLOR);
        mNodePaint.setAntiAlias(true);
        mNodeVisitedPaint = new Paint();
        mNodeVisitedPaint.setColor(Node.COLOR_VISITED);
        mNodeVisitedPaint.setAntiAlias(true);
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Draws nodes to screen from first added to last added.
    private void drawNodes(Canvas canvas) {
        Iterator<Node> iterator = mGraph.getNodes().descendingIterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            Paint paint = (node.wasVisited() ? mNodeVisitedPaint : mNodePaint);

            canvas.drawCircle(node.getX(),
                    node.getY(),
                    Node.RADIUS,
                    paint);

            canvas.drawText(String.valueOf(node.getId()), node.getX(), node.getY(), mNodeTextPaint);
        }
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Draws edges.
    private void drawEdges(Canvas canvas) {
        for (Edge edge : mGraph.getEdges()){
            Node origin = edge.getOrigin();
            Node destination = edge.getDestination();

            Paint paint = (edge.isActive() ? mEdgeActivePaint : mEdgePaint);
            canvas.drawLine(origin.getX(), origin.getY(), destination.getX(), destination.getY(), paint);

            int increment = 20;
            if (destination.getX() < origin.getX() &&
                    Math.abs(destination.getY() - origin.getY()) < Node.RADIUS_SQUARED ) {
                increment = -20;
            }

            canvas.drawText(edge.getWeight() + ""
                    , (origin.getX() + destination.getX()) / 2 + increment
                    , (origin.getY() + destination.getY()) / 2 + increment
                    , mEdgeTextPaint);
        }
    }

    // REQUIRES: None.
    // MODIFIES: this.
    // EFFECTS:  Updates mCurrentNode position and redraws.
    private void moveNode(PointF current) {
        if (current.x > getWidth() || current.x < 0 || current.y > getHeight() || current.y < 0){

        } else {
            mCurrentNode.updatePosition(current);
            mWasMoved = true;
        }
        invalidate();
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


    // REQUIRES: None
    // MODIFIES: this
    // EFFECTS:  Initializes mIndex, and starts a new thread.
    public void executeAlgorithm(int index){
        mIndex = index;
        Thread thread = new Thread(this);
        thread.start();
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Executes algorithm if  0 <= mIndex <= 7, else throws IllegalArgumentException.
    @Override
    public void run() {

        try {
            if (mIndex == 0) {
                Log.d(TAG, "Opcion: " + mIndex + " no implementada");
            } else if (mIndex == 1) {
                mGraph.dfs();
            } else if (mIndex == 2) {
                mGraph.bfs();
            } else if (mIndex == 3) {
                Log.d(TAG, "Opcion: " + mIndex + " no implementada");
            } else if (mIndex == 4) {
                Log.d(TAG, "Opcion: " + mIndex + " no implementada");
            } else if (mIndex == 5) {
                Log.d(TAG, "Opcion: " + mIndex + " no implementada");
            } else if (mIndex == 6) {
                Log.d(TAG, "Opcion: " + mIndex + " no implementada");
            } else if (mIndex == 7) {
                Log.d(TAG, "Opcion: " + mIndex + " no implementada");
            } else {
                throw new IllegalArgumentException();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public void clearVisited() {
        mGraph.clearVisited();
        invalidate();
    }

    public void clearNodes() {
        mDbHandler.clearNodes();
        mGraph.clearNodes();
        Node.resetCounter();
        redraw();
    }

    public interface ShowDialogListener {
        void showEdgeDialog();

        void showNodeDialog();
    }


}
