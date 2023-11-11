package com.example.challenge_2_.Frag;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.challenge_2_.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class List extends Fragment {
    String user;
    FirebaseFirestore db;

    public List(String username) {
        user = username;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button logout = view.findViewById(R.id.logout);
        FloatingActionButton newNote = view.findViewById(R.id.add);

        logout.setOnClickListener(view1 -> gotoFrag(new Login()));

        newNote.setOnClickListener(view2 -> {
            String vazio = "";
            gotoFrag(new Edit(vazio, user, vazio, vazio));
        });
        if(isAdded() && getView() != null) {
            EditText editText = view.findViewById(R.id.searchbar);

            editText.setOnKeyListener((v, keyCode, event) -> {
                // Check if the key event is the Enter key
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String search=editText.getText().toString();
                    pesquisa(search);
                    return true; // Consume the event
                }
                return false; // Let the system handle other key events
            });

            //ir buscar as notas do utilizador em questão
            db = FirebaseFirestore.getInstance();
            db.collection("notes")
                            .

                    whereEqualTo("users_username", user)
                            .

                    get()
                            .

                    addOnCompleteListener(task ->

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

                                select.setOnClickListener(view3 -> gotoFrag(new Edit(titulo, user, nota, id)));
                                menu.addView(select);
                                menu.invalidate();


                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

}



    //Função criada para facilitar a visialuzação do código (troca de fragments)
    private void gotoFrag(@NonNull androidx.fragment.app.Fragment Frag){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout, Frag,null)
                .addToBackStack(null)
                .commit();
    }
    private void pesquisa(String s){
        String search=s;
        View view=getView();
        LinearLayout menu = view.findViewById(R.id.menu);
        menu.removeAllViews();
        db = FirebaseFirestore.getInstance();
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

                            select.setOnClickListener(view3 -> gotoFrag(new Edit(titulo, user, nota, id)));
                            menu.addView(select);
                            menu.invalidate();


                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }
}