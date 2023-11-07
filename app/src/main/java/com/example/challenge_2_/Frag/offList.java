package com.example.challenge_2_.Frag;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.challenge_2_.FeedReaderDbHelper;
import com.example.challenge_2_.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class offList extends Fragment {
    List<String> Notes;
    List<String> Titles;
    public offList() {
        // Required empty public constructor
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        FloatingActionButton newNote=view.findViewById(R.id.add);
        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vazio="";
                offEdit anotherFragment = new offEdit(vazio,vazio);
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

        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderDbHelper.COLUMN_NAME_TITLE,
                FeedReaderDbHelper.COLUMN_NAME_NOTE
        };

        String sortOrder = FeedReaderDbHelper.COLUMN_NAME_NOTE + " DESC";

        Cursor cursor = db.query(
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
        }
        cursor.close();
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_off_list, container, false); // Replace with your actual layout resource
        LinearLayout menu= view.findViewById(R.id.menu);
        Context context=getContext();
        for(int i=0;i<Titles.size();i++){
            Button select=new Button(context);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // Width
                    ViewGroup.LayoutParams.WRAP_CONTENT   // Height (you can adjust this as needed)
            );
            select.setLayoutParams(layoutParams);
            String titulo=Titles.get(i);
            String nota=Notes.get(i);
            select.setText(titulo);
            select.setTextAlignment(view.TEXT_ALIGNMENT_TEXT_START);
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    offEdit anotherFragment = new offEdit(nota, titulo);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.framelayout, anotherFragment,null);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
            menu.addView(select);
            menu.invalidate();
        }
        return view;
        }

}