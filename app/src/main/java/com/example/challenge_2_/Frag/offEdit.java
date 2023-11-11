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
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.challenge_2_.FeedReaderDbHelper;
import com.example.challenge_2_.R;
import com.example.challenge_2_.ViewModels.VMFragments;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class offEdit extends Fragment {
    private String note, title; //strings relativas às notas e ao titulo
    private boolean isNotaNova; //serve p saber se a nota ja existe ou é nova
    private FeedReaderDbHelper dbHelper;
    private VMFragments VMFrag;
    private Button back, save; //botao de salvar e voltar atras da appbar
    private EditText titlebox, notebox; //caixas de texto da nota e do titulo



    public offEdit(String n,String t) {
        note= n ;
        title = t;
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VMFrag = new ViewModelProvider(requireActivity()).get(VMFragments.class);
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_off_edit, container, false);
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    public void onViewCreated(View view,Bundle savedInstanceState) {

        dbHelper = new FeedReaderDbHelper(getContext());
        SQLiteDatabase sql = dbHelper.getWritableDatabase();

        back = view.findViewById(R.id.back);
        save = view.findViewById(R.id.save);
        titlebox = view.findViewById(R.id.title_box);
        notebox = view.findViewById(R.id.notebox);

        isNotaNova = false;
        if (!title.isEmpty()) {
            titlebox.setText(title);
            if (!note.isEmpty()) notebox.setText(note);
        }
        else if(note.isEmpty()) isNotaNova = true;

        ////////////
        // Ao se clicar no botão de voltar atrás da appbar
        //
        back.setOnClickListener(v -> VMFrag.gotoFrag(new offList()));

        ////////////
        // Ao se clicar no botão de salvar da appbar
        //
        save.setOnClickListener(view1 -> {
            //novo titulo da nota
            String newTitle = titlebox.getText().toString();
            //nova nota (texto)
            String newNote = notebox.getText().toString();

            //se o novo titulo for vazio, então, fica com "Untitled Note"
            if(newTitle.isEmpty()) newTitle = "Untitled Note";

            //o que queremos receber
            String[] projecao = {
                    FeedReaderDbHelper.COLUMN_NAME_TITLE,
                    FeedReaderDbHelper.COLUMN_NAME_NOTE
            };
            //o que se seleciona
            String selecao = FeedReaderDbHelper.COLUMN_NAME_TITLE + " = ?";
            //argumentos da seleção
            String[] argsSel = { newTitle };
            //ordem de sorteio
            String ordem = FeedReaderDbHelper.COLUMN_NAME_NOTE + " DESC";

            Cursor cursor = sql.query(
                    FeedReaderDbHelper.TABLE_NAME,
                    projecao,
                    selecao,
                    argsSel,
                    null,
                    null,
                    ordem
            );

            if(isNotaNova){
                if (cursor.getCount() == 0) {
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderDbHelper.COLUMN_NAME_TITLE, newTitle);
                    values.put(FeedReaderDbHelper.COLUMN_NAME_NOTE, newNote);
                    long newRowId = sql.insert(FeedReaderDbHelper.TABLE_NAME, null, values);

                    TOASTY("Saved");
                } else TOASTY("There is a note with this title already");
            }
            else{
                String sel = FeedReaderDbHelper.COLUMN_NAME_TITLE + " LIKE ?";
                String[] selArgs = { title };
                int deletedRows = sql.delete(FeedReaderDbHelper.TABLE_NAME, sel, selArgs);

                ContentValues values = new ContentValues();
                values.put(FeedReaderDbHelper.COLUMN_NAME_TITLE, newTitle);
                values.put(FeedReaderDbHelper.COLUMN_NAME_NOTE, newNote);

                long newRowId = sql.insert(FeedReaderDbHelper.TABLE_NAME, null, values);

                TOASTY("Saved");
            }

            cursor.close();

        });
        Button delete=view.findViewById(R.id.apagar);
        delete.setOnClickListener(v -> {
            if(isNotaNova) VMFrag.gotoFrag(new offList());
            else{
                String sel = FeedReaderDbHelper.COLUMN_NAME_TITLE + " LIKE ?";
                String[] selArgs = { title };
                int deletedRows = sql.delete(FeedReaderDbHelper.TABLE_NAME, sel, selArgs);

                TOASTY("The note has been deleted");
                VMFrag.gotoFrag(new offList());
            }
        });
    }


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