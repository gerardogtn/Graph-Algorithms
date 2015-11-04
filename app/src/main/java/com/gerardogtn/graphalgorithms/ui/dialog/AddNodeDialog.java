package com.gerardogtn.graphalgorithms.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.gerardogtn.graphalgorithms.R;

/**
 * Created by gerardogtn on 11/3/15.
 */
public class AddNodeDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_text_input, null);

        return new AlertDialog.Builder(context)
                .setView(v)
                .setTitle("Add node")
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

}
