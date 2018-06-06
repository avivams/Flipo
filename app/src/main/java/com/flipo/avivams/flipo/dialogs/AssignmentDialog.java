package com.flipo.avivams.flipo.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flipo.avivams.flipo.R;

/**
 * Created by אבי on 06/06/2018.
 */

public class AssignmentDialog extends DialogFragment {

    private String taskTitle, taskDescription, btnOkTxt;

    public static AssignmentDialog AssignmentDialogInstance(String taskTitle, String taskDescription, String btnOkTxt){
        AssignmentDialog ins = new AssignmentDialog();
        ins.taskDescription = taskDescription;
        ins.btnOkTxt = btnOkTxt;
        ins.taskTitle = taskTitle;
        return ins;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialog = inflater.inflate(R.layout.dialog_assignment, null);

        ((TextView)dialog.findViewById(R.id.txt_assignment_title)).setText(taskTitle);
        ((TextView)dialog.findViewById(R.id.txt_assignment_description)).setText(taskDescription);

        Button btn = dialog.findViewById(R.id.btn_assignment_ok);
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
