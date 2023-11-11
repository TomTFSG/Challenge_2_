package com.example.challenge_2_.Frag;

import static android.content.ContentValues.TAG;

import android.content.Context;
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

import com.example.challenge_2_.R;
import com.example.challenge_2_.ViewModels.VMFragments;
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
    private EditText user;
    private EditText pass;
    private FirebaseFirestore db;
    private VMFragments VMFrag;



    ///////////////////////////////////////////////////////////////////////////
    //
    public Login() {
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        VMFrag = new ViewModelProvider(requireActivity()).get(VMFragments.class);
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        user = view.findViewById(R.id.username);
        pass = view.findViewById(R.id.password);
        Button regi=view.findViewById(R.id.register_button);
        regi.setOnClickListener(this);
        Button logi=view.findViewById(R.id.login_button);
        logi.setOnClickListener(this);
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    @Override
    public void onClick(View view) {
        String username = user.getText().toString();
        String password = pass.getText().toString();
        int id= view.getId();

        if (password.isEmpty() || username.isEmpty()) {
            TOASTY("Please fill username and password");
        }
        else{
            if(id == R.id.register_button) registar(username, password);
            else if(id == R.id.login_button) login(username,password);
        }
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



    ///////////////////////////////////////////////////////////////////////////
    //
    // definir variáveis utilizadas para checkar o Login e o Registo
    private final boolean[] exists ={false};
    private String resultU = ""; //resultado User (mutável na função CheckarUser)
    private String resultP = ""; //resultado Password (mutável na função CheckarUser)




    ///////////////////////////////////////////////////////////////////////////
    //
    // Função Registar é chamada quando se clica no botão de Registo
    private void registar(String username, String password){

        //Cria o objeto "novoUser" que vai adicionar à tabela dos users
        Map<String,Object> novoUser = new HashMap<>();
        novoUser.put("username", username);
        novoUser.put("password", password);


        //Vai buscar todos utilizadores existentes para verificar que o username ainda n esta utilizado

        db.collection("users")
            .get()
            .addOnCompleteListener(task -> {
                CheckarUsers(false, task, username);

                // Isto é um listener dentro de um listener, n é muito bonito mas deve funcionar
                if(exists[0]) TOASTY("Username already in use");
                else{
                    //O utilizador é adicionado à Firestore (vulgo, db)
                    db.collection("users")
                            .add(novoUser)
                            .addOnSuccessListener(documentReference -> {
                                //Toast rapido a dizer q registou e a pedir login
                                TOASTY("Sucessfully registered. Please Login");
                                Log.i(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Erro ao adicionar o doc: ", e);//Caso dê erro aparece isto no log
                            });
                }
            });
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    // Função Login é chamada quando se clica no botão de Login
    private void login(String username,String password){
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                            CheckarUsers(true, task, username);

                /*
                    No caso do Login...

                    depois de se checkar os utilizadores (código a cima),
                    caso o nome de utilizador escrito existir,
                    vai-se buscar os dados referidos ao utilizador em questão...

                    Se a password estiver incorreta, o utilizador recebe um Toast a informar que o utilizador ou a passe estã incorretos,
                    senão,este é levado para o Fragmento da lista de notas!


                    (se for verificado anteriormente que o user não existe, então, o utilizador recebe a mensagem a informar que o utilizador ou a passe estã incorretos)
                */
                            if(exists[0]){
                                db.collection("users")
                                        .whereEqualTo("username", resultU)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task1.getResult()) {
                                                    String key=document.getString("password");
                                                    if(password.equals(key)){
                                                        //LOGIN PARA A PAGINA A SEGUIR
                                                        Log.i(TAG, "I'm IN BABY WOOOOOOOO", task1.getException());//log p testar
                                                        VMFrag.gotoFrag(new List(username));
                                                    }
                                                    else TOASTY("Username/Password incorrect");
                                                }
                                            } else {
                                                Log.e(TAG, "Erro a conseguir os docs: ", task1.getException());
                                            }
                                        });
                            }
                            else TOASTY("Username/Password incorrect");
                        }
                );
    }



    ///////////////////////////////////////////////////////////////////////////
    //
    // Função utilizada depois no onCompleteListener de ir buscar a coleção "users" dentro da Firestore
    private void CheckarUsers(
            boolean login,
            @NonNull Task<QuerySnapshot> task,
            String username
    ){
        exists[0] = false;
        resultU = "";
        resultP = "";

        if (task.isSuccessful()) {
            /*
                procura cada QueryDocument dentro do resultado obtido ao se conectar com a Firestore
                (no onCompleteListener)
             */
            for (QueryDocumentSnapshot document : task.getResult()) {
                String result = document.getString("username"); //recebe resultado de username que esta selecionado no for
                Log.i(TAG, username + " => " + result);

                //verifica se o username escrito pelo utilizador é igual ao que o utilizador da query em questão
                if (username.equals(result)) {
                    Log.w(TAG, "JA EXISTE");
                    //referir que o utilizador existe (q está na Firestore)
                    exists[0] = true; //tem de ser um array p ser final e n dar erro ( coisas de java sei la)

                    /*
                        se estivermos no login,
                        vai-se definir os resultados de User e de Password escritos pelo utilizador
                        (que serão utilizados posteriormente ao chamado desta função)

                        se estivermos no registo,
                        só indica-nos que existe (código anterior a este comentário)
                        (tal como no login, será utilizado posteriormente)
                    */
                    if (login) {
                        resultU = result;
                        resultP = document.getString("password");
                    }
                    break;//se ja sabemos q existe, n temos de procurar mais. Logo, break.
                }
                Log.i(TAG, username + " => " + result + " " + exists);
            }
        }
        else Log.e(TAG, "Erro a conseguir os docs: ", task.getException()); //Se der erro isto aparece no log
    }
}