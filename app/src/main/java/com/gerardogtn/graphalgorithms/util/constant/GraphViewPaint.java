package com.gerardogtn.graphalgorithms.util.constant;

import android.graphics.Paint;

import com.gerardogtn.graphalgorithms.data.model.Node;
import com.gerardogtn.graphalgorithms.ui.view.GraphView;

/**
 * Created by gerardogtn on 11/30/15.
 */
public class GraphViewPaint {

    public static final int BACKGROUND_COLOR = Color.BEIGE;
    public static final int NODE_TEXT_COLOR  = Color.WHITE;
    public static final int EDGE_TEXT_COLOR  = Color.PINK;

    public static final int TEXT_SIZE    = 14;
    public static final int STROKE_WIDTH = 5;

    public static final Paint BACKGROUND = getPaintWithColor(BACKGROUND_COLOR);

    public static final Paint NODE         = getPaintWithColor(Node.COLOR);
    public static final Paint NODE_TEXT    = getTextPaintWithColor(NODE_TEXT_COLOR);
    public static final Paint NODE_ACTIVE  = getPaintWithColor(Node.COLOR_ACTIVE);
    public static final Paint NODE_VISITED = getPaintWithColor(Node.COLOR_VISITED);

    public static final Paint EDGE         = getPaintWihColorAndStroke(Color.BLACK);
    public static final Paint EDGE_ACTIVE  = getPaintWihColorAndStroke(Color.RED);
    public static final Paint EDGE_IDLE    = getPaintWihColorAndStroke(Color.BLUE_GRAY);

    public static final Paint EDGE_TEXT      = getTextPaintWithColor(EDGE_TEXT_COLOR);
    public static final Paint EDGE_TEXT_IDLE = getTextPaintWithColor(Color.BLUE_GRAY);
    public static final Paint EDGE_ARROW     = getTextPaintWithColor(Color.BLACK);

    public static final Paint CONNECTION      = getPaintWihColorAndStroke(Color.PURPLE);
    public static final Paint CONNECTION_TEXT = getTextPaintWithColor(Color.PURPLE);


    private static Paint getPaintWithColor(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        return paint;
    }

    private static Paint getPaintWihColorAndStroke(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setAntiAlias(true);
        return paint;
    }

    private static Paint getTextPaintWithColor(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(TEXT_SIZE * GraphView.sDensity);
        paint.setAntiAlias(true);
        return paint;
    }

}
