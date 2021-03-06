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
import android.view.WindowManager;
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
    public static String groupID="",target="",source="",shipment="";
    ProgressBar progress,progressreceive;
    Intent intent;
    static List<Package> mypackages;
    static Spinner shipmentType;
    static int pendingShipments = 0;

    static boolean flag;

    private static FirebaseFirestore db;
    ArrayAdapter<CharSequence> adapter;

    static CollectionReference packagesref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initialize();
    }

    private void initialize() {


        isSCFC = false;
        ThreePFC = false;
        ThreePSC = false;
        pendingShipments = 0;

        parent = findViewById(R.id.receiveparent);
        db = FirebaseFirestore.getInstance();
        mypackages = new ArrayList<Package>();
        packagesref = db.collection(Constants.PackageCollectionName);
        trans_spinner = (Spinner) findViewById(R.id.trans_spinner);
        source_spinner = (Spinner) findViewById(R.id.source_spinner);
        shipmentType = (Spinner) findViewById(R.id.shipmenttype);
        dest_spinner = (Spinner) findViewById(R.id.destination_spinner);
        progress = (ProgressBar) findViewById(R.id.progress);
        progressreceive = (ProgressBar) findViewById(R.id.progressreceive);
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

        shipmentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    shipment = Constants.ShipmentType;
                }else if(position == 1){
                    shipment = Constants.Regular;
                }else if (position == 2){
                    shipment = Constants.Damaged;
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
            shipmentType.setVisibility(View.VISIBLE);
            submitcheck.setVisibility(View.VISIBLE);
            groupid.setText("");
            trackingid.setText("");

            if(isSCFC){
                sourcetext.setVisibility(View.GONE);
                source_spinner.setVisibility(View.GONE);
                autogeneratebutton.setVisibility(View.GONE);
                shipmentType.setVisibility(View.GONE);

                dest_spinner = (Spinner)findViewById(R.id.destination_spinner);
                adapter = ArrayAdapter.createFromResource(this,R.array.FCs, R.layout.textlayoutspinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dest_spinner.setAdapter(adapter);
            }

            source_spinner = (Spinner)findViewById(R.id.source_spinner);
            adapter = ArrayAdapter.createFromResource(this,R.array.ThreePs, R.layout.textlayoutspinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            source_spinner.setAdapter(adapter);

            shipmentType = (Spinner)findViewById(R.id.shipmenttype);
            adapter = ArrayAdapter.createFromResource(this,R.array.shipmentTypeReceive, R.layout.textlayoutspinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            shipmentType.setAdapter(adapter);


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
            shipmentType.setVisibility(View.GONE);

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

                source = source_spinner.getSelectedItem().toString();
                target = dest_spinner.getSelectedItem().toString();
                groupID = groupid.getText().toString();
                shipment = shipmentType.getSelectedItem().toString();

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
                                receive.setVisibility(View.INVISIBLE);
                                progressreceive.setVisibility(View.VISIBLE);
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

        if(!(isSCFC) && shipmentType.getSelectedItem().toString().equals(Constants.ShipmentType)){
            Snackbar.make(parent,"Please enter the Shipment Type ...",Snackbar.LENGTH_LONG).show();
            receive.setVisibility(View.VISIBLE);
            progressreceive.setVisibility(View.INVISIBLE);
        }else{

            intent = new Intent(this,ScanActivity.class);
            intent.putExtra("Type","Receive");
            startActivity(intent);

        }
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
        packagesref.whereEqualTo("groupId",groupID).whereEqualTo("targetNode",target).whereEqualTo("transactionType",trans_spinner.getSelectedItem().toString()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                boolean isOriginal = false;
                for(QueryDocumentSnapshot d:queryDocumentSnapshots){
                    Package p = d.toObject(Package.class);
                    p.setId(d.getId());
                    mypackages.add(p);
                    if(p.getReconcilationState().equals(Constants.Intransit)){
                        pendingShipments++;
                        isOriginal = true;
                    }else if(p.getReconcilationState().equals(Constants.Reconciled)){
                        isOriginal = true;
                    }

                }

                if(isOriginal){
                    manualbutton.setVisibility(View.VISIBLE);
                    scanbutton.setVisibility(View.VISIBLE);
                    summarybutton.setVisibility(View.VISIBLE);
                    submitbutton.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    trans_spinner.setEnabled(false);
                    source_spinner.setEnabled(false);
                    dest_spinner.setEnabled(false);
                    groupid.setEnabled(false);


                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                    builder.setTitle("Error :");
                    builder.setMessage("Cannot find matching Target station and Challan no. Please Try again! ");
                    builder.setPositiveButton("Ok", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                    alert.setCancelable(false);
                    MediaPlayer mp = MediaPlayer.create(ReceiveActivity.this, R.raw.warning_sound);
                    mp.start();
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

                        if(!documentSnapshot.getString("reconcilationState").equals(Constants.Intransit)){

                            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                            builder.setTitle("ERROR :");
                            builder.setMessage("Duplicate Package Received ... ");
                            builder.setPositiveButton("Ok",null);
                            final AlertDialog dlg = builder.create();
                            dlg.show();
                            dlg.setCancelable(false);
                            trackingid.setText("");
                            MediaPlayer mp = MediaPlayer.create(ReceiveActivity.this, R.raw.warning_sound);
                            mp.start();
                            packagesref.document(documentSnapshot.getId()).update("lastUpdatedTime",(System.currentTimeMillis()/1000));

                            receive.setVisibility(View.VISIBLE);
                            progressreceive.setVisibility(View.INVISIBLE);


                        }else {

                            packagesref.document(documentSnapshot.getId()).update("reconcilationState",Constants.Reconciled,"lastUpdatedTime",(System.currentTimeMillis()/1000));
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                            builder.setTitle("Success :");
                            builder.setMessage("Package Reconciled... ");
                            pendingShipments--;
                            Log.e("Shipment",pendingShipments+"");
                            builder.setPositiveButton("Ok",null);
                            final AlertDialog dlg = builder.create();
                            dlg.show();
                            dlg.setCancelable(false);
                            trackingid.setText("");

                            MediaPlayer mp = MediaPlayer.create(ReceiveActivity.this, R.raw.success_sound);
                            mp.start();


                            receive.setVisibility(View.VISIBLE);
                            progressreceive.setVisibility(View.INVISIBLE);

                        }
                    }
                });
            }else {

                packagesref.whereEqualTo("trackingId",pkgid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()){

                            List<Package> list = new ArrayList<Package>();

                            for(QueryDocumentSnapshot d:queryDocumentSnapshots) {

                                Package p = d.toObject(Package.class);
                                p.setId(d.getId());
                                list.add(p);

                            }
                            boolean done = false;

                            Package.sortByTime(list);

                            for(Package p:list){

                                if(!done){

                                    if(p.getGroupId().equals(groupID) && !(p.getReconcilationState().equals(Constants.Intransit))){

                                        done = true;

                                        AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                                        builder.setTitle("ERROR :");
                                        builder.setMessage("Duplicate Package Received ... ");
                                        builder.setPositiveButton("Ok",null);
                                        final AlertDialog dlg = builder.create();
                                        dlg.show();
                                        dlg.setCancelable(false);
                                        trackingid.setText("");
                                        MediaPlayer mp = MediaPlayer.create(ReceiveActivity.this, R.raw.warning_sound);
                                        mp.start();
                                        packagesref.document(p.getId()).update("lastUpdatedTime",(System.currentTimeMillis()/1000));

                                        receive.setVisibility(View.VISIBLE);
                                        progressreceive.setVisibility(View.INVISIBLE);

                                    }else{


                                        if(p.getTargetNode().equals(target)){

                                            done = true;

                                            Package p1 = new Package(pkgid,p.getSourceNode(),p.getTargetNode(),groupID,Constants.NotInChallan,Constants.PendingProcessing,Constants.scfc,p.getShipmenttype(),target,(System.currentTimeMillis()/1000));
                                            packagesref.add(p1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                                                    builder.setTitle("Error :");
                                                    builder.setMessage("This Package is NOT IN CHALLAN ... ");
                                                    builder.setPositiveButton("Ok",null);
                                                    final AlertDialog dlg = builder.create();
                                                    dlg.show();
                                                    dlg.setCancelable(false);

                                                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.warning_sound);
                                                    mp.start();
                                                    trackingid.setText("");

                                                    receive.setVisibility(View.VISIBLE);
                                                    progressreceive.setVisibility(View.INVISIBLE);
                                                }

                                            });

                                        }else{

                                            done = true;

                                            Package p1 = new Package(pkgid,p.getSourceNode(),p.getTargetNode(),groupID,Constants.Missort,Constants.PendingProcessing,Constants.scfc,p.getShipmenttype(),target,(System.currentTimeMillis()/1000));
                                            packagesref.add(p1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                                                    builder.setTitle("Error :");
                                                    builder.setMessage("This Package is MISSORT ... ");
                                                    builder.setPositiveButton("Ok",null);
                                                    final AlertDialog dlg = builder.create();
                                                    dlg.show();
                                                    dlg.setCancelable(false);

                                                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.warning_sound);
                                                    mp.start();
                                                    trackingid.setText("");
                                                    receive.setVisibility(View.VISIBLE);
                                                    progressreceive.setVisibility(View.INVISIBLE);
                                                }
                                            });

                                        }

                                    }

                                }

                            }

                        }else {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                            builder.setTitle("Error :");
                            builder.setMessage("Unknown Package, do you want to accept it ... ");
                            builder.setPositiveButton("Yes, Accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    packagesref.add(new Package(pkgid,Constants.Unknown,Constants.Unknown,groupID,Constants.Unknown,Constants.PendingProcessing,Constants.scfc,Constants.Unknown,target,(System.currentTimeMillis()/1000))).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                                            builder.setTitle("Error :");
                                            builder.setMessage("This Package is Unknown ... ");
                                            builder.setPositiveButton("Ok",null);
                                            final AlertDialog dlg = builder.create();
                                            dlg.show();
                                            dlg.setCancelable(false);

                                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.warning_sound);
                                            mp.start();
                                            trackingid.setText("");

                                            receive.setVisibility(View.VISIBLE);
                                            progressreceive.setVisibility(View.INVISIBLE);

                                        }
                                    });
                                }
                            });
                            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    receive.setVisibility(View.VISIBLE);
                                    progressreceive.setVisibility(View.INVISIBLE);
                                }
                            });
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

            if(shipmentType.getSelectedItem().toString().equals(Constants.ShipmentType)){
                Snackbar.make(parent,"Please enter the Shipment Type ...",Snackbar.LENGTH_LONG).show();
                receive.setVisibility(View.VISIBLE);
                progressreceive.setVisibility(View.INVISIBLE);
            }else{

                packagesref.whereEqualTo("trackingId",pkgid).whereEqualTo("targetNode",target).whereEqualTo("groupId",groupID).whereEqualTo("transactionType",trans_spinner.getSelectedItem().toString()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()){

                            for(QueryDocumentSnapshot d:queryDocumentSnapshots){

                                Package p = d.toObject(Package.class);
                                p.setId(d.getId());

                                if(!p.getReconcilationState().equals(Constants.Intransit)){

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                                    builder.setTitle("ERROR :");
                                    builder.setMessage("Duplicate Package Received ... ");
                                    builder.setPositiveButton("Ok",null);
                                    final AlertDialog dlg = builder.create();
                                    dlg.show();
                                    dlg.setCancelable(false);
                                    trackingid.setText("");
                                    MediaPlayer mp = MediaPlayer.create(ReceiveActivity.this, R.raw.warning_sound);
                                    mp.start();
                                    packagesref.document(p.getId()).update("lastUpdatedTime",(System.currentTimeMillis()/1000));
                                    trackingid.setText("");

                                    receive.setVisibility(View.VISIBLE);
                                    progressreceive.setVisibility(View.INVISIBLE);

                                }else{
                                    Snackbar.make(parent,"Something Went Wrong ...",Snackbar.LENGTH_LONG).show();
                                    trackingid.setText("");
                                    receive.setVisibility(View.VISIBLE);
                                    progressreceive.setVisibility(View.INVISIBLE);

                                }

                            }

                        }else{

                            packagesref.add(new Package(pkgid,source,target,groupID,Constants.Reconciled,Constants.PendingProcessing,trans_spinner.getSelectedItem().toString(),shipment,target,(System.currentTimeMillis()/1000))).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                                    builder.setTitle("Success :");
                                    builder.setMessage("Package Reconciled... ");
                                    final AlertDialog dlg = builder.create();
                                    trackingid.setText("");
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
                                    trackingid.setText("");

                                    receive.setVisibility(View.VISIBLE);
                                    progressreceive.setVisibility(View.INVISIBLE);

                                }
                            });

                        }
                    }
                });


            }


        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void submit() {

        if(pendingShipments > 0){

            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
            builder.setTitle("Alert :");
            builder.setMessage("You still have few pending shipments to scan under this challan. Do you want to exit? ");
            Log.e("shipment count",pendingShipments+"");
            builder.setPositiveButton("Yes Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ReceiveActivity.this,HomeActivity.class));
                    finish();
                }
            }).setNeutralButton("Cancel",null);
            AlertDialog dlg = builder.create();
            dlg.show();
            dlg.setCancelable(false);

        }else{

            startActivity(new Intent(ReceiveActivity.this,HomeActivity.class));
            finish();

        }

    }

}
