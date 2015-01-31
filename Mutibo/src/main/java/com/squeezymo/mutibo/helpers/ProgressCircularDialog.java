package com.squeezymo.mutibo.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Looper;
import com.squeezymo.mutibo.R;

public class ProgressCircularDialog extends Thread {
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private boolean mTerminated;

    public ProgressCircularDialog(Context context) {
        mContext = context;
    }

    @Override
    public void run() {
        Looper.prepare();

        mTerminated = false;

        mProgressDialog = new ProgressDialog(mContext, R.style.TransparentProgressDialogStyle);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        if ( !mTerminated )
            Looper.loop();
    }

    public void dismiss() {
        if ( mTerminated )
            return;

        if ( mProgressDialog != null )
            mProgressDialog.dismiss();

        mTerminated = true;
    }
}