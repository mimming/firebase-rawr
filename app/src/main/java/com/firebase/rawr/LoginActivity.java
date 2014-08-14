package com.firebase.rawr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
    public static final String EXTRA_LOGIN = "extra-login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Use an extra to skip the login form if I've just been launched and I'm logged in

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_login);

        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final EditText loginName = (EditText) findViewById(R.id.loginName);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLogin(loginName.getText().toString());
            }
        });

        // Read old saved login names
        String savedLoginName = preferences.getString("loginName", null);
        if(savedLoginName != null) {
            loginName.setText(savedLoginName);
            loginName.setSelection(savedLoginName.length());
        }

        // Enter to submit the login form
        loginName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    processLogin(loginName.getText().toString());
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void processLogin(String loginName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Save the login name for next time
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString("loginName", loginName);
        preferencesEditor.apply();

        // Start our main activity
        Intent intent = new Intent(this, RawrActivity.class);
        intent.putExtra(EXTRA_LOGIN, loginName);
        startActivity(intent);
    }
}
