package com.flipo.avivams.flipo.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.flipo.avivams.flipo.R;

/**
 * Created by aviv_ams on 16/05/2018.
 */

public class ChooseDialog extends DialogFragment {

    private String promptMsg, btnOkTxt;

    public static ChooseDialog chooseDialogInstance(String promptMsg, String btnOkTxt){
        ChooseDialog ins = new ChooseDialog();
        ins.promptMsg = promptMsg;
        ins.btnOkTxt = btnOkTxt;
        return ins;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialog = inflater.inflate(R.layout.dialog_choose, null);
        ((TextView)dialog.findViewById(R.id.txt_prompt)).setText(promptMsg);

        Button btn = dialog.findViewById(R.id.btn_gotit);
        btn.setText(btnOkTxt);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(dialog);
        Dialog d = builder.create();
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.getWindow().getAttributes().windowAnimations = R.style.Dialog_PopUp;
        return d;
    }


}
