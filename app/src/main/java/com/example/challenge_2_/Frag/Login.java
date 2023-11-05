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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
                login(username,password);
            }

        }
    }
    private void registar(String username, String password){

        //Cria o objeto "user" que vai adicionar à tabela dos users
        Map<String,Object> user=new HashMap<>();
        user.put("username",username);
        user.put("password",password);


        //Vai buscar todos utilizadores existentes para verificar que o username ainda n esta utilizado
        final boolean[] exists ={false};
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    exists[0]=false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String result=document.getString("username");//recebe resultado de username que esta selecionado no for
                        Log.w(TAG, username + " => " + result);
                        if(username.equals(result)){ // O JAVA COMPARA COM .equals, == N EXISTE PASSEI MEIA HORA ATE DESCOBRI ISTO FFS
                            Log.w(TAG,"JA EXISTE");
                            exists[0] =true;//tem de ser um array p ser final e n dar erro ( coisas de java sei la)
                            break;//se ja sabemos q existe, n temos de procurar mais. Logo, break.
                        }
                        Log.w(TAG, username + " => " + result + " "+exists);
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());//Se der erro isto aparece no log
                }




                // Isto é um listener dentro de um listener, n é muito bonito mas deve funcionar
                if(exists[0]==true){
                    //Isto cria um Toast a dizer que o username já está em uso.
                    Context context = getActivity().getApplicationContext();
                    CharSequence err = "Username already in use";
                    int dur = Toast.LENGTH_SHORT;
                    Toast inc = Toast.makeText(context, err, dur);
                    inc.show();
                }

                else{
                    //O utilizador é adicionado à base de dados
                    db.collection("users")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //Toast rapido a dizer q registou e a pedir login
                                    Context context = getActivity().getApplicationContext();
                                    CharSequence err = "Sucessfully registered. Please Login";
                                    int dur = Toast.LENGTH_SHORT;
                                    Toast inc = Toast.makeText(context, err, dur);
                                    inc.show();
                                    Log.w(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);//Caso dê erro aparece isto no log
                                }
                            });
                }
            }
        });
    }
    private void login(String username,String password){
        final boolean[] exists ={false};
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        exists[0]=false;
                        String resultU="";
                        String resultP="";
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String result=document.getString("username");//recebe resultado de username que esta selecionado no for
                                Log.w(TAG, username + " => " + result);
                                if(username.equals(result)){ // O JAVA COMPARA COM .equals, == N EXISTE PASSEI MEIA HORA ATE DESCOBRI ISTO FFS
                                    Log.w(TAG,"JA EXISTE");
                                    exists[0] =true;//tem de ser um array p ser final e n dar erro ( coisas de java sei la)
                                    resultU=result;
                                    resultP=document.getString("password");
                                    break;//se ja sabemos q existe, n temos de procurar mais. Logo, break.
                                }
                                Log.w(TAG, username + " => " + result + " "+exists);
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());//Se der erro isto aparece no log
                        }

                        // Isto é um listener dentro de um listener, n é muito bonito mas deve funcionar
                        if(exists[0]==true){
                            String finalResultP = resultP;
                            db.collection("users")
                                    .whereEqualTo("username", resultU)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String key=document.getString("password");
                                                    if(key.equals(finalResultP)){
                                                        //LOGIN PARA A PAGINA A SEGUIR
                                                        Log.w(TAG, "I'm IN BABY WOOOOOOOO", task.getException());//log p testar

                                                        //mudar para fragmento
                                                        List anotherFragment = new List(username);
                                                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                        transaction.replace(R.id.framelayout, anotherFragment,null);
                                                        transaction.addToBackStack(null);
                                                        transaction.commit();
                                                    }
                                                    else{
                                                        Context context = getActivity().getApplicationContext();
                                                        CharSequence err = "Username/Password incorrect";
                                                        int dur = Toast.LENGTH_SHORT;
                                                        Toast inc = Toast.makeText(context, err, dur);
                                                        inc.show();
                                                    }
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }
                        else{
                            //O utilizador enganou-se
                            Context context = getActivity().getApplicationContext();
                            CharSequence err = "Username/Password incorrect";
                            int dur = Toast.LENGTH_SHORT;
                            Toast inc = Toast.makeText(context, err, dur);
                            inc.show();
                        }
                    }
                });
    }
}