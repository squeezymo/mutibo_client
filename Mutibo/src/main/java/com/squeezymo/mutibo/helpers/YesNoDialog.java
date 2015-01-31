package com.squeezymo.mutibo.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.squeezymo.mutibo.R;

public abstract class YesNoDialog {
    private Context mContext;
    private String mMessage;

    public YesNoDialog(Context context, String message) {
        mContext = context;
        mMessage = message;
    }

    public abstract void yesClicked();
    public abstract void noClicked();

    public void issue() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        yesClicked();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        noClicked();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mMessage)
                .setPositiveButton(mContext.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(mContext.getString(R.string.no), dialogClickListener)
                .show();
    }

}
