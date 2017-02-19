package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            MovieFragment movieFragment = new MovieFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.activity_main, movieFragment).commit();
        }
        setContentView(R.layout.activity_main);
    }
}
