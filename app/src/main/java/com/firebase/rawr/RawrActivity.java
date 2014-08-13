package com.firebase.rawr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.rawr.model.Rawr;

public class RawrActivity extends Activity {

    private final Firebase mFirebaseRef = new Firebase("https://rawr-demo.firebaseio.com/");
    private String mLoginName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mLoginName = intent.getStringExtra(LoginActivity.EXTRA_LOGIN);
        final String finalMLoginName = mLoginName;

        setContentView(R.layout.activity_rawr);


        LinearLayout helloButton = (LinearLayout) findViewById(R.id.userJohn);
        helloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase localRef = mFirebaseRef.child("rawrs").child("john").push();
                localRef.child("from").setValue(finalMLoginName);
                // TODO: not sure I really care about this?
                localRef.child("when").setValue(System.currentTimeMillis());
            }
        });
        LinearLayout goodbyeButton = (LinearLayout) findViewById(R.id.userMary);
        goodbyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase localRef = mFirebaseRef.child("rawrs").child("mary").push();
                localRef.child("from").setValue(finalMLoginName);
                // TODO: not sure I really care about this?
                localRef.child("when").setValue(System.currentTimeMillis());
            }
        });

        Button addUserButton = (Button) findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // All my rawrs
        final Firebase myRawrsRef = mFirebaseRef.child("rawrs").child(mLoginName);

        // Listen for new rawrs to me
        myRawrsRef.endAt().limit(1).addChildEventListener(new ChildEventListener() {
            private boolean firstSeen = false;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Ugly way to skip the first record
                if (!firstSeen) {
                    firstSeen = true;
                    return;
                }

                Rawr rawr = dataSnapshot.getValue(Rawr.class);

                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);

                TextView toastTextView = new TextView(getApplicationContext());
                toastTextView.setText(rawr.getFrom() + ", \"RAAAAWR!!!!1\"");
                toastTextView.setTextSize(50);

                toast.setView(toastTextView);
                toast.show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rawr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
