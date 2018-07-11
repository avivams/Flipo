package com.flipo.avivams.flipo.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

import com.flipo.avivams.flipo.R;

/**
 * Created by aviv_ams on 10/07/2018.
 */

public class RecordCompletedDialog extends DialogFragment {

    private DialogMatcher.RecordResultDialogListener listener;


    public RecordCompletedDialog(){}

    @Override
    public void onResume() {
        super.onResume();
        Window window =  getDialog().getWindow();
        window.setLayout((int)getActivity().getResources().getDimension(R.dimen.preview_dialog_record_result_width),
                (int)getActivity().getResources().getDimension(R.dimen.preview_dialog_record_result_height));
    }

    public static RecordCompletedDialog RecordDialogInstance(@NonNull DialogMatcher.RecordResultDialogListener listener) {
        RecordCompletedDialog dialog = new RecordCompletedDialog();
        dialog.listener = listener;
        return dialog;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View rootview = getActivity().getLayoutInflater().inflate(R.layout.dialog_record_result, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        initListeners(rootview);

        builder.setView(rootview);
        Dialog dlg = builder.create();
        Window window =  dlg.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.Dialog_PopUp;

        return dlg;
    }



    @Override
    public void dismiss() {
        listener = null;
        super.dismiss();
    }


    private void initListeners(View rootview){

        rootview.findViewById(R.id.dialog_result_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
