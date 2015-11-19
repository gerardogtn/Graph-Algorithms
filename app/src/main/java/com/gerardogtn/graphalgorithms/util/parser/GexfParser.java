package com.gerardogtn.graphalgorithms.util.parser;//package com.gerardogtn.graphalgorithms.util.parser;

import com.gerardogtn.graphalgorithms.data.model.Edge;
import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.data.model.Node;
import com.gerardogtn.graphalgorithms.util.exception.NodeIdNotFoundException;
import com.gerardogtn.graphalgorithms.util.exception.ParseGexfException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by gerardogtn on 11/18/15.
 */
public class GexfParser extends DefaultHandler {

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

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (!parseStarted){
            if (qName.toLowerCase().equals("gexf")){
                parseStarted = true;
            } else {
                throw new ParseGexfException("Format not supported");
            }
        }else if (qName.toLowerCase().equals("node")) {
            Node node = new Node(0);
            node.setId(parseNodeOrEdge(attributes.getValue("id")));
            nodes.add(node);
            if (nodes.size() > 50){
                throw new ParseGexfException("There are too many nodes");
            }
        } else if (qName.toLowerCase().equals("edge")) {
            int originId = parseNodeOrEdge(attributes.getValue("source"));
            int targetId = parseNodeOrEdge(attributes.getValue("target"));
            Edge edge;
            if (attributes.getValue("weight") != null) {
                float weight = Float.parseFloat(attributes.getValue("weight"));
                edge = new Edge(getNodeById(originId), getNodeById(targetId), (int) weight, isDirected);
            } else {
                edge = new Edge(getNodeById(originId), getNodeById(targetId), 0, isDirected);
            }
            edges.add(edge);

        } else if (qName.toLowerCase().equals("graph")){
            isDirected = (attributes.getValue("defaultedgetype").equals("directed"));
        }

    }

    private int parseNodeOrEdge(String node) {
        try {
            return Integer.parseInt(node);
        } catch (NumberFormatException e) {
            return Integer.parseInt(node.substring(1));
        }
    }

    private Node getNodeById(int id){
        for (Node node : nodes){
            if (node.getId() == id){
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
    @Override
    public void endDocument() throws SAXException {
        if (nodes.size() < 50){
            Collections.sort(nodes);
            Graph.clearGraph();
            Graph.addNodes(nodes);
            Graph.addEdges(edges);
        } else {
            throw new ParseGexfException("There are too many nodes");
        }
    }
}
