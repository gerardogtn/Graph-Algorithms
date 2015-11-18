package com.gerardogtn.graphalgorithms.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gerardogtn.graphalgorithms.R;
import com.gerardogtn.graphalgorithms.ui.activity.MainActivity;

/**
 * Created by gerardogtn on 11/3/15.
 */
public class AddEdgeDialog extends DialogFragment {

    private TextInputLayout mInputLayout;
    private TextView mTextView;
    private OnCreateEdgeListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();
        View root = LayoutInflater.from(activity).inflate(R.layout.dialog_text_input, null);

        mTextView = (TextView) root.findViewById(R.id.txt_dialog);
        mInputLayout = (TextInputLayout) root.findViewById(R.id.itxt_dialog);

        final AlertDialog dialog =  new AlertDialog.Builder(activity)
                .setView(root)
                .setTitle("Add Edge")
                .setPositiveButton(android.R.string.ok, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button mPositiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                mPositiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String input = mTextView.getText().toString();
                        if (TextUtils.isEmpty(input)) {
                            mListener.onCreateEdge(0);
                            dialog.dismiss();
                        } else if (input.length() > 4) {
                            mInputLayout.setError("Weight must be less than 9999");
                        } else {
                            mListener.onCreateEdge(Integer.parseInt(input));
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    public void setOnClickListener(OnCreateEdgeListener listener) {
        this.mListener = listener;
    }

    public interface OnCreateEdgeListener {
        void onCreateEdge(int weight);
    }
}
