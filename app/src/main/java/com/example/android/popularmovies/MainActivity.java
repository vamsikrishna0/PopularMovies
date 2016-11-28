package com.example.android.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static android.R.attr.fragment;

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
