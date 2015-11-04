package com.gerardogtn.graphalgorithms.util;

/**
 * Created by gerardogtn on 11/3/15.
 */
public class NodeIdNotFoundException extends RuntimeException {

    public NodeIdNotFoundException(int id){
        super("Node with id: " + id + " was not found!");
    }

}
