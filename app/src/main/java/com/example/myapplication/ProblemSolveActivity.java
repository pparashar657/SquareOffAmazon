package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

public class ProblemSolveActivity extends AppCompatActivity implements View.OnClickListener {


    MaterialCardView hastrackingid,hasnttrackingid;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_solve);
        intent = new Intent(this,Problem.class);
        hasnttrackingid = (MaterialCardView) findViewById(R.id.hasnttrackingid);
        hastrackingid = (MaterialCardView) findViewById(R.id.hastrackingid);
        hastrackingid.setOnClickListener(this);
        hasnttrackingid.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
       if(v.getId() == R.id.hastrackingid){
           intent.putExtra("tarckingid",true);
       }else if(v.getId() == R.id.hasnttrackingid){
           intent.putExtra("tarckingid",false);
       }
       startActivity(intent);
       finish();
    }
}
