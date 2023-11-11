package com.example.challenge_2_;

import static android.content.ContentValues.TAG;
import  androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.example.challenge_2_.Frag.Login;
import com.example.challenge_2_.Frag.offList;
import com.example.challenge_2_.ViewModels.VMFragments;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {
    private boolean isConnected = false;
    private VMFragments VMFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();



        VMFrag = new ViewModelProvider(this).get(VMFragments.class);
        VMFrag.gotoFrag(
            (activeNetworkInfo != null && activeNetworkInfo.isConnected())
                ? new Login()
                : new offList()
        );
        VMFrag.getFragAtual().observe(this, fragAtual -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout, fragAtual)
                    .addToBackStack(null)
                    .commit();
        });



    }

}