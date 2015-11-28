package com.gerardogtn.graphalgorithms.util.file;

import com.gerardogtn.graphalgorithms.data.model.Edge;
import com.gerardogtn.graphalgorithms.data.model.Node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by gerardogtn on 11/27/15.
 */
public class GexfFileWriter {

    private boolean isDirected = false;
    private LinkedList<Node> mNodes;
    private Set<Edge> mEdges;

    public GexfFileWriter(boolean isDirected, LinkedList<Node> nodes, Set<Edge> edges) {
        this.isDirected = isDirected;
        this.mNodes = nodes;
        this.mEdges = edges;
    }

    // REQUIRES: None.
    // MODIFIES: File at GEXF_PATH.
    // EFFECTS : Returns true if exporting mNodes and mEdges to gexf format was succesful, false
    // otherwise.
    public boolean writeGexf(){
        File file = new File(FileConstants.GEXF_PATH);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(GexfFileConstants.XML_HEADER);
            writer.write(GexfFileConstants.GEXF_HEADER);
            writeGraphType(writer);
            writeNodes(writer);
            writeEdges(writer);
            writer.write(GexfFileConstants.GRAPH_CLOSE);
            writer.write(GexfFileConstants.GEXF_CLOSE);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }
        return true;
    }

    // REQUIRES: None.
    // MODIFIES: File at GEXF_PATH
    // EFFECTS : Writes graph type in gexf format.
    private void writeGraphType(BufferedWriter writer) throws IOException {
        if (isDirected){
            writer.write(String.format(GexfFileConstants.GRAPH, GexfFileConstants.GRAPH_DIRECTED));
        } else {
            writer.write(String.format(GexfFileConstants.GRAPH, GexfFileConstants.GRAPH_UNDIRECTED));
        }
    }

    // REQUIRES: None.
    // MODIFIES: File at GEXF_PATH
    // EFFECTS : Writes nodes in gexf format.
    private void writeNodes(BufferedWriter writer) throws IOException {
        writer.write(GexfFileConstants.NODES_OPEN_HEADER);

        Iterator<Node> iterator = mNodes.descendingIterator();
        while (iterator.hasNext()) {
            writer.write(String.format(GexfFileConstants.NODE, iterator.next().getId()));
        }

        writer.write(GexfFileConstants.NODES_CLOSE_HEADER);
    }

    // REQUIRES: None.
    // MODIFIES: File at GEXF_PATH
    // EFFECTS : Writes edges in gexf format.
    private void writeEdges(BufferedWriter writer) throws IOException {
        writer.write(GexfFileConstants.EDGES_OPEN_HEADER);
        int i = 1;
        for (Edge edge : mEdges) {
            writer.write(String.format(GexfFileConstants.EDGE,
                    i++,
                    String.valueOf(isDirected),
                    edge.getOrigin().getId(),
                    edge.getDestination().getId(),
                    edge.getWeight()));
        }
        writer.write(GexfFileConstants.EDGES_CLOSE_HEADER);
    }
}
