package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ReceiveActivity extends AppCompatActivity {


    Spinner trans_spinner;
    Spinner source_spinner;
    Spinner dest_spinner;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        trans_spinner = (Spinner)findViewById(R.id.trans_spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.Receive_transaction_type, R.layout.textlayoutspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trans_spinner.setAdapter(adapter);


        setAdapter();

    }

    private void setAdapter() {



        source_spinner = (Spinner)findViewById(R.id.source_spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.ThreePs, R.layout.textlayoutspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        source_spinner.setAdapter(adapter);

        dest_spinner = (Spinner)findViewById(R.id.destination_spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.FCs, R.layout.textlayoutspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dest_spinner.setAdapter(adapter);

    }
}
