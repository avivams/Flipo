package com.flipo.avivams.flipo.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.flipo.avivams.flipo.R;

/**
 * Created by aviv_ams on 06/06/2018.
 */

public class InfoDialog extends DialogFragment {

    private String taskTitle, taskDescription, btnOkTxt;


    public static InfoDialog AssignmentDialogInstance(String btnOkTxt){
        InfoDialog ins = new InfoDialog();
        ins.btnOkTxt = btnOkTxt;

      //  ins.createView(parent, a);
        return ins;
    }


    @Override
    public void onResume() {
        super.onResume();
        Window window =  getDialog().getWindow();
        window.setLayout((int)getActivity().getResources().getDimension(R.dimen.dialog_info_width),
                (int)getActivity().getResources().getDimension(R.dimen.dialog_info_height));
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialog = inflater.inflate(R.layout.dialog_info, null);

        Button btn = dialog.findViewById(R.id.info_btn_ok);
        btn.setText(btnOkTxt);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        builder.setView(dialog);
        Dialog dlg = builder.create();
        Window window =  dlg.getWindow();
        try {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.getAttributes().windowAnimations = R.style.Dialog_PopUp;
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return dlg;
    }

}
