package com.example.shareride;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

public class CommanClass {
    public static ProgressDialog progressBar(Context context, ProgressDialog progressDialog, String message)
    {
        progressDialog = new ProgressDialog(context,R.style.CustomProgressDialog);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        return progressDialog;
    }

    public static void dialog(final Context context, AlertDialog alertDialog, String title, String message, final Class activityToOpen)
    {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, activityToOpen);
                        context.startActivity(intent);
                    }
                });
        alertDialog = builder.create();
        final AlertDialog finalAlertDialog = alertDialog;
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                finalAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
        });
        finalAlertDialog.show();
    }
}
