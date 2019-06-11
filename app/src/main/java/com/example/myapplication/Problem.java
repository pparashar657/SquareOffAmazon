package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class Problem extends AppCompatActivity implements View.OnClickListener {


    private static final int REQUEST_EXTERNAL_WRITE = 111;
    private FirebaseFirestore db;
    private static final int REQUEST_IMAGE_CAPTURE = 109;
    boolean hastrackingid = false;
    ConstraintLayout trackingidlayout,newtrackinglayout;
    ConstraintLayout parent;
    TextView heading;
    TextInputEditText trackingid,lpn,quantity,ticket,newtrackingid,comments;
    MaterialSpinner processedat,marked;
    ArrayAdapter<CharSequence> adapter;
    MaterialButton submit;
    FloatingActionButton camera;
    ImageView image;
    Bitmap imageBitmap;
    ProgressBar progress;
    String storageId;
    String idstring="",lpnstring="",quantitystring="",ticketstring="",processedstring="",markedstring="",newidstring="",commentsstring="";
    CollectionReference packageref;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        initialize();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_WRITE);
            }
        }

        hastrackingid = getIntent().getBooleanExtra("tarckingid",false);
        if(!hastrackingid){
            heading.setText("Problem Solve-Without Tracking Id");
            trackingidlayout.setVisibility(View.GONE);
            newtrackinglayout.setVisibility(View.GONE);
            adapter = ArrayAdapter.createFromResource(this,R.array.marked_under2, R.layout.textlayoutspinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            marked.setAdapter(adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_WRITE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Snackbar.make(parent,"Permission is Necessary ....",Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert :");
        builder.setMessage("Are you sure, you want to exit? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Problem.super.onBackPressed();
            }
        });
        builder.setNeutralButton("Cancel",null);
        builder.create().show();
    }

    private void initialize() {

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        heading = (TextView) findViewById(R.id.heading);
        parent = (ConstraintLayout) findViewById(R.id.par);
        submit = (MaterialButton) findViewById(R.id.materialButton2) ;
        camera = (FloatingActionButton) findViewById(R.id.camera);
        image = (ImageView) findViewById(R.id.image);
        submit .setOnClickListener(this);
        camera.setOnClickListener(this);
        image.setOnClickListener(this);
        progress = (ProgressBar) findViewById(R.id.progress);
        trackingidlayout = (ConstraintLayout) findViewById(R.id.constraintLayout4);
        newtrackinglayout = (ConstraintLayout) findViewById(R.id.constraintLayout10);
        trackingid = (TextInputEditText) findViewById(R.id.track);
        lpn = (TextInputEditText) findViewById(R.id.lpn);
        quantity = (TextInputEditText) findViewById(R.id.quantity);
        ticket = (TextInputEditText) findViewById(R.id.ticket);
        newtrackingid = (TextInputEditText) findViewById(R.id.newtrackingid);
        comments = (TextInputEditText) findViewById(R.id.comments);
        processedat = (MaterialSpinner) findViewById(R.id.process_spinner);
        marked = (MaterialSpinner) findViewById(R.id.marked_spinner);

        adapter = ArrayAdapter.createFromResource(this,R.array.FCs, R.layout.textlayoutspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        processedat.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(this,R.array.marked_under, R.layout.textlayoutspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marked.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {


        switch(v.getId()){

            case R.id.image:{

                image.setOnCreateContextMenuListener(this);
                break;
            }

            case R.id.materialButton2:{

                if(isNetworkAvailable()){
                    idstring = trackingid.getText().toString();
                    lpnstring = lpn.getText().toString();
                    quantitystring = quantity.getText().toString();
                    ticketstring = ticket.getText().toString();
                    processedstring = processedat.getSelectedItem().toString();
                    markedstring = marked.getSelectedItem().toString();
                    newidstring = newtrackingid.getText().toString();
                    commentsstring = comments.getText().toString();

                    if(iscomplete()){
                        saveonDB();
                    }
                }else{
                    Snackbar.make(parent,"No Connection available .....",Snackbar.LENGTH_LONG).show();
                }
                break;
            }

            case R.id.camera:{
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                break;
            }
        }

    }

    private void clearall() {
        trackingid.setText("");
        lpn.setText("");
        quantity.setText("");
        ticket.setText("");
        processedat.setSelection(0);
        marked.setSelection(0);
        newtrackingid.setText("");
        comments.setText("");
        image.setImageResource(0);
    }

    private boolean iscomplete() {

        if(hastrackingid && idstring.equals("")){
            Snackbar.make(parent,"Please enter a Valid Tracking ID ...", Snackbar.LENGTH_LONG).show();
            return false;
        }else if (lpnstring.equals("")){
            Snackbar.make(parent,"Please enter a XOO/BOO/LPN ...", Snackbar.LENGTH_LONG).show();
            return false;
        }else if(processedstring.equals(Constants.choose)){
            Snackbar.make(parent,"Please provide a Node name ...", Snackbar.LENGTH_LONG).show();
            return false;
        }else if(markedstring.equals(Constants.choose)){
            Snackbar.make(parent,"Please provide a Marked Under category ...", Snackbar.LENGTH_LONG).show();
            return false;
        }else if(imageBitmap == null){
            Snackbar.make(parent,"Please take an image of the package ...", Snackbar.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            imageBitmap = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(imageBitmap);

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0,102,0,"Delete Image ...");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == 102){
            image.setImageResource(0);
            imageBitmap = null;
            Snackbar.make(parent,"Image Deleted Successfully ...",Snackbar.LENGTH_SHORT).show();
            return true;
        }else{
            return super.onContextItemSelected(item);
        }
    }

    private void saveonDB() {

        progress.setVisibility(View.VISIBLE);
        submit.setVisibility(View.INVISIBLE);

        if(hastrackingid ){
            updateStatus();
        }

        if(!hastrackingid){
            update();
        }
    }

    private void update() {

        CollectionReference problemref = db.collection(Constants.ProblemCollectionName);
        ProblemDetail prblm = new ProblemDetail(idstring,lpnstring,quantitystring,processedstring,markedstring,newidstring,commentsstring,(System.currentTimeMillis())/1000);
        Log.e("object",prblm.getTrackingId());
        problemref.add(prblm).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                storageId = documentReference.getId();
                StorageReference filepath = storageReference.child("Photo").child(storageId);
                filepath.putFile(getImageUri(Problem.this,imageBitmap)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Problem.this);
                        builder.setTitle("Success :");
                        builder.setMessage("Record Added Successfully ...");
                        builder.setPositiveButton("Ok", null);
                        AlertDialog alert = builder.create();
                        alert.show();
                        alert.setCancelable(false);
                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.success_sound);
                        mp.start();
                        progress.setVisibility(View.INVISIBLE);
                        submit.setVisibility(View.VISIBLE);
                        clearall();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(parent,e.getMessage(),Snackbar.LENGTH_LONG).show();
                        progress.setVisibility(View.INVISIBLE);
                        submit.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void updateStatus() {

        packageref = db.collection(Constants.PackageCollectionName);
        packageref.whereEqualTo("trackingId",idstring).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if(queryDocumentSnapshots.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Problem.this);
                    builder.setTitle("Error :");
                    builder.setMessage(" Tracking Id not Found ...");
                    builder.setPositiveButton("Ok", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                    alert.setCancelable(false);
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.warning_sound);
                    mp.start();
                    progress.setVisibility(View.INVISIBLE);
                    submit.setVisibility(View.VISIBLE);

                } else {
                    Log.e("Size of query",""+queryDocumentSnapshots.size());
                    for(QueryDocumentSnapshot d:queryDocumentSnapshots){
                        packageref.document(d.getId()).update("processingState",Constants.Processed,"currentNode",processedstring,"lastUpdatedTime",(System.currentTimeMillis()/1000));
                    }
                    update();

                }
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
