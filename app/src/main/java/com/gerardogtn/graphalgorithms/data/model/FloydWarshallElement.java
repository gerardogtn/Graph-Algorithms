package com.gerardogtn.graphalgorithms.data.model;

/**
 * Created by gerardogtn on 11/16/15.
 */
public class FloydWarshallElement {

    private int mValue;
    private boolean mIsActive;
    private boolean mIsHeader;
    private boolean mIsModified;

    public FloydWarshallElement(int value, boolean isActive, boolean isHeader) {
        this.mValue = value;
        this.mIsActive = isActive;
        this.mIsHeader = isHeader;
        mIsModified = false;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        this.mValue = value;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public void setIsActive(boolean isActive) {
        this.mIsActive = isActive;
    }

    public boolean isHeader() {
        return mIsHeader;
    }

    public void setIsHeader(boolean isHeader) {
        this.mIsHeader = isHeader;
    }

    public boolean isModified() {
        return mIsModified;
    }

    public void setModified(boolean isModified) {
        this.mIsModified = isModified;
    }
}
