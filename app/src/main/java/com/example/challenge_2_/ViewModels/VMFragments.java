package com.example.challenge_2_.ViewModels;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.challenge_2_.Frag.Login;

public class VMFragments extends ViewModel {
    private MutableLiveData<Fragment> fragToGo = new MutableLiveData<Fragment>();

    // TRUE => Menu
    // FALSE => Editor
    public VMFragments(){
    }

    public void gotoFrag(@NonNull Fragment Frag){
        fragToGo.setValue(Frag);
    }

    public LiveData<Fragment> getFragAtual(){
        return fragToGo;
    }
}