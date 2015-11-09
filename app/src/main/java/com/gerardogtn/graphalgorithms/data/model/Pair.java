package com.gerardogtn.graphalgorithms.data.model;

public class Pair<First, Second extends Number> implements Comparable<Pair>{

    private First first;
    private Second second;

    public Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public First getFirst() {
        return first;
    }

    public void setFirst(First first) {
        this.first = first;
    }

    public Second getSecond() {
        return second;
    }

    public void setSecond(Second second) {
        this.second = second;
    }

    @Override
    public int compareTo(Pair pair) {
        return (int) (this.second.floatValue() - pair.second.floatValue());
    }
}