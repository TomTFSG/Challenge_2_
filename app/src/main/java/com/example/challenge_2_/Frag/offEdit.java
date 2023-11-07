package com.example.challenge_2_.Frag;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.challenge_2_.FeedReaderDbHelper;
import com.example.challenge_2_.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class offEdit extends Fragment {
    String note;
    String title;
    boolean flag;//serve p saber se a nota ja existe ou é nova
    FeedReaderDbHelper dbHelper;
    public offEdit(String n,String t) {
        note=n;
        title=t;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_off_edit, container, false);
    }
    public void onViewCreated(View view,Bundle savedInstanceState) {
        dbHelper = new FeedReaderDbHelper(getContext());
        SQLiteDatabase sql =dbHelper.getWritableDatabase();

        EditText titlebox = getView().findViewById(R.id.title_box);
        EditText notebox = getView().findViewById(R.id.notebox);
        flag=false;
        if (!title.isEmpty()) {
            titlebox.setText(title);
            if (!note.isEmpty()) {
                notebox.setText(note);
            }
        }
        else{
            if(note.isEmpty()){
                flag=true;
            }
        }
        Button back=view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle=titlebox.getText().toString();
                String newNote=notebox.getText().toString();
                if(flag){
                    if(newTitle.isEmpty()){
                        newTitle="Untitled Note";
                    }
                    //ADDNOTE TO DATABASE
                    String[] projection = {
                            FeedReaderDbHelper.COLUMN_NAME_TITLE,
                            FeedReaderDbHelper.COLUMN_NAME_NOTE
                    };

                    String selection = FeedReaderDbHelper.COLUMN_NAME_TITLE + " = ?";
                    String[] selectionArgs = { newTitle };

                    String sortOrder =
                            FeedReaderDbHelper.COLUMN_NAME_NOTE + " DESC";

                    Cursor cursor = sql.query(
                            FeedReaderDbHelper.TABLE_NAME,   // The table to query
                            projection,             // The array of columns to return (pass null to get all)
                            selection,              // The columns for the WHERE clause
                            selectionArgs,          // The values for the WHERE clause
                            null,                   // don't group the rows
                            null,                   // don't filter by row groups
                            sortOrder               // The sort order
                    );
                    if (cursor.getCount() == 0) {
                        ContentValues values = new ContentValues();
                        values.put(FeedReaderDbHelper.COLUMN_NAME_TITLE, newTitle);
                        values.put(FeedReaderDbHelper.COLUMN_NAME_NOTE, newNote);
                        long newRowId = sql.insert(FeedReaderDbHelper.TABLE_NAME, null, values);
                        offList anotherFragment = new offList();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.framelayout, anotherFragment,null);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        Context context = getActivity().getApplicationContext();
                        CharSequence err = "There is a note with this title already";
                        int dur = Toast.LENGTH_SHORT;
                        Toast inc = Toast.makeText(context, err, dur);
                        inc.show();
                    }
                    cursor.close();


                }
                else{
                    if(newTitle.isEmpty()){
                        newTitle="Untitled Note";
                    }
                    //ADDNOTE TO DATABASE
                    String[] projection = {
                            FeedReaderDbHelper.COLUMN_NAME_TITLE,
                            FeedReaderDbHelper.COLUMN_NAME_NOTE
                    };

                    String selection = FeedReaderDbHelper.COLUMN_NAME_TITLE + " = ?";
                    String[] selectionArgs = { newTitle };

                    String sortOrder =
                            FeedReaderDbHelper.COLUMN_NAME_NOTE + " DESC";

                    Cursor cursor = sql.query(
                            FeedReaderDbHelper.TABLE_NAME,   // The table to query
                            projection,             // The array of columns to return (pass null to get all)
                            selection,              // The columns for the WHERE clause
                            selectionArgs,          // The values for the WHERE clause
                            null,                   // don't group the rows
                            null,                   // don't filter by row groups
                            sortOrder               // The sort order
                    );
                    if (cursor.getCount() == 0) {
                        String sel = FeedReaderDbHelper.COLUMN_NAME_TITLE + " LIKE ?";
                        String[] selArgs = { title };
                        int deletedRows = sql.delete(FeedReaderDbHelper.TABLE_NAME, sel, selArgs);
                        ContentValues values = new ContentValues();
                        values.put(FeedReaderDbHelper.COLUMN_NAME_TITLE, newTitle);
                        values.put(FeedReaderDbHelper.COLUMN_NAME_NOTE, newNote);
                        long newRowId = sql.insert(FeedReaderDbHelper.TABLE_NAME, null, values);

                        offList anotherFragment = new offList();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.framelayout, anotherFragment,null);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        String sel = FeedReaderDbHelper.COLUMN_NAME_TITLE + " LIKE ?";
                        String[] selArgs = { title };
                        int deletedRows = sql.delete(FeedReaderDbHelper.TABLE_NAME, sel, selArgs);

                        ContentValues val = new ContentValues();
                        val.put(FeedReaderDbHelper.COLUMN_NAME_TITLE, newTitle);
                        val.put(FeedReaderDbHelper.COLUMN_NAME_NOTE, newNote);
                        long newRowId = sql.insert(FeedReaderDbHelper.TABLE_NAME, null, val);

                        offList anotherFragment = new offList();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.framelayout, anotherFragment,null);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    cursor.close();


                }

            }
        });
        Button delete=view.findViewById(R.id.apagar);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(flag){
                    offList anotherFragment = new offList();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.framelayout, anotherFragment,null);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                else{
                    String sel = FeedReaderDbHelper.COLUMN_NAME_TITLE + " LIKE ?";
                    String[] selArgs = { title };
                    int deletedRows = sql.delete(FeedReaderDbHelper.TABLE_NAME, sel, selArgs);

                    Context context = getActivity().getApplicationContext();
                    CharSequence err = "The note has been deleted";
                    int dur = Toast.LENGTH_SHORT;
                    Toast inc = Toast.makeText(context, err, dur);
                    inc.show();

                    offList anotherFragment = new offList();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.framelayout, anotherFragment,null);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
    }
}