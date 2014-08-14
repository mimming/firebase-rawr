package com.firebase.rawr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.firebase.rawr.model.Rawr;
import com.firebase.rawr.model.User;

public class RawrActivity extends Activity {

    private Firebase mFirebaseRef;
    private String mLoginName;
    private Firebase mMyRawrsRef;
    private User mCurrentUser;
    private FlowLayout mContactList;


    private ChildEventListener mNewRawrsListener = new ChildEventListener() {
        // TODO: There's a bug in here. Sometimes I don't get my first rawr
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
    };
    private ValueEventListener mCurrentUserListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // If we have an exsiting user
            if(dataSnapshot.getValue() != null) {
                mCurrentUser = dataSnapshot.getValue(User.class);
            } else {
                mCurrentUser = new User();
                mCurrentUser.setName(mLoginName);
                mCurrentUser.setRawrs(0);
                mCurrentUser.setProfileImageBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.dino1));

                // Save the current user
                mFirebaseRef.child("users").child(mLoginName).setValue(mCurrentUser);
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get login name from extra
        Intent intent = getIntent();
        mLoginName = intent.getStringExtra(LoginActivity.EXTRA_LOGIN);
        final String finalMLoginName = mLoginName;

        // Get my contact list
        mContactList = (FlowLayout)findViewById(R.id.contactList);
        mContactList.

        // get our firebase
        mFirebaseRef = new Firebase("https://rawr-demo.firebaseio.com/");
        // All my rawrs
        mMyRawrsRef = mFirebaseRef.child("rawrs").child(mLoginName);

        // Watch for changes to me
        mFirebaseRef.child("users").child(mLoginName).addValueEventListener(mCurrentUserListener);

        setContentView(R.layout.activity_rawr);

        LinearLayout johnButton = (LinearLayout) findViewById(R.id.userJohn);
        johnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRawr(finalMLoginName, "john");
            }
        });
        LinearLayout maryButton = (LinearLayout) findViewById(R.id.userMary);
        maryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRawr(finalMLoginName, "mary");
            }
        });

        Button addUserButton = (Button) findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Add user activity
            }
        });

        // logging out
        Button logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to login clearing the stack
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                // End this one
                finish();
            }
        });

        // Listen for new rawrs to me
        mMyRawrsRef.endAt().limit(1).addChildEventListener(mNewRawrsListener);
    }

    private void sendRawr(String from, String to) {
        Firebase localRef = mFirebaseRef.child("rawrs").child(to).push();
        localRef.child("from").setValue(from);
        localRef.child("when").setValue(System.currentTimeMillis());

        // Use a transaction for the cached rawrs count
        mFirebaseRef.child("users").child(to).child("rawrs").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(0);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                //This method will be called once with the results of the transaction.
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO: consider moving this to onpause?
        mMyRawrsRef.removeEventListener(mNewRawrsListener);
        mFirebaseRef.child("users").child(mLoginName).removeEventListener(mCurrentUserListener);

    }
}
