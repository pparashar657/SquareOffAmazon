package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReceiveActivity extends AppCompatActivity implements View.OnClickListener {


    static Spinner trans_spinner;
    Spinner source_spinner;
    Spinner dest_spinner;
    TextView sourcetext,targettext;
    ConstraintLayout challanlayout;
    ConstraintLayout manualreceivelayout;
    static ConstraintLayout parent;
    MaterialButton autogeneratebutton,manualbutton,scanbutton,summarybutton,submitbutton,receive,submitcheck;
    static boolean isSCFC,ThreePSC,ThreePFC;
    TextInputEditText groupid,trackingid;
    public static String groupID="",target="",source="";
    ProgressBar progress;
    Intent intent;
    static List<Package> mypackages;

    static boolean flag;

    private static FirebaseFirestore db;
    ArrayAdapter<CharSequence> adapter;

    static CollectionReference packagesref;

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
        db = FirebaseFirestore.getInstance();
        mypackages = new ArrayList<Package>();
        packagesref = db.collection(Constants.PackageCollectionName);
        trans_spinner = (Spinner) findViewById(R.id.trans_spinner);
        source_spinner = (Spinner) findViewById(R.id.source_spinner);
        dest_spinner = (Spinner) findViewById(R.id.destination_spinner);
        progress = (ProgressBar) findViewById(R.id.progress);
        sourcetext = (TextView) findViewById(R.id.sourcetext);
        targettext = (TextView) findViewById(R.id.targettext);
        challanlayout = (ConstraintLayout) findViewById(R.id. challanlayout);
        manualreceivelayout = (ConstraintLayout) findViewById(R.id.manualreceivelayout);
        autogeneratebutton = (MaterialButton) findViewById(R.id.autogeneratebutton);
        manualbutton = (MaterialButton) findViewById(R.id.manualbutton);
        scanbutton = (MaterialButton) findViewById(R.id.scanbutton);
        summarybutton = (MaterialButton) findViewById(R.id.summarybutton);
        submitbutton = (MaterialButton) findViewById(R.id.submitbutton);
        submitcheck = (MaterialButton) findViewById(R.id.submitCheck);
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
        submitcheck.setOnClickListener(this);

    }

    private void setAdapter() {

        if(isSCFC || ThreePFC || ThreePSC){
            targettext.setVisibility(View.VISIBLE);
            dest_spinner.setVisibility(View.VISIBLE);
            challanlayout.setVisibility(View.VISIBLE);
            sourcetext.setVisibility(View.VISIBLE);
            source_spinner.setVisibility(View.VISIBLE);
            autogeneratebutton.setVisibility(View.VISIBLE);
            submitcheck.setVisibility(View.VISIBLE);
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
            submitcheck.setVisibility(View.GONE);

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

        } else{
            if(!isComplete()){

            }else if(v.getId() == R.id.submitCheck){

               if(isNetworkAvailable()){
                   if(isSCFC){
                       submitcheck.setVisibility(View.GONE);
                       progress.setVisibility(View.VISIBLE);
                       isValid();
                   }else {
                       manualbutton.setVisibility(View.VISIBLE);
                       scanbutton.setVisibility(View.VISIBLE);
                       summarybutton.setVisibility(View.VISIBLE);
                       submitbutton.setVisibility(View.VISIBLE);
                       submitcheck.setVisibility(View.GONE);
                       trans_spinner.setEnabled(false);
                       source_spinner.setEnabled(false);
                       dest_spinner.setEnabled(false);
                       groupid.setEnabled(false);
                       autogeneratebutton.setEnabled(false);
                   }
               }else{
                   Snackbar.make(parent,"No Connection Available ....",Snackbar.LENGTH_LONG).show();
               }

            } else {

                if(isNetworkAvailable()){
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

                }else{
                    Snackbar.make(parent,"No Connection Available ....",Snackbar.LENGTH_LONG).show();
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


    private void isValid() {

        // Checks wheather the groupid and Target node have a valid entry.
        groupID = groupid.getText().toString();
        target = dest_spinner.getSelectedItem().toString();
        packagesref.whereEqualTo("groupId",groupID).whereEqualTo("targetNode",target).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    manualbutton.setVisibility(View.VISIBLE);
                    scanbutton.setVisibility(View.VISIBLE);
                    summarybutton.setVisibility(View.VISIBLE);
                    submitbutton.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    trans_spinner.setEnabled(false);
                    source_spinner.setEnabled(false);
                    dest_spinner.setEnabled(false);
                    groupid.setEnabled(false);

                    for(QueryDocumentSnapshot d:queryDocumentSnapshots){
                        Package p = d.toObject(Package.class);
                        p.setId(d.getId());
                        mypackages.add(p);
                    }

                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                    builder.setTitle("Error :");
                    builder.setMessage("Cannot find matching Target station and Challan no. Please Try again! ");
                    builder.setPositiveButton("Ok", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                    alert.setCancelable(false);
                    submitcheck.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }

    private void receive(final String pkgid) {

        if(isSCFC){

            String id = Package.searchbyId(mypackages,pkgid);

            // Package Reconciled
            if(id != null){

                packagesref.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getString("reconcilationState").equals(Constants.Reconciled)){
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                            builder.setTitle("ERROR :");
                            builder.setMessage("Duplicate Package Received ... ");
                            builder.setPositiveButton("Ok",null);
                            final AlertDialog dlg = builder.create();
                            dlg.show();
                            dlg.setCancelable(false);
                            MediaPlayer mp = MediaPlayer.create(ReceiveActivity.this, R.raw.warning_sound);
                            mp.start();
                            packagesref.document(documentSnapshot.getId()).update("lastUpdatedTime",(System.currentTimeMillis()/1000));
                        }else {

                            packagesref.document(documentSnapshot.getId()).update("reconcilationState",Constants.Reconciled,"lastUpdatedTime",(System.currentTimeMillis()/1000));
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
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

                            MediaPlayer mp = MediaPlayer.create(ReceiveActivity.this, R.raw.success_sound);
                            mp.start();

                        }
                    }
                });
            }else {
                packagesref.whereEqualTo("trackingId",pkgid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot d:queryDocumentSnapshots){
                                Log.e("Check","Found the id in DB");
                                Package p = d.toObject(Package.class);
                                p.setId(d.getId());

                                if(p.getTargetNode().equals(target)){

                                    packagesref.document(p.getId()).update("reconcilationState",Constants.NotInChallan,"groupId",groupID,"lastUpdatedTime",(System.currentTimeMillis()/1000)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                                            builder.setTitle("Error :");
                                            builder.setMessage("This Package is NOT IN CHALLAN ... ");
                                            final AlertDialog dlg = builder.create();
                                            dlg.show();

                                            final Timer t = new Timer();
                                            t.schedule(new TimerTask() {
                                                public void run() {
                                                    dlg.dismiss();
                                                    t.cancel();
                                                }
                                            }, 2000);

                                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.warning_sound);
                                            mp.start();
                                        }
                                    });

                                }else{
                                    packagesref.document(p.getId()).update("reconcilationState",Constants.Missort,"lastUpdatedTime",(System.currentTimeMillis()/1000),"currentNode",target).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                                            builder.setTitle("Error :");
                                            Log.e("Check","Found the id with matching different Target i.e. Missort");
                                            builder.setMessage("This Package is MISSORT ... ");
                                            final AlertDialog dlg = builder.create();
                                            dlg.show();

                                            final Timer t = new Timer();
                                            t.schedule(new TimerTask() {
                                                public void run() {
                                                    dlg.dismiss();
                                                    t.cancel();
                                                }
                                            }, 2000);

                                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.warning_sound);
                                            mp.start();
                                        }
                                    });
                                }
                            }
                        }else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                            builder.setTitle("Error :");
                            builder.setMessage("Unknown Package, do you want to accept it ... ");
                            builder.setPositiveButton("Yes, Accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    packagesref.add(new Package(pkgid,Constants.NotApplicable,target,groupID,Constants.Unknown,Constants.PendingProcessing,Constants.scfc,Constants.Unknown,target,(System.currentTimeMillis()/1000))).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                                            builder.setTitle("Error :");
                                            builder.setMessage("This Package is Unknown ... ");
                                            final AlertDialog dlg = builder.create();
                                            dlg.show();

                                            final Timer t = new Timer();
                                            t.schedule(new TimerTask() {
                                                public void run() {
                                                    dlg.dismiss();
                                                    t.cancel();
                                                }
                                            }, 2000);

                                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.warning_sound);
                                            mp.start();
                                        }
                                    });
                                }
                            });
                            builder.setNeutralButton("Cancel",null);
                            AlertDialog dlg = builder.create();
                            dlg.show();
                            dlg.setCancelable(false);
                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.warning_sound);
                            mp.start();

                        }
                    }
                });
            }
        }else{

            packagesref.add(new Package(pkgid,source,target,groupID,Constants.Reconciled,Constants.PendingProcessing,trans_spinner.getSelectedItem().toString(),Constants.Unknown,target,(System.currentTimeMillis()/1000))).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
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

                    MediaPlayer mp = MediaPlayer.create(ReceiveActivity.this, R.raw.success_sound);
                    mp.start();
                }
            });

        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void submit() {


    }
}
