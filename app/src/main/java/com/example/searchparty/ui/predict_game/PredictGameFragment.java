package com.example.searchparty.ui.predict_game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import com.example.searchparty.Models.Game;
import com.example.searchparty.Models.Team;
import com.example.searchparty.R;

public class PredictGameFragment extends Fragment {
    
    private PredictGameViewModel predictGameViewModel;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        predictGameViewModel =
                ViewModelProviders.of(this).get(PredictGameViewModel.class);
        View root = inflater.inflate(R.layout.fragment_predict_game, container, false);
        final TextView textView = root.findViewById(R.id.text_predict_game);
        predictGameViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
    
        Team ATeam = new Team("ATeam");
        Team BTeam = new Team("BTeam");
    
        Game futureGame = new Game(ATeam, BTeam);
        Game pastGame = new Game(BTeam, ATeam);
        
        pastGame.completeGame();
        
        TextView txtTest = root.findViewById(R.id.txt_predict_test);
        
        String outstr = "";
    
        outstr = ATeam.getFutureGames().get(0).toString();
        outstr = outstr + "\n\n" + BTeam.getPreviousGames().get(0).toString();
        
        txtTest.setText(outstr);
        
        return root;
    }
}