package com.simona.oxforddictionary;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class ActivityB extends AppCompatActivity {

    LinearLayout wholeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);

        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#135D98")));

        initViews();
    }

    void  initViews(){
        wholeScreen = findViewById(R.id.wholeScreenID);
        wholeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMainActivity = new Intent(ActivityB.this, MainActivity.class);
                startActivity(toMainActivity);
            }
        });
    }
}