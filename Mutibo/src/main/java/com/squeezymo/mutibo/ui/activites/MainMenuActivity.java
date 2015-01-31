package com.squeezymo.mutibo.ui.activites;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.squeezymo.mutibo.R;
import com.squeezymo.mutibo.client.RestfulClient;
import com.squeezymo.mutibo.helpers.YesNoDialog;
import com.squeezymo.mutibo.model.User;

public class MainMenuActivity extends Activity {
    private static final String LOG_TAG = MainMenuActivity.class.getCanonicalName();
    public static final String START_ADDER_ACTIVITY_EXTRA = "start_adder";
    private static final int SIGN_IN_REQUEST = 1;

    private Button startGameBtn;
    private Button logInBtn;
    private Button quitGameBtn;
    private Button addBtn;
    private TextView signedInAsFld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_starter);

        startGameBtn = (Button) findViewById(R.id.btn_start_game);
        addBtn = (Button) findViewById(R.id.btn_add);
        logInBtn = (Button) findViewById(R.id.btn_log_in);
        quitGameBtn = (Button) findViewById(R.id.btn_quit);
        signedInAsFld = (TextView) findViewById(R.id.signedAsInfo);

        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, QuizzActivity.class));
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = RestfulClient.getInstance().getUser();
                if (!RestfulClient.getInstance().isAuthorized() || user == null || !user.getAuthorityNames().contains(User.ROLE_USER)) {
                    new YesNoDialog(MainMenuActivity.this, MainMenuActivity.this.getString(R.string.singInAddingWarning)) {
                        @Override
                        public void yesClicked() {
                            Intent intent = new Intent(MainMenuActivity.this, LogInActivity.class);
                            intent.putExtra(START_ADDER_ACTIVITY_EXTRA, 1);
                            startActivityForResult(intent, SIGN_IN_REQUEST);
                        }

                        @Override
                        public void noClicked() {}
                    }.issue();
                }
                else {
                    startActivity(new Intent(MainMenuActivity.this, AdderActivity.class));
                }
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RestfulClient.getInstance().isAuthorized()) {
                    new YesNoDialog(MainMenuActivity.this, MainMenuActivity.this.getString(R.string.reallySignOut)) {
                        @Override
                        public void yesClicked() {
                            RestfulClient.getInstance().signOut();
                            redraw();
                        }

                        @Override
                        public void noClicked() {}
                    }.issue();
                }
                else {
                    startActivityForResult(new Intent(MainMenuActivity.this, LogInActivity.class), SIGN_IN_REQUEST);
                }
            }
        });

        quitGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMenuActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        redraw();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SIGN_IN_REQUEST:
                if ( resultCode != Activity.RESULT_OK ) return;
                redraw();

                if ( data != null && data.hasExtra(START_ADDER_ACTIVITY_EXTRA) )
                    startActivity(new Intent(MainMenuActivity.this, AdderActivity.class));
            break;
        }
    }

    private void redraw() {
        if (RestfulClient.getInstance().isAuthorized()) {
            logInBtn.setText(getString(R.string.signOut));
            signedInAsFld.setText(getString(R.string.signedInAs) + " " + RestfulClient.getInstance().getUsername());
        }
        else {
            logInBtn.setText(getString(R.string.signIn));
            signedInAsFld.setText(getString(R.string.notSignedIn));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.starter, menu);
        return true;
    }
}
