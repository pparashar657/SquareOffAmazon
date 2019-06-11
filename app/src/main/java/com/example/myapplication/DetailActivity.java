package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class DetailActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SummaryRecyclerAdapter adapter;
    TextView head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        head = findViewById(R.id.head);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        init();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

    }

    private void init() {

        Intent intent = getIntent();
        String type = intent.getStringExtra(Constants.Type);

        if(type.equals(Constants.TotalSent)){

            head.setText("  Total Sent ");
            adapter = new SummaryRecyclerAdapter(ReceiveSummaryActivity.totalliststring);

        }else if(type.equals(Constants.Reconciled)){

            head.setText("  Reconciled ");
            adapter = new SummaryRecyclerAdapter(ReceiveSummaryActivity.reconciledlist);

        }else if(type.equals(Constants.Unreconciled)){

            head.setText("  UnReconciled ");
            adapter = new SummaryRecyclerAdapter(ReceiveSummaryActivity.unreconciledlist);

        }else {

            head.setText("  Extra Reconciled ");
            adapter = new SummaryRecyclerAdapter(ReceiveSummaryActivity.extralist);

        }

    }
}
