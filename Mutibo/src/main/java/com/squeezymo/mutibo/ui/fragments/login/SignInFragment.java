package com.squeezymo.mutibo.ui.fragments.login;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.squeezymo.mutibo.R;
import com.squeezymo.mutibo.client.RestfulClient;
import com.squeezymo.mutibo.client.SecuredRestException;
import com.squeezymo.mutibo.helpers.ProgressCircularDialog;
import com.squeezymo.mutibo.ui.activites.CallbackHandler;

public class SignInFragment extends Fragment {
    private static final String LOG_TAG = SignInFragment.class.getCanonicalName();

    private CallbackHandler mCallback;
    private Handler mHandler;
    private ProgressCircularDialog mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_screen, null);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (CallbackHandler) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + CallbackHandler.class.getCanonicalName());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final Button logInBtn = (Button) getView().findViewById(R.id.btn_log_in);
        final EditText loginFld = (EditText) getView().findViewById(R.id.field_login);
        final EditText passwordFld = (EditText) getView().findViewById(R.id.field_password);
        final TextView errMsgFld = (TextView) getView().findViewById(R.id.field_errmsg);

        final SharedPreferences prefs = getActivity().getSharedPreferences(RestfulClient.USER_PREFS, Context.MODE_PRIVATE);
        loginFld.setText( prefs.getString(RestfulClient.LOGIN_PREF, "") );
        passwordFld.setText( prefs.getString(RestfulClient.PASSWORD_PREF, "") );

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread( new Runnable() {
                    @Override
                    public void run() {
                        logInBtn.setClickable(false);

                        mProgress = new ProgressCircularDialog(getActivity());
                        mProgress.start();

                        try{ Thread.sleep(1000); } catch(Exception e) {} // TODO imitate business

                        boolean isNewlyAuthorized = false;

                        try {
                            RestfulClient client = RestfulClient.getInstance();
                            isNewlyAuthorized = client.signIn(loginFld.getText().toString(), passwordFld.getText().toString());
                            if ( isNewlyAuthorized ) {
                                SharedPreferences.Editor prefEditor = prefs.edit();
                                prefEditor.putString(RestfulClient.LOGIN_PREF, loginFld.getText().toString());
                                prefEditor.putString(RestfulClient.PASSWORD_PREF, passwordFld.getText().toString());
                                prefEditor.commit();

                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getActivity(), getActivity().getString(R.string.loggedIn), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                mHandler.sendEmptyMessage(SecuredRestException.AUTHENTICATION_FAILURE);
                            }
                        }
                        catch (SecuredRestException sre) {
                            mHandler.sendEmptyMessage(sre.getExceptionType());
                        }
                        catch (Exception e) {
                            mHandler.sendEmptyMessage(SecuredRestException.DEFAULT);
                        }
                        finally {
                            logInBtn.setClickable(true);
                            mProgress.dismiss();
                        }

                        if (isNewlyAuthorized)
                            mCallback.pass(Activity.RESULT_OK);

                    }
                }).start();
            }
        });

        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case SecuredRestException.AUTHENTICATION_FAILURE:
                        errMsgFld.setText(getString(R.string.authenticationFailureException));
                        errMsgFld.setVisibility(View.VISIBLE);
                        break;
                    case SecuredRestException.CREDENTIAL_MISSING:
                        errMsgFld.setText(getString(R.string.credentialMissingException));
                        errMsgFld.setVisibility(View.VISIBLE);
                        break;
                    case SecuredRestException.NO_CONNECTION:
                        errMsgFld.setText(getString(R.string.noConnectionException));
                        errMsgFld.setVisibility(View.VISIBLE);
                        break;
                    case SecuredRestException.USER_EXISTS:
                        errMsgFld.setText(getString(R.string.userExists));
                        errMsgFld.setVisibility(View.VISIBLE);
                        break;
                    case SecuredRestException.DEFAULT:
                    default:
                        errMsgFld.setText(getString(R.string.unknownException));
                        errMsgFld.setVisibility(View.VISIBLE);
                        break;
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgress != null) mProgress.dismiss();
    }
}
