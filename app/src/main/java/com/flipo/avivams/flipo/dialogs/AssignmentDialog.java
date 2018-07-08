package com.flipo.avivams.flipo.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.flipo.avivams.flipo.R;

/**
 * Created by aviv_ams on 06/06/2018.
 */

public class AssignmentDialog extends DialogFragment {

    private String taskTitle, taskDescription, btnOkTxt;


    public static AssignmentDialog AssignmentDialogInstance(String taskTitle, String taskDescription, String btnOkTxt){
        AssignmentDialog ins = new AssignmentDialog();
        ins.taskDescription = taskDescription;
        ins.btnOkTxt = btnOkTxt;
        ins.taskTitle = taskTitle;
      //  ins.createView(parent, a);
        return ins;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialog = inflater.inflate(R.layout.deprec_dialog_assignment, null);

        ((TextView)dialog.findViewById(R.id.txt_assignment_title)).setText(taskTitle);
        TextView description =dialog.findViewById(R.id.txt_assignment_description);
        description.setText(taskDescription);
        description.setMovementMethod(new ScrollingMovementMethod());

        Button btn = dialog.findViewById(R.id.btn_assignment_ok);
        btn.setText(btnOkTxt);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        dialog.setMinimumWidth((int)activity.getResources().getDimension(R.dimen.dialog_assignment_width));
        dialog.setMinimumHeight((int)activity.getResources().getDimension(R.dimen.dialog_assignment_height));

        builder.setView(dialog);
        Dialog dlg = builder.create();
        Window window =  dlg.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.Dialog_PopUp;

        return dlg;
    }

}
