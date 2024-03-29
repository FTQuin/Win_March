package com.example.searchparty;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.searchparty.DatabaseInterfaces.WebScraper;
import com.example.searchparty.ui.future_games.FutureGamesFragmentDirections;
import com.example.searchparty.ui.predict_game.PredictGameFragmentArgs;
import com.example.searchparty.ui.saved_prediction.SavedPredictionFragmentDirections;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.searchparty.Models.Game;
import com.example.searchparty.Models.Prediction;
import com.example.searchparty.ui.future_games.FutureGamesFragment;
import com.example.searchparty.ui.predict_game.PredictGameFragment;
import com.example.searchparty.ui.saved_prediction.SavedPredictionFragment;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

public class MainActivity extends AppCompatActivity implements SavedPredictionFragment.OnListFragmentInteractionListener, FutureGamesFragment.OnListFragmentInteractionListener {
    
    private AppBarConfiguration mAppBarConfiguration;
    private FragmentManager fm;
    Toolbar toolbar;
    int selectedColorRGB;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_saved_predictions, R.id.nav_predict_game,
                R.id.nav_settings, R.id.nav_outcome, R.id.nav_future_games)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        
        fm = getSupportFragmentManager();
    
    
        WebScraper ws = new WebScraper();
        ws.scrape(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if(item.getItemId() == R.id.action_settings) {
            final ColorPicker cp = new ColorPicker(MainActivity.this, Color.red(selectedColorRGB), Color.green(selectedColorRGB), Color.blue(selectedColorRGB));
            cp.show();
    
            /* On Click listener for the dialog, when the user select the color */
            Button okColor = cp.findViewById(R.id.okColorButton);
    
    
            okColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
    
                    toolbar.setBackgroundColor(cp.getColor());
                    selectedColorRGB = cp.getColor();
                    
                    cp.dismiss();
                }
            });
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    
    private void changeFragment(Game game){
        NavDirections directions = FutureGamesFragmentDirections.actionNavFutureGamesToNavPredictGame(game.getID());
        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(directions);
    }
    
    
    
    @Override
    public void onListFragmentInteraction(Prediction prediction) {
        NavDirections directions = SavedPredictionFragmentDirections.actionNavSavedPredictionsToNavOutcome(prediction.getID());
        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(directions);
    }
    
    //called when a game is clicked on in future games
    @Override
    public void onListFragmentInteraction(Game game) {
        changeFragment(game);
    }
}
