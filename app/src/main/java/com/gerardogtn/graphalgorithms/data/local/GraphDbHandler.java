package com.gerardogtn.graphalgorithms.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gerardogtn.graphalgorithms.data.model.Edge;
import com.gerardogtn.graphalgorithms.data.model.Node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by gerardogtn on 11/6/15.
 */
public class GraphDbHandler {

    public static final String TAG = GraphDbHandler.class.getSimpleName();
    private SQLiteDatabase mDatabase;

    public GraphDbHandler(Context context) {
        DbOpenHelper databaseHelper = new DbOpenHelper(context.getApplicationContext());
        mDatabase = databaseHelper.getWritableDatabase();
    }

    public synchronized LinkedList<Node> getNodes() {
        LinkedList<Node> result = new LinkedList<>();

        Cursor cursor = mDatabase.query(NodeDbSchema.TABLE_NAME, NodeDbSchema.nodeColumns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Node node = new Node(cursor.getInt(1));
                node.setX(cursor.getFloat(2));
                node.setY(cursor.getFloat(3));
                node.setVisited(cursor.getInt(4) == 1);
                node.setActive(cursor.getInt(5) == 1);
                result.push(node);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return result;
    }

    public synchronized LinkedList<Edge> getEdges() {
        LinkedList<Edge> result = new LinkedList<>();

        Cursor cursor = mDatabase.query(EdgeDbSchema.TABLE_NAME, EdgeDbSchema.edgeColumns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int originId = cursor.getInt(1);
                int destinationId = cursor.getInt(2);
                int weight = cursor.getInt(3);
                boolean isDirected = cursor.getInt(4) == 1;

                Edge edge = new Edge(originId, destinationId, weight, isDirected);
                edge.setActive(cursor.getInt(5) == 1);
                result.push(edge);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return result;
    }

    public synchronized void clearNodes() {
        mDatabase.delete(NodeDbSchema.TABLE_NAME, null, null);
    }

    public synchronized void clearEdges() {
        mDatabase.delete(EdgeDbSchema.TABLE_NAME, null, null);
    }


    public synchronized void writeNodes(LinkedList<Node> nodes) {
        Iterator<Node> iterator = nodes.descendingIterator();

        while (iterator.hasNext()) {
            Node node = iterator.next();
            ContentValues values = new ContentValues();
            values.put(NodeDbSchema.COLUMN_DATA, node.getData());
            values.put(NodeDbSchema.COLUMN_X_POSITION, node.getX());
            values.put(NodeDbSchema.COLUMN_Y_POSITION, node.getY());
            values.put(NodeDbSchema.COLUMN_IS_ACTIVE, node.isActive());
            values.put(NodeDbSchema.COLUMN_IS_VISITED, node.wasVisited());

            mDatabase.insert(NodeDbSchema.TABLE_NAME, null, values);
        }
    }

    public synchronized void writeEdges(Set<Edge> edges) {
        Iterator<Edge> iterator = edges.iterator();
        Edge edge;
        while (iterator.hasNext()){
            edge = iterator.next();
            ContentValues values = new ContentValues();
            values.put(EdgeDbSchema.COLUMN_ORIGIN_NODE, edge.getOrigin().getId());
            values.put(EdgeDbSchema.COLUMN_DESTINATION_NODE, edge.getDestination().getId());
            values.put(EdgeDbSchema.COLUMN_WEIGHT, edge.getWeight());
            values.put(EdgeDbSchema.COLUMN_IS_DIRECTED, edge.isDirected());
            values.put(EdgeDbSchema.COLUMN_IS_ACTIVE, edge.isActive());

            mDatabase.insert(EdgeDbSchema.TABLE_NAME, null, values);
        }

    }
}
