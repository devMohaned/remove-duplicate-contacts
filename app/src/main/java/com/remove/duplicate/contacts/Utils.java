package com.remove.duplicate.contacts;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public  class Utils {
    public static void showAlertDialog(final Context mContext, String title, String message, String posBtnText
            , DialogInterface.OnClickListener posBtnListener
            , String negBtnText, DialogInterface.OnClickListener negBtnListener) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(posBtnText, posBtnListener)
                    .setNegativeButton(negBtnText, negBtnListener)
                    .setCancelable(true)
                    .setIcon(android.R.drawable.stat_sys_warning);
            alertDialogBuilder.show();


    }
}
