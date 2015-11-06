package com.gerardogtn.graphalgorithms.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import com.gerardogtn.graphalgorithms.R;
import com.gerardogtn.graphalgorithms.ui.activity.MainActivity;

/**
 * Created by gerardogtn on 11/3/15.
 */
public class AddEdgeDialog extends DialogFragment implements DialogInterface.OnClickListener{

    private TextView mTextView;
    private OnCreateEdgeListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();
        mTextView = (EditText) LayoutInflater.from(activity).inflate(R.layout.dialog_text_input, null);

        return new android.support.v7.app.AlertDialog.Builder(activity)
                .setView(mTextView)
                .setTitle("Add Edge")
                .setPositiveButton(android.R.string.ok, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        String input = mTextView.getText().toString();
        if (TextUtils.isEmpty(input)){
            mListener.onCreateEdge(0);
        } else {
            mListener.onCreateEdge(Integer.parseInt(input));
        }
    }

    public void setOnClickListener(OnCreateEdgeListener listener){
        this.mListener = listener;
    }

    public interface OnCreateEdgeListener{
        void onCreateEdge(int weight);
    }
}
