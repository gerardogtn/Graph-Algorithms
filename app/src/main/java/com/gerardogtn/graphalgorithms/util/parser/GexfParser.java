package com.gerardogtn.graphalgorithms.util.parser;//package com.gerardogtn.graphalgorithms.util.parser;

import android.support.annotation.NonNull;

import com.gerardogtn.graphalgorithms.data.model.Edge;
import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.data.model.Node;
import com.gerardogtn.graphalgorithms.util.exception.NodeIdNotFoundException;
import com.gerardogtn.graphalgorithms.util.exception.ParseGexfException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedList;

/**
 * Created by gerardogtn on 11/18/15.
 */
public class GexfParser extends DefaultHandler {

    public static final String TAG = GexfParser.class.getSimpleName();
    public static final int MAX_NODES = 50;

    private StringBuffer accumulator;

    private boolean isDirected;
    private LinkedList<Node> nodes;
    private LinkedList<Edge> edges;
    private boolean parseStarted = false;

    public GexfParser() {
        nodes = new LinkedList<>();
        edges = new LinkedList<>();
        isDirected = false;
    }

    @Override
    public void startDocument() throws SAXException {
        accumulator = new StringBuffer();
    }

    // REQUIRES: None
    // MODIFIES: this.
    // EFFECTS:  If parse hasn't started, start parsing. Else handle parse.
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!parseStarted) {
            startGexfParse(qName);
        } else {
            parseGexf(qName, attributes);
        }
    }

    // REQUIRES: None
    // MODIFIES: this
    // EFFECTS:  If first tag is gexf, start parsing. Else throw ParseGexfException.
    // TODO: Change format not supported to an enum in exception.
    private void startGexfParse(@NonNull String qName) {
        if (qName.toLowerCase().equals(GexfConstants.GEXF)) {
            parseStarted = true;
        } else {
            throw new ParseGexfException("Format not supported");
        }
    }


    // REQUIRES: None
    // MODIFIES: this
    // EFFECTS:  Checks for appropiate parse and handles case.
    // TODO: Check usage of @Nonnull annotation.
    private void parseGexf(String qName, Attributes attributes) {
        if (qName.toLowerCase().equals(GexfConstants.GRAPH)) {
            parseGraphValues(attributes);
        } else if (qName.toLowerCase().equals(GexfConstants.NODE)) {
            parseNode(attributes);
        } else if (qName.toLowerCase().equals(GexfConstants.EDGE)) {
            parseEdge(attributes);
        }
    }

    // REQUIRES: attributes are graph attributes. Graph is clear.
    // MODIFIES: this.
    // EFFECTS:  If DEFAULT_EDGE_TYPE is directed, set Graph to directed else set graph to undirected.
    private void parseGraphValues(Attributes attributes) {
        isDirected = (attributes.getValue(GexfConstants.DEFAULT_EDGE_TYPE).equals(GexfConstants.DIRECTED));
        Graph.setDirected(isDirected);
    }

    // REQUIRES: attributes are node attributes.
    // MODIFIES: this
    // EFFECTS:  Parse values of a node, creates a node with such values and adds it to nodes. If
    // the size of nodes is greater than max nodes throws a ParseGexfException.
    private void parseNode(Attributes attributes) {
        Node node = new Node(0);
        node.setId(parseIntegerStringWithLeadingChar(attributes.getValue(GexfConstants.ID)));
        nodes.add(node);

        if (nodes.size() > MAX_NODES) {
            throw new ParseGexfException("There are too many nodes");
        }
    }

    // REQUIRES: attributes are edge attributes.
    // MODIFIES: this.
    // EFFECTS:  Parse values of an edge, creates an edge with such values and adds it to edges.
    private void parseEdge(Attributes attributes) {
        Edge current;

        int originId = parseIntegerStringWithLeadingChar(attributes.getValue(GexfConstants.SOURCE));
        int targetId = parseIntegerStringWithLeadingChar(attributes.getValue(GexfConstants.TARGET));
        current = parseWeightIfExists(attributes, originId, targetId);

        edges.add(current);
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  If weight exists creates a new weighted edge from node with originid to node with
    // targetId. Else creates a new weighted edge from node with originid to node with targetid with
    // weight 0.
    // TODO: Change weighted edge to unweighted edge.
    @NonNull
    private Edge parseWeightIfExists(Attributes attributes, int originId, int targetId) {
        Edge current;

        if (attributes.getValue(GexfConstants.WEIGHT) != null) {
            float weight = Float.parseFloat(attributes.getValue(GexfConstants.WEIGHT));
            current = new Edge(getNodeById(originId), getNodeById(targetId), (int) weight, isDirected);
        } else {
            current = new Edge(getNodeById(originId), getNodeById(targetId), 0, isDirected);
        }

        return current;
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Parses string with format "\a?\d*" to int.
    private int parseIntegerStringWithLeadingChar(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return Integer.parseInt(string.substring(1));
        }
    }

    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS:  Finds the node with id equal to id, if no such node was found throw
    // NodeIdNotFoundException.
    private Node getNodeById(int id) {
        for (Node node : nodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        throw new NodeIdNotFoundException(id);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        accumulator.setLength(0);
    }

    public void characters(char[] temp, int start, int length) {
        accumulator.append(temp, start, length);
    }


    // TODO: Check order of insertion.
    // TODO: Limit floyd warshall.
    // REQUIRES: None.
    // MODIFIES: Graph.
    // EFFECTS:  Clears Graph and sets this.nodes to Graph.nodes and this.edges to Graph.edges.
    @Override
    public void endDocument() throws SAXException {
        Graph.clearGraph();
        Graph.addNodes(nodes);
        Graph.addEdges(edges);
    }
}
