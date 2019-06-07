package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

public class HomeActivity extends AppCompatActivity implements View .OnClickListener{


    CardView card;
    CardView card1;
    CardView card2;
    CardView card3;
    Intent intent;
    ActivityOptions options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        card = (CardView) findViewById(R.id.materialCardView);
        card1 = (CardView) findViewById(R.id.materialCardView1);
        card2 = (CardView) findViewById(R.id.materialCardView2);
        card3 = (CardView) findViewById(R.id.materialCardView3);

        ObjectAnimator animation = ObjectAnimator.ofFloat(card,"translationX",0f);
        animation.setDuration(1000);
        animation.start();

        ObjectAnimator animation1 = ObjectAnimator.ofFloat(card1,"translationX",-0f);
        animation1.setDuration(1000);
        animation1.start();


        ObjectAnimator animation2 = ObjectAnimator.ofFloat(card2,"translationX",0f);
        animation2.setDuration(1000);
        animation2.start();


        ObjectAnimator animation3 = ObjectAnimator.ofFloat(card3,"translationX",-0f);
        animation3.setDuration(1000);
        animation3.start();

        card.setOnClickListener(this);
        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card3.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        View tempv;
        Pair pair[] = new Pair[2];

        switch (v.getId()){
            case R.id.materialCardView :
                intent = new Intent(this,UploadActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    tempv = v.findViewById(R.id.appCompatImageView);
                    pair[0] = new Pair<View,String>(tempv,"transitionimage");

                    tempv = v.findViewById(R.id.heading);
                    pair[1] = new Pair<View,String>(tempv,"transitiontext");

                    options = ActivityOptions.makeSceneTransitionAnimation(this,pair);

                    startActivity(intent,options.toBundle());
                } else {
                    startActivity(intent);
                }

                break;

            case R.id.materialCardView1 :
                intent = new Intent(this,ReceiveActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    tempv = v.findViewById(R.id.appCompatImageView1);
                    pair[0] = new Pair<View,String>(tempv,"transitionimage");

                    tempv = v.findViewById(R.id.textView1);
                    pair[1] = new Pair<View,String>(tempv,"transitiontext");

                    options = ActivityOptions.makeSceneTransitionAnimation(this,pair);

                    startActivity(intent,options.toBundle());
                } else {
                    startActivity(intent);
                }

                break;

            case R.id.materialCardView2 :
                intent = new Intent(this,ProblemSolveActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    tempv = v.findViewById(R.id.appCompatImageView2);
                    pair[0] = new Pair<View,String>(tempv,"transitionimage");

                    tempv = v.findViewById(R.id.textView2);
                    pair[1] = new Pair<View,String>(tempv,"transitiontext");

                    options = ActivityOptions.makeSceneTransitionAnimation(this,pair);

                    startActivity(intent,options.toBundle());
                } else {
                    startActivity(intent);
                }

                break;

            case R.id.materialCardView3 :
                intent = new Intent(this,FindItemActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    tempv = v.findViewById(R.id.appCompatImageView3);
                    pair[0] = new Pair<View,String>(tempv,"transitionimage");

                    tempv = v.findViewById(R.id.textView3);
                    pair[1] = new Pair<View,String>(tempv,"transitiontext");

                    options = ActivityOptions.makeSceneTransitionAnimation(this,pair);

                    startActivity(intent,options.toBundle());
                } else {
                    startActivity(intent);
                }

                break;
        }

    }
}
