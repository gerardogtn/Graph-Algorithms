package com.gerardogtn.graphalgorithms.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gerardogtn.graphalgorithms.data.model.Edge;
import com.gerardogtn.graphalgorithms.data.model.Node;

import java.util.Iterator;
import java.util.LinkedList;


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

    private LinkedList<Node> mNodes;
    private Node mCurrentNode;


    public GraphView(Context context) {
        this(context, null);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mNodes = new LinkedList<>();

        mNodePaint = new Paint();
        mNodePaint.setColor(Node.COLOR);

        mEdgePaint = new Paint();
        mEdgePaint.setColor(TEXT_COLOR);
        mEdgePaint.setStyle(Paint.Style.STROKE);
        mEdgePaint.setStrokeWidth(5);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(BACKGROUND_COLOR);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);

        Iterator<Node> iterator = mNodes.descendingIterator();
        while (iterator.hasNext()){
            Node node = iterator.next();

            canvas.drawCircle(node.getX(),
                    node.getY(),
                    Node.RADIUS,
                    mNodePaint);
            canvas.drawText(String.valueOf(node.getId()), node.getX(), node.getY(), mTextPaint);


            for (Edge edge : node.edges){
                Node target = edge.getDestination();
                canvas.drawLine(node.getX(), node.getY(), target.getX(), target.getY(), mEdgePaint);
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mWasMoved = false;
                Node previousNode = mCurrentNode;
                mCurrentNode = findNode(current);
                connectNode(previousNode);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentNode != null){
                    mCurrentNode.updatePosition(current);
                    mWasMoved = true;
                    invalidate();
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
    // EFFECTS: Adds node to mNodes and redraws.
    public void addNode(Node node){
        mNodes.push(node);
        mCurrentNode = null;
        invalidate();
    }

    // REQUIRES: PointF is valid.
    // MODIFIES: None.
    // EFFECTS: Returns the first node found that encompasses PointF. Returns null if not found.
    @Nullable
    private Node findNode(PointF point){
        for (Node node : mNodes){
            boolean belongs = Math.pow(point.x - node.getX(), 2)
                    +  Math.pow(point.y - node.getY(), 2)
                    < Node.RADIUS_SQUARED;

            if (belongs){
                return node;
            }
        }
        return null;
    }


    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:If mCurrent and previous node are not equal and not null, creates an edge between them.
    // NOTE: Since the edges between nodes is a Set we don't have to worry for duplicate edges.
    private void connectNode(Node previousNode) {
        if (mCurrentNode != null && previousNode != null && !mCurrentNode.equals(previousNode)){
            mCurrentNode.addEdge(previousNode);
            previousNode.addEdge(mCurrentNode);
            mCurrentNode = null;
            invalidate();
        }
    }
}
