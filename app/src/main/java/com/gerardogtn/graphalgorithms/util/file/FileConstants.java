package com.gerardogtn.graphalgorithms.util.file;

import android.os.Environment;

import java.io.File;

/**
 * Created by gerardogtn on 11/17/15.
 */
public class FileConstants {

    public static final String GRAPH_IMAGE_NAME = "graph_image.jpeg";
    public static final String GRAPH_IMAGE_PATH = getPathToFile(GRAPH_IMAGE_NAME);


    public static final String GEXF_NAME = "graph.gexf";
    public static final String GEXF_PATH = getPathToFile(GEXF_NAME);

    public static String getPathToFile(String fileName){
        return Environment.getExternalStorageDirectory() + File.separator + fileName;
    }

}

