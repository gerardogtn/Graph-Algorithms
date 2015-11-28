package com.gerardogtn.graphalgorithms.util.file;

/**
 * Created by gerardogtn on 11/27/15.
 */
public class GexfFileConstants {

    public static final String XML_HEADER  = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    public static final String GEXF_HEADER = "<gexf xmlns=\"http://www.gexf.net/1.2draft\" version=\"1.2\">\n";
    public static final String GEXF_CLOSE  = "</gexf>\n";

    public static final String GRAPH   = "  <graph mode=\"static\" defaultedgetype=\"%s\">\n";
    public static final String GRAPH_DIRECTED = "directed";
    public static final String GRAPH_UNDIRECTED = "undirected";
    public static final String GRAPH_CLOSE      = "  </graph>\n";

    public static final String NODES_OPEN_HEADER  = "    <nodes>\n";
    public static final String NODE               = "      <node id=\"%d\"/>\n";
    public static final String NODES_CLOSE_HEADER = "    </nodes>\n";

    public static final String EDGES_OPEN_HEADER  = "    <edges>\n";
    public static final String EDGE = "      <edge id=\"e%d\" directed=\"%s\" source=\"%d\" target=\"%d\" weight=\"%d\"/>\n";
    public static final String EDGES_CLOSE_HEADER = "    </edges>\n";
}
