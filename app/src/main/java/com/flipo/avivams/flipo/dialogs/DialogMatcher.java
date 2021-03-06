package com.flipo.avivams.flipo.dialogs;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;

import com.flipo.avivams.flipo.R;

/**
 * Created by aviv_ams on 17/05/2018.
 */

public class DialogMatcher {

    public enum DoodlesDialogType {CHOOSE_SHAPE, CHOOSE_FREE_SHAPE, DRAW_PATH, DRAW_SHAPE_FIRST, DRAW_PATH_FIRST, CHOSE_EXIST_PATH,
                            CHOOSE_DELETE, DELETE_CHOSED, INFO_DIALOG};

    public enum PreviewDialogType {RECORD_RESULT};

    public static void showDialog(Context context, DoodlesDialogType type, FragmentTransaction transaction, ResultYesNoListener listener){

        switch(type){
            case CHOOSE_SHAPE:
                ChooseDialog.chooseDialogInstance(context.getString(R.string.prompt_choose_shape), context.getString(R.string.btn_ok_gotit))
                        .show(transaction, context.getString(R.string.dialog_tag_choose));
                break;

            case CHOOSE_FREE_SHAPE:
                ChooseDialog.chooseDialogInstance(context.getString(R.string.prompt_choose_free_shape), context.getString(R.string.btn_ok_alright))
                        .show(transaction, context.getString(R.string.dialog_tag_choose_free_shape));
                break;

            case CHOOSE_DELETE:
                ChooseDialog.chooseDialogInstance(context.getString(R.string.prompt_choose_to_erase), context.getString(R.string.btn_ok_alright))
                        .show(transaction, context.getString(R.string.dialog_tag_choose_delete));
                break;

            case DRAW_PATH:
                ChooseDialog.chooseDialogInstance(context.getString(R.string.prompt_draw_path), context.getString(R.string.btn_ok_alright))
                        .show(transaction, context.getString(R.string.dialog_tag_draw_path));
                break;

            case DRAW_SHAPE_FIRST:
                ChooseDialog.chooseDialogInstance(context.getString(R.string.prompt_draw_shape_first), context.getString(R.string.btn_ok))
                        .show(transaction, context.getString(R.string.dialog_tag_draw_shape_first));
                break;

            case DRAW_PATH_FIRST:
                ChooseDialog.chooseDialogInstance(context.getString(R.string.prompt_draw_path_first), context.getString(R.string.btn_ok))
                        .show(transaction, context.getString(R.string.dialog_tag_draw_path_first));
                break;

            case CHOSE_EXIST_PATH:
                YesNoDialog.yesNoDialogInstance(context.getString(R.string.quest_combine_exist_path), context.getString(R.string.btn_yes),
                        context.getString(R.string.btn_no), listener).show(transaction, context.getString(R.string.quest_combine_exist_path));
                break;

            case DELETE_CHOSED:
                YesNoDialog.yesNoDialogInstance(context.getString(R.string.quest_erase_selected), context.getString(R.string.btn_yes),
                        context.getString(R.string.btn_no), listener).show(transaction, context.getString(R.string.dialog_tag_choose_delete));
                break;

            case INFO_DIALOG:
                InfoDialog.AssignmentDialogInstance(context.getString(R.string.btn_ok)).show(transaction, context.getString(R.string.dialog_tag_assignment));
                break;
        }
    }


    public static void showDialog(Context context, PreviewDialogType type, FragmentTransaction transaction, @NonNull RecordResultDialogListener listener) {
        switch (type){
            case RECORD_RESULT:
                RecordCompletedDialog.RecordDialogInstance(listener).show(transaction, context.getString(R.string.dialog_record_result_tag));

                break;
        }
    }


    public interface ResultYesNoListener{
        void resultOk();
        void resultCancel();
    }


    public interface RecordResultDialogListener{

        void onConfirmButtonClicked(TabsDialogDeprec.TabType tabType, String userInput);
    }
}
