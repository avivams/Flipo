package com.flipo.avivams.flipo.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.flipo.avivams.flipo.R;

/**
 * Created by aviv_ams on 17/05/2018.
 */

public class YesNoDialog extends DialogFragment {

    private String question, btnYesTxt, btnCancelTxt;
    private DialogMatcher.ResultYesNoListener listener;


    public static YesNoDialog yesNoDialogInstance(String question, String btnYesTxt, String btnCancelTxt, DialogMatcher.ResultYesNoListener listener){
        YesNoDialog ins = new YesNoDialog();
        ins.question = question;
        ins.btnYesTxt = btnYesTxt;
        ins.btnCancelTxt = btnCancelTxt;
        ins.listener = listener;
        return ins;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window =  getDialog().getWindow();
        window.setLayout((int)getActivity().getResources().getDimension(R.dimen.dialog_prompt_width),
                (int)getActivity().getResources().getDimension(R.dimen.dialog_prompt_height));
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialog = inflater.inflate(R.layout.dialog_yesno, null);
        TextView v = dialog.findViewById(R.id.txt_question);
        v.setText(question);
        Button yes = dialog.findViewById(R.id.btn_yes);
        yes.setText(btnYesTxt);
        yes.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.resultOk();
                dismiss();
            }
        });

        Button cancelBtn = dialog.findViewById(R.id.btn_no);
        cancelBtn.setText(btnCancelTxt);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.resultCancel();
                getDialog().cancel();
            }
        });

        builder.setView(dialog);
        Dialog d = builder.create();
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.getWindow().getAttributes().windowAnimations = R.style.Dialog_PopUp;
        return d;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        listener = null;
        super.onDismiss(dialog);
    }

}
