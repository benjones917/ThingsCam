package com.ben.thingscam;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";


    private RecyclerView recyclerView;
    private ViewAdapter viewAdapter;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        auth.addAuthStateListener(authListener);
        auth.signOut();
        auth.signInAnonymously();
        Log.d(TAG, "Signed in to Firebase Anonymously");

        recyclerView = (RecyclerView) findViewById(R.id.doorbellView);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);

        databaseRef = database.getReference().child("images");

    }

    @Override
    public void onStart() {
        super.onStart();

        viewAdapter = new ViewAdapter(this, databaseRef);
        recyclerView.setAdapter(viewAdapter);

        viewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(viewAdapter.getItemCount());
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        if (viewAdapter != null) {
            viewAdapter.cleanup();
            viewAdapter = null;
        }
    }

}
