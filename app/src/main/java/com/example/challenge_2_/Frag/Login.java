package com.example.challenge_2_.Frag;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.challenge_2_.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Login extends Fragment implements View.OnClickListener{
    EditText user;
    EditText pass;
    FirebaseFirestore db;
    public Login() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        user = view.findViewById(R.id.username);
        pass = view.findViewById(R.id.password);
        Button regi=view.findViewById(R.id.register_button);
        regi.setOnClickListener(this);
        Button logi=view.findViewById(R.id.login_button);
        logi.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        Context context = getActivity().getApplicationContext();
        String username = user.getText().toString();
        String password = pass.getText().toString();
        int id= view.getId();
        if(id==R.id.register_button){
            if (password.isEmpty() || username.isEmpty()) {
                CharSequence err = "Please fill username and password";
                int dur = Toast.LENGTH_SHORT;
                Toast inc = Toast.makeText(context, err, dur);
                inc.show();
            } else {
                //REGISTAR
                registar(username,password);

            }

        }
        else if(id==R.id.login_button){
            if (password.isEmpty() || username.isEmpty()) {
                CharSequence err = "Please fill username and password";
                int dur = Toast.LENGTH_SHORT;
                Toast inc = Toast.makeText(context, err, dur);
                inc.show();
            } else {
                //LOGIN


            }

        }
    }
    private void registar(String username, String password){
        Map<String,Object> user=new HashMap<>();
        user.put("username",username);
        user.put("password",password);
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}