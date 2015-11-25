package com.gerardogtn.graphalgorithms.data.local;

/**
 * Created by gerardogtn on 11/6/15.
 */
public class EdgeDbSchema {
    public static final String TABLE_NAME = "graphs";

    public static final String COLUMN_ID               = "id";
    public static final String COLUMN_ORIGIN_NODE      = "origin_node";
    public static final String COLUMN_DESTINATION_NODE = "destination_node";
    public static final String COLUMN_WEIGHT           = "weight";
    public static final String COLUMN_IS_DIRECTED      = "is_directed";
    public static final String COLUMN_IS_ACTIVE        = "is_active";

    public static final String edgeColumns[] = new String[]{
            EdgeDbSchema.COLUMN_ID,
            EdgeDbSchema.COLUMN_ORIGIN_NODE,
            EdgeDbSchema.COLUMN_DESTINATION_NODE,
            EdgeDbSchema.COLUMN_WEIGHT,
            EdgeDbSchema.COLUMN_IS_DIRECTED,
            EdgeDbSchema.COLUMN_IS_ACTIVE
    };

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ORIGIN_NODE      + " INTEGER NOT NULL, " +
                    COLUMN_DESTINATION_NODE + " INTEGER NOT NULL, " +
                    COLUMN_WEIGHT           + " INTEGER NOT NULL, " +
                    COLUMN_IS_DIRECTED      + " INTEGER NOT NULL, " +
                    COLUMN_IS_ACTIVE        + " INTEGER NOT NULL, " +

                    "FOREIGN KEY (" + COLUMN_ORIGIN_NODE + ") REFERENCES " +
                    NodeDbSchema.TABLE_NAME + "(" + NodeDbSchema.COLUMN_ID + "), " +

                    "FOREIGN KEY (" + COLUMN_DESTINATION_NODE + ") REFERENCES " +
                    NodeDbSchema.TABLE_NAME + "(" + NodeDbSchema.COLUMN_ID + ")" +
                    " ); ";

}
