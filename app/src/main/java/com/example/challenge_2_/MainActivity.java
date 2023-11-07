package com.example.challenge_2_;

import static android.content.ContentValues.TAG;
import  androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.example.challenge_2_.Frag.Login;
import com.example.challenge_2_.Frag.offList;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {
    boolean isConnected=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            // Device is connected to the internet
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, new Login())
                    .commit();
        } else {
            // Device is not connected to the internet
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, new offList())
                    .commit();
        }
    }

}