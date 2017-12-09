package com.example.android.mycinerama;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MovieActivity extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = MovieActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

    }

}
