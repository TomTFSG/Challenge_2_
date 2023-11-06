package com.example.challenge_2_.Frag;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.challenge_2_.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class List extends Fragment {
    String user;
    FirebaseFirestore db;
    public List(String username) {
        user=username;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);

    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        Button logout=view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login anotherFragment = new Login();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout, anotherFragment,null);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db=FirebaseFirestore.getInstance();
        db.collection("notes")
                .whereEqualTo("users_username", user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        View view=getView();
                        LinearLayout menu= view.findViewById(R.id.menu);
                        Context context=getContext();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String titulo= document.getString("title"); // DADO ESTE MODELO, NENHUM UTILIZADOR PODE TER DUAS NOTAS COM O MESMO NOME
                                Button select=new Button(context);
                                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, // Width
                                        ViewGroup.LayoutParams.WRAP_CONTENT   // Height (you can adjust this as needed)
                                );
                                select.setLayoutParams(layoutParams);
                                select.setText(titulo);
                                select.setTextAlignment(view.TEXT_ALIGNMENT_TEXT_START);

                                select.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //GO to edit note usando titulo como referencia
                                    }
                                });
                                menu.addView(select);
                                menu.invalidate();


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}