package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import fr.ganfra.materialspinner.MaterialSpinner;

public class Problem extends AppCompatActivity implements View.OnClickListener {

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
    String idstring="",lpnstring="",quantitystring="",ticketstring="",processedstring="",markedstring="",newidstring="",commentsstring="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        initialize();

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


        heading = (TextView) findViewById(R.id.heading);
        parent = (ConstraintLayout) findViewById(R.id.par);
        submit = (MaterialButton) findViewById(R.id.materialButton2) ;
        camera = (FloatingActionButton) findViewById(R.id.camera);
        image = (ImageView) findViewById(R.id.image);
        submit .setOnClickListener(this);
        camera.setOnClickListener(this);
        image.setOnClickListener(this);
        trackingidlayout = (ConstraintLayout) findViewById(R.id.constraintLayout4);
        newtrackinglayout = (ConstraintLayout) findViewById(R.id.constraintLayout10);
        trackingid = (TextInputEditText) findViewById(R.id.trackingid1);
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

                idstring = trackingid.getText().toString();
                lpnstring = lpn.getText().toString();
                quantitystring = quantity.getText().toString();
                ticketstring = ticket.getText().toString();
                processedstring = processedat.getSelectedItem().toString();
                markedstring = marked.getSelectedItem().toString();
                newidstring = newtrackingid.getText().toString();
                commentsstring = comments.getText().toString();

                if(iscomplete()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Success :");
                    builder.setMessage("Record Added Successfully ...");
                    builder.setPositiveButton("Ok", null);
                    builder.create().show();
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.success_sound);
                    mp.start();

                    saveonDB();
                }
                break;
            }

            case R.id.camera:{
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                break;
            }
        }

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
        }else{
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
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
            Snackbar.make(parent,"Image Deleted Successfully ...",Snackbar.LENGTH_SHORT).show();
            return true;
        }else{
            return super.onContextItemSelected(item);
        }
    }

    private void saveonDB() {


    }
}
