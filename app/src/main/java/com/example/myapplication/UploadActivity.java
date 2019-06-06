package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    Spinner trans_spinner;
    Spinner source_spinner;
    Spinner dest_spinner;
    Spinner shipment_spinner;
    ArrayAdapter<CharSequence> adapter;
    MaterialButton choose_file;
    MaterialButton scan_items;
    MaterialButton summary;
    MaterialButton upload;
    MaterialButton home;
    TextInputEditText groupId;
    public static List<String> trId;
    Intent intent;

    public static final String choose = "Select ....";
    String transaction_type = "";
    String source = "";
    String destination = "";
    String shipment_type = "";
    String groupid = "";

    public static final int Request_code_file = 10;
    public static final int Permission_Request = 0;
    int file_size = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        choose_file = (MaterialButton) findViewById(R.id.choose_file);
        scan_items = (MaterialButton) findViewById(R.id.scan_items);
        summary = (MaterialButton) findViewById(R.id.summary);
        home = (MaterialButton) findViewById(R.id.home);
        upload = (MaterialButton) findViewById(R.id.upload);
        groupId = (TextInputEditText) findViewById(R.id.groupId);
        trId = new ArrayList<String>();
        setAdapter();
        choose_file.setOnClickListener(this);
        scan_items.setOnClickListener(this);
        summary.setOnClickListener(this);
        home.setOnClickListener(this);
        upload.setOnClickListener(this);


    }

    private void setAdapter() {

        trans_spinner = (Spinner)findViewById(R.id.trans_spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.Transaction_type, R.layout.textlayoutspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trans_spinner.setAdapter(adapter);


        source_spinner = (Spinner)findViewById(R.id.source_spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.SCs, R.layout.textlayoutspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        source_spinner.setAdapter(adapter);

        dest_spinner = (Spinner)findViewById(R.id.destination_spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.FCs, R.layout.textlayoutspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dest_spinner.setAdapter(adapter);

        shipment_spinner = (Spinner)findViewById(R.id.shipment_spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.shipment_type, R.layout.textlayoutspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shipment_spinner.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.choose_file :
                choose();
                break;

            case R.id.scan_items :
                scan();
                break;

            case R.id.summary :
                summary();
                break;

            case R.id.home :
                home();
                finish();
                break;

            case R.id.upload :
                upload();
                break;
        }

    }


    private void scan() {
        intent = new Intent(this,ScanActivity.class);
        startActivity(intent);
    }


    private void summary() {
        if(trId.isEmpty()){
            Snackbar.make(findViewById(R.id.upload_parent),"Nothing To Show ....",Snackbar.LENGTH_LONG).show();
        } else{
            intent = new Intent(this,SummaryActivity.class);
            startActivity(intent);
        }

    }

    private void home() {
        intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }


    private void upload() {

        if(!iscomplete()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ERROR");
            builder.setMessage("Please Complete the details before Uploading.");
            builder.setPositiveButton("Ok", null);
            builder.create().show();
        }else if(trId.isEmpty()){
            Snackbar.make(findViewById(R.id.upload_parent),"Nothing To Upload ....",Snackbar.LENGTH_LONG).show();
        }else {
            updateDB();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Success :) ");
            builder.setMessage("Successfully Uploaded "+ trId.size() + " Records.");
            builder.setPositiveButton("Upload Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(UploadActivity.this, UploadActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setNeutralButton("Home", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(UploadActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();

        }


    }

    private void updateDB() {







    }

    private boolean iscomplete() {

        transaction_type = trans_spinner.getSelectedItem().toString();
        source = source_spinner.getSelectedItem().toString();
        destination = dest_spinner.getSelectedItem().toString();
        shipment_type = shipment_spinner.getSelectedItem().toString();
        groupid = groupId.getText().toString();

        if(transaction_type.equals(choose) || source.equals(choose) || destination.equals(choose) || shipment_type.equals(choose) || groupid.equals("")){
            Log.e("Choose","Returning false");
            return false;

        }else{
            Log.e("Choose","Returning True");
            return true;
        }
    }

    private void choose() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},Permission_Request);
            }else {
                choosefile();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case Permission_Request : {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted !", Toast.LENGTH_SHORT).show();
                    choosefile();
                }else{
                    Toast.makeText(this, "Permission Denied !!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void choosefile() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(i,Request_code_file);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String fullerror ="";
        String actualfilepath = "";
        if (requestCode == Request_code_file){
            if (resultCode == RESULT_OK){
                try {
                    Uri imageuri = data.getData();
                    InputStream stream = null;
                    String tempID= "", id ="";
                    Uri uri = data.getData();
                    fullerror = fullerror +"file auth is "+uri.getAuthority();
                    if (imageuri.getAuthority().equals("media")){
                        tempID =   imageuri.toString();
                        tempID = tempID.substring(tempID.lastIndexOf("/")+1);
                        id = tempID;
                        Uri contenturi = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        String selector = MediaStore.Images.Media._ID+"=?";
                        actualfilepath = getColunmData( contenturi, selector, new String[]{id}  );
                    }else if (imageuri.getAuthority().equals("com.android.providers.media.documents")){
                        tempID = DocumentsContract.getDocumentId(imageuri);
                        String[] split = tempID.split(":");
                        String type = split[0];
                        id = split[1];
                        Uri contenturi = null;
                        if (type.equals("image")){
                            contenturi = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        }else if (type.equals("video")){
                            contenturi = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        }else if (type.equals("audio")){
                            contenturi = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        String selector = "_id=?";
                        actualfilepath = getColunmData( contenturi, selector, new String[]{id}  );
                    } else if (imageuri.getAuthority().equals("com.android.providers.downloads.documents")){
                        tempID =   imageuri.toString();
                        tempID = tempID.substring(tempID.lastIndexOf("/")+1);
                        id = tempID;
                        Uri contenturi = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        // String selector = MediaStore.Images.Media._ID+"=?";
                        actualfilepath = getColunmData( contenturi, null, null  );
                    }else if (imageuri.getAuthority().equals("com.android.externalstorage.documents")){
                        tempID = DocumentsContract.getDocumentId(imageuri);
                        String[] split = tempID.split(":");
                        String type = split[0];
                        id = split[1];
                        Uri contenturi = null;
                        if (type.equals("primary")){
                            actualfilepath=  Environment.getExternalStorageDirectory()+"/"+id;
                        }
                    }
                    File myFile = new File(actualfilepath);
                    String temppath =  uri.getPath();
                    if (temppath.contains("//")){
                        temppath = temppath.substring(temppath.indexOf("//")+1);
                    }
                    fullerror = fullerror +"\n"+" file details -  "+actualfilepath+"\n --"+ uri.getPath()+"\n--"+temppath;
                    if ( actualfilepath.equals("") || actualfilepath.equals(" ")) {
                        myFile = new File(temppath);
                    }else {
                        myFile = new File(actualfilepath);
                    }
                    readfile(myFile);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readfile(File myFile) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(myFile));
            String line;
            file_size = 0;
            while ((line = br.readLine())!=null){
                file_size++;
                trId.add(line);
            }
            br.close();
        }catch (Exception e){
            Log.e("main", " error is "+e.toString());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Records Added :");
        builder.setMessage("Successfully Added "+file_size+" records.");
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }

    public String getColunmData( Uri uri, String selection, String[] selectarg){
        String filepath ="";
        Cursor cursor = null;
        String colunm = "_data";
        String[] projection = {colunm};
        cursor =  getContentResolver().query( uri, projection, selection, selectarg, null);
        if (cursor!= null){
            cursor.moveToFirst();
            filepath = cursor.getString(cursor.getColumnIndex(colunm));
        }
        if (cursor!= null)
            cursor.close();
        return  filepath;
    }

}
