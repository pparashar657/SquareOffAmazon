package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ReceiveActivity extends AppCompatActivity implements View.OnClickListener {


    Spinner trans_spinner;
    Spinner source_spinner;
    Spinner dest_spinner;
    TextView sourcetext,targettext;
    ConstraintLayout challanlayout;
    ConstraintLayout manualreceivelayout;
    static ConstraintLayout parent;
    MaterialButton autogeneratebutton,manualbutton,scanbutton,summarybutton,submitbutton,receive;
    boolean isSCFC,ThreePSC,ThreePFC;
    TextInputEditText groupid,trackingid;
    public static String groupID="",target="",source="";
    Intent intent;

    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        initialize();
    }

    private void initialize() {


        isSCFC = false;
        ThreePFC = false;
        ThreePSC = false;

        parent = findViewById(R.id.receiveparent);
        trans_spinner = (Spinner) findViewById(R.id.trans_spinner);
        source_spinner = (Spinner) findViewById(R.id.source_spinner);
        dest_spinner = (Spinner) findViewById(R.id.destination_spinner);
        sourcetext = (TextView) findViewById(R.id.sourcetext);
        targettext = (TextView) findViewById(R.id.targettext);
        challanlayout = (ConstraintLayout) findViewById(R.id. challanlayout);
        manualreceivelayout = (ConstraintLayout) findViewById(R.id.manualreceivelayout);
        autogeneratebutton = (MaterialButton) findViewById(R.id.autogeneratebutton);
        manualbutton = (MaterialButton) findViewById(R.id.manualbutton);
        scanbutton = (MaterialButton) findViewById(R.id.scanbutton);
        summarybutton = (MaterialButton) findViewById(R.id.summarybutton);
        submitbutton = (MaterialButton) findViewById(R.id.submitbutton);
        groupid = (TextInputEditText) findViewById(R.id.newtrackingid);
        trackingid = (TextInputEditText) findViewById(R.id.trackingid);
        receive = (MaterialButton) findViewById(R.id.receive);


        trans_spinner = (Spinner)findViewById(R.id.trans_spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.Receive_transaction_type, R.layout.textlayoutspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trans_spinner.setAdapter(adapter);
        trans_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    isSCFC = false;
                    ThreePSC = false;
                    ThreePFC = false;
                    setAdapter();

                }
                if(position == 1){
                    isSCFC = true;
                    ThreePSC = false;
                    ThreePFC = false;
                    setAdapter();

                }
                if(position == 2){
                    isSCFC = false;
                    ThreePSC = true;
                    ThreePFC = false;
                    setAdapter();

                }
                if(position == 3){
                    isSCFC = false;
                    ThreePSC = false;
                    ThreePFC = true;
                    setAdapter();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        manualbutton.setOnClickListener(this);
        autogeneratebutton.setOnClickListener(this);
        scanbutton.setOnClickListener(this);
        summarybutton.setOnClickListener(this);
        submitbutton.setOnClickListener(this);
        receive.setOnClickListener(this);

    }

    private void setAdapter() {

        if(isSCFC || ThreePFC || ThreePSC){
            targettext.setVisibility(View.VISIBLE);
            dest_spinner.setVisibility(View.VISIBLE);
            challanlayout.setVisibility(View.VISIBLE);
            manualbutton.setVisibility(View.VISIBLE);
            scanbutton.setVisibility(View.VISIBLE);
            summarybutton.setVisibility(View.VISIBLE);
            submitbutton.setVisibility(View.VISIBLE);
            sourcetext.setVisibility(View.VISIBLE);
            source_spinner.setVisibility(View.VISIBLE);
            autogeneratebutton.setVisibility(View.VISIBLE);
            groupid.setText("");
            trackingid.setText("");

            if(isSCFC){
                sourcetext.setVisibility(View.GONE);
                source_spinner.setVisibility(View.GONE);
                autogeneratebutton.setVisibility(View.GONE);

                dest_spinner = (Spinner)findViewById(R.id.destination_spinner);
                adapter = ArrayAdapter.createFromResource(this,R.array.FCs, R.layout.textlayoutspinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dest_spinner.setAdapter(adapter);
            }

            source_spinner = (Spinner)findViewById(R.id.source_spinner);
            adapter = ArrayAdapter.createFromResource(this,R.array.ThreePs, R.layout.textlayoutspinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            source_spinner.setAdapter(adapter);

            if(ThreePSC){
                dest_spinner = (Spinner)findViewById(R.id.destination_spinner);
                adapter = ArrayAdapter.createFromResource(this,R.array.SCs, R.layout.textlayoutspinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dest_spinner.setAdapter(adapter);
            }else if(ThreePFC){
                dest_spinner = (Spinner)findViewById(R.id.destination_spinner);
                adapter = ArrayAdapter.createFromResource(this,R.array.FCs, R.layout.textlayoutspinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dest_spinner.setAdapter(adapter);
            }

        }else{

            targettext.setVisibility(View.GONE);
            dest_spinner.setVisibility(View.GONE);
            challanlayout.setVisibility(View.GONE);
            manualbutton.setVisibility(View.GONE);
            scanbutton.setVisibility(View.GONE);
            summarybutton.setVisibility(View.GONE);
            submitbutton.setVisibility(View.GONE);
            sourcetext.setVisibility(View.GONE);
            source_spinner.setVisibility(View.GONE);
            autogeneratebutton.setVisibility(View.GONE);

        }

    }

    @Override
    public void onBackPressed() {

        if(trans_spinner.getSelectedItem().toString().equals(Constants.choose)){
            super.onBackPressed();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert :");
            builder.setMessage("Are you sure, you want to exit? ");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ReceiveActivity.super.onBackPressed();
                }
            });
            builder.setNeutralButton("Cancel",null);
            builder.create().show();
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.autogeneratebutton){

            source = source_spinner.getSelectedItem().toString();
            target = dest_spinner.getSelectedItem().toString();
            if(source.equals(Constants.choose)){
                Snackbar.make(parent,"Please enter the Source Node ...",Snackbar.LENGTH_LONG).show();
            } else if(target.equals(Constants.choose)){
                Snackbar.make(parent,"Please enter the Target Node ...",Snackbar.LENGTH_LONG).show();
            } else {
                String curTime = (Calendar.getInstance().getTime()).toString().replaceAll(" ","-").substring(0,19);
                groupID = source+"_"+target+"_"+curTime;
                groupid.setText(groupID);
            }

        }
        else{
            if(!isComplete()){

            }else if(!isValid()){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error :");
                builder.setMessage("Cannot find matching Target station and Challan no. Please Try again! ");
                builder.setPositiveButton("Ok", null);
                AlertDialog alert = builder.create();
                alert.show();
                alert.setCancelable(false);

            } else {

                switch(v.getId()){

                    case R.id.manualbutton:{
                        if(manualreceivelayout.getVisibility() == View.VISIBLE){
                            manualreceivelayout.setVisibility(View.GONE);
                        }else{
                            manualreceivelayout.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                    case R.id.receive:{
                        if(trackingid.getText().toString().equals("")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Error :");
                            builder.setMessage("Please Enter the tracking Id.. ");
                            builder.setPositiveButton("Ok", null);
                            builder.create().show();
                        }else{
                            receive(trackingid.getText().toString());
                        }
                        break;
                    }
                    case R.id.scanbutton:{
                        scan();
                        break;
                    }
                    case R.id.summarybutton:{
                        summary();
                        break;
                    }
                    case R.id.submitbutton:{

                        submit();
                        break;
                    }

                }

            }
        }




    }
    private void scan() {
        intent = new Intent(this,ScanActivity.class);
        intent.putExtra("Type","Receive");
        startActivity(intent);
    }



    private boolean isComplete(){


        if(!isSCFC){

            if(source_spinner.getSelectedItem().toString().equals(Constants.choose)){
                Snackbar.make(parent,"Please enter the Source Node ...",Snackbar.LENGTH_LONG).show();
                return false;
            }
        }

        if(dest_spinner.getSelectedItem().toString().equals(Constants.choose)){
            Snackbar.make(parent,"Please enter the Target Node ...",Snackbar.LENGTH_LONG).show();
            return false;
        }else if(groupid.getText().toString().equals("")){
            Snackbar.make(parent,"Please enter a Valid Challan No ...",Snackbar.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    private void summary() {
        intent =  new Intent(this,ReceiveSummaryActivity.class);
        startActivity(intent);

    }


    private boolean isValid() {

        // Checks wheather the groupid and Target node have a valid entry.

        groupID = groupid.getText().toString();
        target = dest_spinner.getSelectedItem().toString();


        return true;
    }

    private void receive(String toString) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success :");
        builder.setMessage("Package Reconciled... ");
        final AlertDialog dlg = builder.create();
        dlg.show();

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dlg.dismiss();
                t.cancel();
            }
        }, 2000);

        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.success_sound);
        mp.start();

    }


    private void submit() {



    }
}
