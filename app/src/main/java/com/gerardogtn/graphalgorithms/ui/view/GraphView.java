package com.gerardogtn.graphalgorithms.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gerardogtn.graphalgorithms.data.model.Edge;
import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.data.model.Node;

import java.util.Iterator;


/**
 * Created by gerardogtn on 11/1/15.
 */
public class GraphView extends View {

    public static final String TAG = GraphView.class.getSimpleName();

    public static final int BACKGROUND_COLOR = 0xFFF8EFE0;
    public static final int TEXT_COLOR = Color.BLACK;

    private boolean mWasMoved = false;

    private Paint mNodePaint;
    private Paint mEdgePaint;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;

    private Graph mGraph;
    private Node mCurrentNode;
    private Node mPreviousNode;

    private ShowDialogListener insertListener;


    public GraphView(Context context) {
        this(context, null);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGraph = Graph.getInstance(false);
        setUpPaints();
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

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mWasMoved = false;
                handleNewEdge(current);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentNode != null){
                    moveNode(current);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mWasMoved){
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
    public void addNode(Node node){
        mGraph.addNode(node);
        mCurrentNode = null;
        invalidate();
    }

    public void setEventListener(ShowDialogListener edgeListener){
        this.insertListener = edgeListener;
    }

    private void setUpPaints() {
        setUpNodePaint();
        setUpEdgePaint();
        setUpBackgroundPaint();
        setUpTextPaint();
    }

    private void setUpTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(20);
    }

    private void setUpBackgroundPaint() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(BACKGROUND_COLOR);
    }

    private void setUpEdgePaint() {
        mEdgePaint = new Paint();
        mEdgePaint.setColor(TEXT_COLOR);
        mEdgePaint.setStyle(Paint.Style.STROKE);
        mEdgePaint.setStrokeWidth(5);
        mEdgePaint.setAntiAlias(true);
    }

    private void setUpNodePaint() {
        mNodePaint = new Paint();
        mNodePaint.setColor(Node.COLOR);
        mNodePaint.setAntiAlias(true);
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Draws nodes to screen from first added to last added.
    private void drawNodes(Canvas canvas) {
        Iterator<Node> iterator = mGraph.getmNodes().descendingIterator();
        while (iterator.hasNext()){
            Node node = iterator.next();

            canvas.drawCircle(node.getX(),
                    node.getY(),
                    Node.RADIUS,
                    mNodePaint);
            canvas.drawText(String.valueOf(node.getId()), node.getX(), node.getY(), mTextPaint);
        }
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Draws edges.
    private void drawEdges(Canvas canvas) {
        for (Edge edge : mGraph.getmEdges()){
            Node origin = edge.getOrigin();
            Node destination = edge.getDestination();
            canvas.drawLine(origin.getX(), origin.getY(), destination.getX(), destination.getY(), mEdgePaint);
        }
    }

    // REQUIRES: None.
    // MODIFIES: this.
    // EFFECTS:  Updates mCurrentNode position and redraws.
    private void moveNode(PointF current) {
        mCurrentNode.updatePosition(current);
        mWasMoved = true;
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
        }
    }

    // ASSUMES:  mCurrentNode is not null and mPreviousNode is not null.
    // MODIFIES: this.
    // EFFECTS:  Adds edge to GraphView and redraws.
    public void addEdge(int weight){
        mGraph.addEdge(mPreviousNode, mCurrentNode, weight);
        mCurrentNode = null;
        mPreviousNode = null;
        invalidate();
    }

    public interface ShowDialogListener {
        void showEdgeDialog();
        void showNodeDialog();
    }

}
