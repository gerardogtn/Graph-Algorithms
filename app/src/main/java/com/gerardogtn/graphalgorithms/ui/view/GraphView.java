package com.gerardogtn.graphalgorithms.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gerardogtn.graphalgorithms.data.local.GraphDbHandler;
import com.gerardogtn.graphalgorithms.data.model.Edge;
import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.data.model.Node;
import com.gerardogtn.graphalgorithms.ui.activity.MainActivity;
import com.gerardogtn.graphalgorithms.util.constant.Color;
import com.gerardogtn.graphalgorithms.util.file.FileConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;


/**
 * Created by gerardogtn on 11/1/15.
 */
public class GraphView extends View implements Graph.OnGraphUpdateListener, Runnable {

    public static final String TAG = GraphView.class.getSimpleName();

    public static final int BACKGROUND_COLOR = Color.BEIGE;
    public static final int NODE_TEXT_COLOR = Color.WHITE;
    public static final int EDGE_TEXT_COLOR = Color.PINK;

    private int mIndex = 0;

    private boolean mWasMoved = false;
    private boolean mIsDialogActive = false;
    private boolean mShowConnections = false;

    private Paint mBackgroundPaint;

    private Paint mNodePaint = new Paint();
    private Paint mNodeActivePaint = new Paint();
    private Paint mNodeVisitedPaint = new Paint();
    private Paint mNodeTextPaint = new Paint();

    private Paint mEdgePaint = new Paint();
    private Paint mEdgeActivePaint = new Paint();
    private Paint mEdgeIdlePaint = new Paint();
    private Paint mEdgeTextPaint = new Paint();
    private Paint mEdgeIdleTextPaint = new Paint();
    private Paint mEdgeArrowPaint = new Paint();

    private Paint mConnectionPaint = new Paint();
    private Paint mTextConnectionPaint = new Paint();

    private Graph mGraph;
    private Node mCurrentNode;
    private Node mPreviousNode;

    private OnEventListener insertListener;
    private GraphDbHandler mDbHandler;
    private OnStopAnimationListener mStopListener;


    public GraphView(Context context) {
        this(context, null);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Context mContext = context.getApplicationContext();
        mGraph = Graph.getInstance(false);
        mGraph.setOnGraphUpdateListener(this);
        mDbHandler = new GraphDbHandler(mContext);
        setUpPaints();
        loadGraph();
    }


    private void setUpPaints() {
        setUpBackgroundPaint();
        setUpNodePaints();
        setUpEdgePaint();
        setUpConnectionPaint();
    }

    private void setUpBackgroundPaint() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(BACKGROUND_COLOR);
    }

    private void setUpNodePaints() {
        setUpAbstractNodePaint(mNodePaint, Node.COLOR);
        setUpAbstractNodePaint(mNodeVisitedPaint, Node.COLOR_VISITED);
        setUpAbstractNodePaint(mNodeActivePaint, Node.COLOR_ACTIVE);
        setUpAbstractTextPaint(mNodeTextPaint, NODE_TEXT_COLOR);
    }

    private void setUpAbstractNodePaint(Paint paint, int color) {
        paint.setColor(color);
        paint.setAntiAlias(true);
    }

    private void setUpAbstractTextPaint(Paint paint, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(24);
        paint.setAntiAlias(true);
    }

    private void setUpEdgePaint() {
        setUpAbstractEdgePaint(mEdgePaint, Color.BLACK);
        setUpAbstractEdgePaint(mEdgeActivePaint, Color.RED);
        setUpAbstractEdgePaint(mEdgeIdlePaint, Color.BLUE_GRAY);
        setUpAbstractTextPaint(mEdgeTextPaint, EDGE_TEXT_COLOR);
        setUpAbstractTextPaint(mEdgeIdleTextPaint, Color.BLUE_GRAY);
        setUpAbstractTextPaint(mEdgeArrowPaint, Color.BLACK);
    }

    private void setUpAbstractEdgePaint(Paint paint, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
    }

    private void setUpConnectionPaint() {
        setUpAbstractEdgePaint(mConnectionPaint, Color.PURPLE);
        setUpAbstractTextPaint(mTextConnectionPaint, Color.PURPLE);
    }

    // ASSUMES: If graph is directed all edges are directed.
    // REQUIRES: None.
    // MODIFIES: Database.
    // EFFECTS: Clear graph database and set to values in graph singleton
    public void loadGraph() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Graph.addNodesReverse(mDbHandler.getNodes());
                Graph.addEdges(mDbHandler.getEdges());
                if (!mDbHandler.getEdges().isEmpty()) {
                    Graph.setDirected(mDbHandler.getEdges().get(0).isDirected());
                }
                redraw();
            }
        });
        thread.start();
    }

    // REQUIRES: None.
    // MODIFIES: Database.
    // EFFECTS: Clear graph singleton and set to values in database.
    public void saveGraph() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mDbHandler.clearEdges();
                mDbHandler.writeEdges(mGraph.getEdges());
                mDbHandler.clearNodes();
                mDbHandler.writeNodes(mGraph.getNodes());
            }
        });
        thread.start();
    }

    public void setEventListener(OnEventListener edgeListener) {
        this.insertListener = edgeListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);
        drawEdges(canvas);
        if (mShowConnections) {
            drawConnections(canvas);
        }
        drawNodes(canvas);
    }


    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Draws nodes to screen from first added to last added.
    private void drawNodes(Canvas canvas) {
        Iterator<Node> iterator = mGraph.getNodes().descendingIterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            Paint paint = (node.wasVisited() ? mNodeVisitedPaint : mNodePaint);
            paint = (node.isActive() ? mNodeActivePaint : paint);

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
        for (Edge edge : mGraph.getEdges()) {
            Node origin = edge.getOrigin();
            Node destination = edge.getDestination();

            Paint paint = (edge.isActive() ? mEdgeActivePaint : mEdgePaint);
            paint = (edge.isIdle() ? mEdgeIdlePaint : paint);

            canvas.drawLine(origin.getX(), origin.getY(), destination.getX(), destination.getY(), paint);
            if (edge.isDirected()) {
                Point circle = getPosPoint(origin.getX(), origin.getY(), destination.getX(), destination.getY());
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(circle.x, circle.y,20, paint);
            }
            if (!edge.isIdle()) {
                drawTextOnLine(canvas, edge.getWeight() + "", mEdgeTextPaint,
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
            }
            else {
                point.y = (int) y1 + 50;
            }
        }
        else {
            float m = (y1-y0)/(x1-x0);
            float b = y0 - m * x0;
            float totalLength = (float) Math.sqrt(Math.pow((y1-y0), 2)+Math.pow((x1-x0), 2));
            float xEval = (float) (totalLength - 50) * (float) Math.cos(Math.atan((y1-y0)/(x1-x0)));
            point.x = (int) (x0 - xEval);
            if (x0 < x1) {
                if (!(x0 < point.x && point.x < x1)) point.x = (int) (x0 + xEval);
            }
            else {
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
    private void drawConnections(Canvas canvas) {
        for (Node node : mGraph.getNodes()) {
            Node parent = node.getParent();
            if (node.getDistance() != Integer.MAX_VALUE && parent != null && parent.getId() != node.getId()) {
                canvas.drawLine(node.getX(), node.getY(), parent.getX(), parent.getY(), mConnectionPaint);

                drawTextOnLine(canvas, node.getDistance() + "", mTextConnectionPaint,
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
        mGraph.addNode(node);
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
        mIndex = index;
        Thread thread = new Thread(this);
        thread.start();
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Executes appropiate algorithm if  0 <= mIndex <= 5, else if mIndex =  6 throws
    // IllegalArgumentException, else trows IndexOutOfBoundsException.
    @Override
    public void run() {

        int index = mIndex + 1;

        try {
            if (index == 1) {
                mGraph.dfs();
            } else if (index == 2) {
                mGraph.bfs();
            } else if (index == 3) {
                mGraph.prim();
            } else if (index == 4) {
                mGraph.kruskal();
            } else if (index == 5) {
                mGraph.dijkstra();
            } else if (index == 6) {
                mGraph.bellmanFord();
            } else if (index == 7) {
                throw new IllegalArgumentException("Floyd Warshall is not implemented in GraphView");
            } else {
                throw new IndexOutOfBoundsException();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        mStopListener.stopAnimation(true);
    }

    public void resetGraph() {
        mGraph.reset();
        invalidate();
    }

    public void clearNodes() {
        mDbHandler.clearNodes();
        mGraph.clearNodes();
        Node.resetCounter();
    }

    public void clearEdges() {
        mDbHandler.clearEdges();
        mGraph.clearEdges();
    }

    public void setOnStopListener(MainActivity onStopListener) {
        this.mStopListener = onStopListener;
    }

    // REQUIRES: None.
    // MODIFIES: this.
    // EFFECTS:  Saves graphview as JPEG, if save was unsuccesful return false, otherwise true.
    public boolean writeGraphImage() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawPaint(mBackgroundPaint);
        drawEdges(canvas);
        if (mShowConnections) {
            drawConnections(canvas);
        }
        drawNodes(canvas);

        File file = new File(FileConstants.GRAPH_IMAGE_PATH);

        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(this, "Couldn't save image", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public interface OnEventListener {
        void showEdgeDialog();

        void showNodeDialog();
    }

    public interface OnStopAnimationListener {
        void stopAnimation(boolean showClearButton);
    }


    private static class SaveBitmapTask implements Runnable {

        private static SaveBitmapTask mTask;

        private SaveBitmapTask() {

        }

        public static SaveBitmapTask getInstance() {
            if (mTask == null) {
                mTask = new SaveBitmapTask();
            }
            return mTask;
        }

        @Override
        public void run() {

        }
    }
}
