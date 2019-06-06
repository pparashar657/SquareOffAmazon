package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        MaterialButton button = (MaterialButton) findViewById(R.id.materialButton);
        ImageView back = (ImageView) findViewById(R.id.back);
        ImageView logo = (ImageView) findViewById(R.id.appCompatImageView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ObjectAnimator animation = ObjectAnimator.ofFloat(button,"translationY",-300f);
        animation.setDuration(1000);
        animation.start();

        ObjectAnimator animation1 = ObjectAnimator.ofFloat(back,"translationY",260f);
        animation1.setDuration(1000);
        animation1.start();

        ObjectAnimator animation2 = ObjectAnimator.ofFloat(logo,"translationY",260f);
        animation2.setDuration(1000);
        animation2.start();



    }
}
