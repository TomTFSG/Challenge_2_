package com.example.challenge_2_.Frag;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.challenge_2_.FeedReaderDbHelper;
import com.example.challenge_2_.R;
import com.example.challenge_2_.ViewModels.VMFragments;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class offList extends Fragment {
    List<String> Notes;
    List<String> Titles;
    private VMFragments VMFrag;



    ///////////////////////////////////////////////////////////////////////////
    //
    public offList() {
        // Required empty public constructor
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VMFrag = new ViewModelProvider(requireActivity()).get(VMFragments.class);

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
        }
        cursor.close();
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        FloatingActionButton newNote=view.findViewById(R.id.add);
        newNote.setOnClickListener(view1 -> VMFrag.gotoFrag(new offEdit("","")));
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_off_list, container, false); // Replace with your actual layout resource
        LinearLayout menu= view.findViewById(R.id.menu);

        for(int i = 0; i < Titles.size(); i++){
            Button select = new Button(getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // Width
                    ViewGroup.LayoutParams.WRAP_CONTENT   // Height (you can adjust this as needed)
            );
            select.setLayoutParams(layoutParams);

            String titulo = Titles.get(i);
            String nota = Notes.get(i);

            select.setText(titulo);
            select.setTextAlignment(view.TEXT_ALIGNMENT_TEXT_START);

            select.setOnClickListener(view1 -> { VMFrag.gotoFrag(new offEdit(nota, titulo));});

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

                            if (pressDuration < MAX_CLICK_DURATION) VMFrag.gotoFrag(new offEdit(nota,titulo));
                            else onButtonLongPress(select,titulo,nota);

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
        return view;
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    private void onButtonLongPress(
            Button select,
            String titulo,
            String nota
    ){
        PopupMenu popupMenu = new PopupMenu(getContext(), select);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(getContext());
            SQLiteDatabase sql = dbHelper.getWritableDatabase();
            // Toast message on menu item clicked
            if(menuItem.getTitle().equals("Delete note")){
                    String sel = FeedReaderDbHelper.COLUMN_NAME_TITLE + " LIKE ?";
                    String[] selArgs = { titulo };
                    long deletedRows = sql.delete(FeedReaderDbHelper.TABLE_NAME, sel, selArgs);

                    TOASTY("The note has been deleted");
                resetList();
            }
            else if(menuItem.getTitle().equals("Edit note")) VMFrag.gotoFrag(new offEdit(nota, titulo));
            return true;
        });
        // Showing the popup menu
        popupMenu.show();
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    private void resetList(){ VMFrag.gotoFrag(new offList()); }


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