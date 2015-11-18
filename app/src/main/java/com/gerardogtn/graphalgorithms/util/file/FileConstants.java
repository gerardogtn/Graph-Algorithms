package com.gerardogtn.graphalgorithms.util.file;

import android.os.Environment;

import java.io.File;

/**
 * Created by gerardogtn on 11/17/15.
 */
public class FileConstants {

    public static final String GRAPH_IMAGE_NAME = "graph_image.jpeg";
    public static final String GRAPH_IMAGE_PATH = Environment.getExternalStorageDirectory()
            + File.separator
            + GRAPH_IMAGE_NAME;
}
