package com.example.challenge_2_.Frag;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.challenge_2_.FeedReaderDbHelper;
import com.example.challenge_2_.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class List extends Fragment {
    private java.util.List<String> Notes;
    private java.util.List<String> Titles;
    int backedupnotes=0;
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
        FloatingActionButton newNote=view.findViewById(R.id.add);
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
        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vazio="";
                Edit anotherFragment = new Edit(vazio,user,vazio,vazio);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout, anotherFragment,null);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        db=FirebaseFirestore.getInstance();
        db.collection("notes")
                .whereEqualTo("users_username", user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        LinearLayout menu= view.findViewById(R.id.menu);
                        Context context=getContext();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String titulo= document.getString("title"); // DADO ESTE MODELO, NENHUM UTILIZADOR PODE TER DUAS NOTAS COM O MESMO NOME
                                String nota=document.getString("note");
                                String id= document.getId().toString();
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
                                        Edit anotherFragment = new Edit(titulo,user,nota,id);
                                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        transaction.replace(R.id.framelayout, anotherFragment,null);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
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
        if(backedupnotes>0) {
            for (int a = 0; a < backedupnotes; a++) {
                String createNote = Notes.get(a);
                String newTitle = Titles.get(a);
                Map<String, Object> note = new HashMap<>();
                note.put("note", createNote);
                note.put("title", newTitle);
                note.put("users_username", user);
                db.collection("notes")
                        .add(note) // Firestore will generate a unique document ID AKA path to file
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                Context context = getActivity().getApplicationContext();
                                CharSequence err = "Notes created Offline have been uploaded";
                                int dur = Toast.LENGTH_SHORT;
                                Toast inc = Toast.makeText(context, err, dur);
                                inc.show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
            }
            //POSSIVEIS ERROS AQUI
            FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(getContext());
            SQLiteDatabase sql = dbHelper.getReadableDatabase();
            sql.execSQL(FeedReaderDbHelper.SQL_DELETE_ENTRIES);
            sql.execSQL(FeedReaderDbHelper.SQL_CREATE_ENTRIES);

        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //BACKUP WHAT WAS OFFLINE
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(getContext());
        SQLiteDatabase sql = dbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderDbHelper.COLUMN_NAME_TITLE,
                FeedReaderDbHelper.COLUMN_NAME_NOTE
        };

        String sortOrder = FeedReaderDbHelper.COLUMN_NAME_NOTE + " DESC";

        Cursor cursor = sql.query(
                FeedReaderDbHelper.TABLE_NAME,
                projection,
                null, // Remove the selection and selectionArgs
                null,
                null,
                null,
                sortOrder
        );

        Titles = new ArrayList<>();
        Notes = new ArrayList();
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_NAME_TITLE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderDbHelper.COLUMN_NAME_NOTE));
            Titles.add(title);
            Notes.add(note);

            // Log the retrieved data for debugging
            Log.d(TAG, "Title: " + title + ", Note: " + note);
            backedupnotes++;
        }
        cursor.close();

    }
}