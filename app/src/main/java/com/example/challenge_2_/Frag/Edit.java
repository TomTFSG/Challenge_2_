package com.example.challenge_2_.Frag;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Edit extends Fragment {
    String title;
    String note;
    String user;
    String id;
    FirebaseFirestore db;
    public Edit(String title,String user,String note,String id) {
        db=FirebaseFirestore.getInstance();
        this.title=title;
        this.user=user;
        this.note=note;
        this.id=id;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }
    public void onViewCreated(View view,Bundle savedInstanceState){

        EditText titlebox= getView().findViewById(R.id.title_box);
        EditText notebox= getView().findViewById(R.id.notebox);
        if(!title.isEmpty()){
            titlebox.setText(title);
            if(!note.isEmpty()){
                notebox.setText(note);
            }
        }
        Button back=view.findViewById(R.id.back);
        Button save=view.findViewById(R.id.save);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                List anotherFragment = new List(user);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout, anotherFragment,null);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle=titlebox.getText().toString();
                String newNote=notebox.getText().toString();
                if(id.isEmpty()){
                    if(newTitle.isEmpty()){
                        newTitle="Untitled Note";
                    }
                    Map<String, Object> note = new HashMap<>();
                    note.put("note", newNote);
                    note.put("title", newTitle);
                    note.put("users_username", user);
                    db.collection("notes")
                            .add(note) // Firestore will generate a unique document ID AKA path to file
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                }
                else{
                    DocumentReference docRef = db.collection("notes").document(id);
                    docRef.update("title", newTitle).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.w(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error updating document", e);
                                }
                            });
                    docRef.update("note", newNote).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Log.w(TAG, "DocumentSnapshot successfully updated!");
                                    Log.d(TAG,newNote);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error updating document", e);
                                }
                            });
                }
                Context context = getActivity().getApplicationContext();
                CharSequence err = "Saved";
                int dur = Toast.LENGTH_SHORT;
                Toast inc = Toast.makeText(context, err, dur);
                inc.show();
            }
        });
        Button delete=view.findViewById(R.id.apagar);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                db.collection("notes").document(id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                Context context = getActivity().getApplicationContext();
                                CharSequence err = "The note has been deleted";
                                int dur = Toast.LENGTH_SHORT;
                                Toast inc = Toast.makeText(context, err, dur);
                                inc.show();
                                List anotherFragment = new List(user);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.framelayout, anotherFragment,null);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
            }
        });
    }

}