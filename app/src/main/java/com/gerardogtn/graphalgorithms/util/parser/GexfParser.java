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


        if (qName.toLowerCase().equals("node")) {
            Node node = new Node(0);
            node.setId(parseNodeOrEdge(attributes.getValue("id")));
            nodes.add(node);
        } else if (qName.toLowerCase().equals("edge")) {
            int originId = parseNodeOrEdge(attributes.getValue("source"));
            int targetId = parseNodeOrEdge(attributes.getValue("target"));
            float weight = Float.parseFloat(attributes.getValue("weight"));
            Edge edge = new Edge(getNodeById(originId), getNodeById(targetId), (int) weight, isDirected);
            edges.add(edge);

        } else if (qName.toLowerCase().equals("graph")){
            isDirected = (attributes.getValue("defaultedgetype").equals("directed"));
        }
    }

    private boolean parseDirected(String edgedefault) {
        if (edgedefault.equals("directed")) {
            return true;
        } else if (edgedefault.equals("undirected")) {
            return false;
        }
        throw new IllegalArgumentException("Couldn't set graph type");
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
        if (qName.toLowerCase().equals("nodes")) {
            Collections.sort(nodes);
        }
        accumulator.setLength(0);
    }

    public void characters(char[] temp, int start, int length) {
        accumulator.append(temp, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        if (nodes.size() < 50){
            Graph.clearGraph();
            Graph.addNodesReverse(nodes);
            Graph.addEdges(edges);
        } else {
            throw new ParseGexfException("There are too many nodes");
        }
    }
}
