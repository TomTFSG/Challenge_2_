package com.example.challenge_2_.Frag;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.challenge_2_.FeedReaderDbHelper;
import com.example.challenge_2_.R;
import com.example.challenge_2_.ViewModels.VMFragments;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class List extends Fragment {
    private String user;
    private FirebaseFirestore db;
    private VMFragments VMFrag;
    private java.util.List<String> Notes;
    private java.util.List<String> Titles;
    int backedupnotes = 0;

    public List(String username) {
        user = username;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        VMFrag = new ViewModelProvider(requireActivity()).get(VMFragments.class);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button logout = view.findViewById(R.id.logout);
        Button newNote = view.findViewById(R.id.add);

        logout.setOnClickListener(view1 -> VMFrag.gotoFrag(new Login()));

        newNote.setOnClickListener(view2 -> {
            String vazio = "";
            VMFrag.gotoFrag(new Edit(vazio, user, vazio, vazio));
        });
        if(isAdded() && getView() != null) {
            EditText editText = view.findViewById(R.id.searchbar);

            editText.setOnKeyListener((v, keyCode, event) -> {
                // Check if the key event is the Enter key
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String search = editText.getText().toString();
                    pesquisa(search);
                    return true; // Consume the event
                }
                return false; // Let the system handle other key events
            });

            //ir buscar as notas do utilizador em questão
            db.collection("notes")
                    .whereEqualTo("users_username", user)
                    .get()
                    .addOnCompleteListener(task ->
                    {

                        LinearLayout menu = view.findViewById(R.id.menu);
                        Context context = getContext();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // DADO ESTE MODELO, NENHUM UTILIZADOR PODE TER DUAS NOTAS COM O MESMO NOME
                                // DADO ESTE MODELO, NENHUM UTILIZADOR PODE TER DUAS NOTAS COM O MESMO NOME
                                // DADO ESTE MODELO, NENHUM UTILIZADOR PODE TER DUAS NOTAS COM O MESMO NOME
                                String titulo = document.getString("title");
                                String nota = document.getString("note");
                                String id = document.getId().toString();
                                Button select = new Button(context);

                                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, // Width
                                        ViewGroup.LayoutParams.WRAP_CONTENT   // Height (you can adjust this as needed)
                                );

                                select.setLayoutParams(layoutParams);
                                select.setText(titulo);
                                select.setTextAlignment(view.TEXT_ALIGNMENT_TEXT_START);

                                select.setOnTouchListener(new View.OnTouchListener(){
                                    private Handler handler = new Handler();
                                    private Runnable runnable;
                                    private long pressStartTime;
                                    private static final long MAX_CLICK_DURATION = 200;
                                    @SuppressLint("ClickableViewAccessibility")
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {

                                        switch (event.getAction()) {
                                            case MotionEvent.ACTION_DOWN:
                                                pressStartTime = System.currentTimeMillis();
                                                break;
                                            case MotionEvent.ACTION_UP:
                                                long pressDuration = System.currentTimeMillis() - pressStartTime;

                                                if (pressDuration < MAX_CLICK_DURATION) VMFrag.gotoFrag(new Edit(titulo, user, nota, id));
                                                else onButtonLongPress(select,titulo, user, nota,id);

                                                break;
                                            case MotionEvent.ACTION_CANCEL:
                                                // Cancel the handler if the button is released
                                                pressStartTime = 0;break;
                                        }
                                        return true; // Consume the touch event
                                    }

                                });
                                menu.addView(select);
                                menu.invalidate();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
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
                            .addOnSuccessListener(documentReference -> TOASTY("Notes created offline have been uploaded"))
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

    private void pesquisa(String s){
        String search = s;
        View view = getView();
        LinearLayout menu = view.findViewById(R.id.menu);
        menu.removeAllViews();
        db.collection("notes")
                .whereEqualTo("users_username", user)
                .whereEqualTo("title",search)
                .get()
                .addOnCompleteListener(task -> {
                    Context context = getContext();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // DADO ESTE MODELO, NENHUM UTILIZADOR PODE TER DUAS NOTAS COM O MESMO NOME

                            String titulo = document.getString("title");
                            String nota = document.getString("note");
                            String id = document.getId().toString();
                            Button select = new Button(context);

                            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, // Width
                                    ViewGroup.LayoutParams.WRAP_CONTENT   // Height (you can adjust this as needed)
                            );

                            select.setLayoutParams(layoutParams);
                            select.setText(titulo);
                            select.setTextAlignment(view.TEXT_ALIGNMENT_TEXT_START);

                            select.setOnClickListener(view3 -> VMFrag.gotoFrag(new Edit(titulo, user, nota, id)));
                            menu.addView(select);
                            menu.invalidate();
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }
    private void onButtonLongPress(
            Button button,
            String titulo,
            String user,
            String nota,
            String id
    ){
        PopupMenu popupMenu = new PopupMenu(getContext(), button);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            // Toast message on menu item clicked
            if(menuItem.getTitle().equals("Delete note")){

                db.collection("notes").document(id)
                        .delete()
                        .addOnSuccessListener(aVoid -> { TOASTY("The note has been deleted"); })
                        .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));


                resetList();
            }
            else if(menuItem.getTitle().equals("Edit note")) VMFrag.gotoFrag(new Edit(titulo, user, nota, id));
            return true;
        });
        // Showing the popup menu
        popupMenu.show();

    }



    ///////////////////////////////////////////////////////////////////////////
    //
    private void resetList(){ VMFrag.gotoFrag(new List(user)); }




    ///////////////////////////////////////////////////////////////////////////
    //
    // Função que cria um toast (referência a MK)
    private void TOASTY(CharSequence err){
        Context context = getActivity().getApplicationContext();
        int dur = Toast.LENGTH_SHORT;
        Toast inc = Toast.makeText(context, err, dur);
        inc.show();
    }
}