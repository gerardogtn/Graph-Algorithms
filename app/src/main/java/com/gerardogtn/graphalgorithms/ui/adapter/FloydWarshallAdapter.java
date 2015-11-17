package com.gerardogtn.graphalgorithms.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gerardogtn.graphalgorithms.R;
import com.gerardogtn.graphalgorithms.data.model.FloydWarshallElement;
import com.gerardogtn.graphalgorithms.data.model.Graph;
import com.gerardogtn.graphalgorithms.data.model.Node;

import java.util.LinkedList;

/**
 * Created by gerardogtn on 11/16/15.
 */
public class FloydWarshallAdapter extends RecyclerView.Adapter<FloydWarshallAdapter.FloydWarshallViewHolder> {

    private LinkedList<FloydWarshallElement> mElements;
    private LayoutInflater mInflater;
    private Activity mActivity;
    private NotifyDataSetChangedOnUiTask mTask;
    private static int mActiveColor;

    public FloydWarshallAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        setUpElements();
        mActiveColor = context.getResources().getColor(R.color.colorAccent);
        mActivity = ((Activity) context);
        mTask = new NotifyDataSetChangedOnUiTask();
    }

    private void setUpElements() {
        int[][] adjacencyMatrix = Graph.getAdjacencyMatrix();
        mElements = new LinkedList<>();

        mElements.add(new FloydWarshallElement(-1, false, true));
        for (int i = 1; i <= Graph.getNodesSize(); i++) {
            mElements.add(new FloydWarshallElement(i, false, true));
        }

        for (int i = 0; i < Graph.getNodesSize(); i++) {
            mElements.add(new FloydWarshallElement(i + 1, false, true));
            for (int j = 0; j < Graph.getNodesSize(); j++) {
                mElements.add(new FloydWarshallElement(adjacencyMatrix[i][j], false, false));
            }
        }
    }

    public void updateElement(int row, int column, int value){
        FloydWarshallElement current = mElements.get(matchPosition(row, column));
        current.setValue(value);
        mActivity.runOnUiThread(mTask);
    }

    public void makeModified(int row, int column, boolean isModified) {
        FloydWarshallElement current = mElements.get(matchPosition(row, column));
        current.setModified(isModified);
        mActivity.runOnUiThread(mTask);
    }

    public void makeRowActive(int position, boolean isActive){
        int count = 0;
        int size = Graph.getNodesSize();
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                if (i == position){
                    count++;
                    mElements.get((i+1) * (size + 1) + (j+ 1)).setIsActive(isActive);
                }
            }
        }
        mActivity.runOnUiThread(mTask);
    }


    // REQUIRES: None.
    // MODIFIES: None.
    // EFFECTS : Matches a position in a n-1 x n - 1 matrix to the right lower matrix of n x n size.
    private int matchPosition(int row, int column){
        int size = Graph.getNodesSize();
        return (size + 1) * (row + 1) + (column + 1);
    }

    public void makeColumnActive(int position, boolean isActive){
        int size = Graph.getNodesSize();
        for (int i = 0; i < size; i++){
            for (int j = 0; j <=size; j++){
                if (j == position){
                    mElements.get((i+1) * (size + 1) + (j+ 1)).setIsActive(isActive);
                }
            }
        }

        mActivity.runOnUiThread(mTask);
    }


    @Override
    public FloydWarshallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_floyd_warshall, parent, false);
        return new FloydWarshallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FloydWarshallViewHolder holder, int position) {
        FloydWarshallElement element = mElements.get(position);
        holder.setValue(element.getValue());
        holder.setActive(element.isActive());
        holder.setHeader(element.isHeader());
        holder.setModified(element.isModified());
    }

    @Override
    public int getItemCount() {
        return mElements.size();
    }

    public class FloydWarshallViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public FloydWarshallViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.txt_element);
        }

        // REQUIRES: None.
        // MODIFIES: this.
        // EFFECTS:  Sets mTextView.text to value.
        public void setValue(int value) {
            if (value == Node.MAX_VALUE) {
                mTextView.setText(R.string.inf);
            } else if (value == -1) {
                mTextView.setText("");
            } else {
                mTextView.setText(String.format("%d", value));
            }
        }

        // REQUIRES: None.
        // MODIFIES: this.
        // EFFECTS:  Sets mTextView.background color to mActiveColor.
        public void setActive(boolean isActive) {
            if (isActive) {
                mTextView.setBackgroundColor(mActiveColor);
            } else {
                mTextView.setBackgroundColor(Color.WHITE);
            }
        }

        // ASSUMES: Is called after setActive.
        // REQUIRES: None.
        // MODIFIES: this.
        // EFFECTS:  Sets mTextView.background color to mActiveColor.
        public void setModified(boolean isModified){
            if (isModified){
                mTextView.setBackgroundColor(Color.CYAN);
            }
        }

        // ASSUMES: Is called after setActive.
        // REQUIRES: None.
        // MODIFIES: this.
        // EFFECTS:  Sets mTextView.background color to gray.
        public void setHeader(boolean isHeader) {
            if (isHeader) {
                mTextView.setBackgroundColor(Color.GRAY);
                mTextView.setTextColor(Color.WHITE);
            }
        }

    }

    private class NotifyDataSetChangedOnUiTask implements Runnable {

        public NotifyDataSetChangedOnUiTask() {

        }

        @Override
        public void run() {
            notifyDataSetChanged();
        }
    }

}
