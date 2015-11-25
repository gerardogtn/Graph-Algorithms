package com.gerardogtn.graphalgorithms.data.local;

/**
 * Created by gerardogtn on 11/6/15.
 */
public class NodeDbSchema {

    public static final String TABLE_NAME = "nodes";

    public static final String COLUMN_ID         = "id";
    public static final String COLUMN_DATA       = "data";
    public static final String COLUMN_X_POSITION = "x";
    public static final String COLUMN_Y_POSITION = "y";
    public static final String COLUMN_IS_ACTIVE  = "active";
    public static final String COLUMN_IS_VISITED = "visited";

    public static final String nodeColumns[] = new String[]{
            NodeDbSchema.COLUMN_ID,
            NodeDbSchema.COLUMN_DATA,
            NodeDbSchema.COLUMN_X_POSITION,
            NodeDbSchema.COLUMN_Y_POSITION,
            NodeDbSchema.COLUMN_IS_VISITED,
            NodeDbSchema.COLUMN_IS_ACTIVE
    };

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID         + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " +
                    COLUMN_DATA       + " INTEGER NOT NULL, " +
                    COLUMN_X_POSITION + " REAL NOT NULL, " +
                    COLUMN_Y_POSITION + " REAL NOT NULL, " +
                    COLUMN_IS_ACTIVE  + " INTEGER NOT NULL, " +
                    COLUMN_IS_VISITED + " INTEGER NOT NULL" +
                    " ); ";

}
