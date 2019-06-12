package com.example.myapplication;


import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;

import java.util.Timer;
import java.util.TimerTask;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        type = getIntent().getStringExtra("Type");
        int currentApiVersion = Build.VERSION.SDK_INT;
        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {

            }
            else
            {
                requestPermission();
            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ScanActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        if(type.equals("Upload")){
            builder.setPositiveButton("Add this Item", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UploadActivity.trId.add(myResult);
                    scannerView.stopCamera();
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.success_sound);
                    mp.start();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ScanActivity.this);
                    builder1.setTitle("Success :");
                    builder1.setMessage("Package Added ... ");
                    builder1.setPositiveButton("Scan More", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            scannerView.startCamera();
                            scannerView.resumeCameraPreview(ScanActivity.this);
                        }
                    });
                    builder1.setNeutralButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    final AlertDialog dlg = builder1.create();
                    dlg.show();
                    dlg.setCancelable(false);
                }
            });

        }else if(type.equals("Receive")){
            builder.setPositiveButton("Receive this Item", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    receive(myResult);
                    scannerView.resumeCameraPreview(ScanActivity.this);
                }
            });
        }
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(ScanActivity.this);
            }
        });
        builder.setMessage(myResult);
        AlertDialog alert1 = builder.create();
        alert1.show();

    }

    private void receive(final String pkgid) {

        if(ReceiveActivity.isSCFC){

            scannerView.stopCamera();
            String id = Package.searchbyId(ReceiveActivity.mypackages,pkgid);
            // Package Reconciled
            if(id != null){
                ReceiveActivity.packagesref.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.getString("reconcilationState").equals(Constants.Reconciled)){
                            AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                            builder.setTitle("ERROR :");
                            builder.setMessage("Duplicate Package Received ... ");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    scannerView.startCamera();
                                    scannerView.resumeCameraPreview(ScanActivity.this);
                                }
                            });
                            final AlertDialog dlg = builder.create();
                            dlg.show();
                            dlg.setCancelable(false);
                            MediaPlayer mp = MediaPlayer.create(ScanActivity.this, R.raw.warning_sound);
                            mp.start();
                            ReceiveActivity.packagesref.document(documentSnapshot.getId()).update("lastUpdatedTime",(System.currentTimeMillis()/1000));
                        }else {

                            ReceiveActivity.packagesref.document(documentSnapshot.getId()).update("reconcilationState",Constants.Reconciled,"lastUpdatedTime",(System.currentTimeMillis()/1000));
                            AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                            builder.setTitle("Success :");
                            builder.setMessage("Package Reconciled... ");
                            builder.setPositiveButton("Scan More", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    scannerView.startCamera();
                                    scannerView.resumeCameraPreview(ScanActivity.this);
                                }
                            }).setNeutralButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            final AlertDialog dlg = builder.create();
                            dlg.show();
                            dlg.setCancelable(false);
                            MediaPlayer mp = MediaPlayer.create(ScanActivity.this, R.raw.success_sound);
                            mp.start();

                        }
                    }
                });
            }else {
                ReceiveActivity.packagesref.whereEqualTo("trackingId",pkgid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot d:queryDocumentSnapshots){
                                Package p = d.toObject(Package.class);
                                p.setId(d.getId());

                                if(p.getTargetNode().equals(ReceiveActivity.target)){

                                    ReceiveActivity.packagesref.document(p.getId()).update("reconcilationState",Constants.NotInChallan,"groupId",ReceiveActivity.groupID,"lastUpdatedTime",(System.currentTimeMillis()/1000)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                                            builder.setTitle("Error :");
                                            builder.setMessage("This Package is NOT IN CHALLAN ... ");
                                            builder.setPositiveButton("Scan More", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    scannerView.startCamera();
                                                    scannerView.resumeCameraPreview(ScanActivity.this);
                                                }
                                            }).setNeutralButton("Done", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            });
                                            final AlertDialog dlg = builder.create();
                                            dlg.show();
                                            dlg.setCancelable(false);

                                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.warning_sound);
                                            mp.start();
                                        }
                                    });

                                }else{
                                    ReceiveActivity.packagesref.document(p.getId()).update("reconcilationState",Constants.Missort,"lastUpdatedTime",(System.currentTimeMillis()/1000),"currentNode",ReceiveActivity.target).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                                            builder.setTitle("Error :");
                                            Log.e("Check","Found the id with matching different Target i.e. Missort");
                                            builder.setMessage("This Package is MISSORT ... ");
                                            builder.setPositiveButton("Scan More", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    scannerView.startCamera();
                                                    scannerView.resumeCameraPreview(ScanActivity.this);
                                                }
                                            }).setNeutralButton("Done", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            });
                                            final AlertDialog dlg = builder.create();
                                            dlg.show();
                                            dlg.setCancelable(false);
                                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.warning_sound);
                                            mp.start();
                                        }
                                    });
                                }
                            }
                        }else {
                            Log.e("Check","Didn't found the ID i.e. Unknown");
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                            builder.setTitle("Error :");
                            builder.setMessage("Unknown Package, do you want to accept it ... ");
                            builder.setPositiveButton("Yes, Accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ReceiveActivity.packagesref.add(new Package(pkgid,Constants.Unknown,Constants.Unknown,ReceiveActivity.groupID,Constants.Unknown,Constants.PendingProcessing,Constants.scfc,Constants.Unknown,ReceiveActivity.target,(System.currentTimeMillis()/1000))).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                                            builder.setTitle("Error :");
                                            builder.setMessage("This Package is Unknown ... ");
                                            builder.setPositiveButton("Scan More", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    scannerView.startCamera();
                                                    scannerView.resumeCameraPreview(ScanActivity.this);
                                                }
                                            }).setNeutralButton("Done", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            });
                                            final AlertDialog dlg = builder.create();
                                            dlg.show();
                                            dlg.setCancelable(false);
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

            scannerView.stopCamera();
            ReceiveActivity.packagesref.add(new Package(pkgid,ReceiveActivity.source,ReceiveActivity.target,ReceiveActivity.groupID,Constants.Reconciled,Constants.PendingProcessing,ReceiveActivity.trans_spinner.getSelectedItem().toString(),Constants.Unknown,ReceiveActivity.target,(System.currentTimeMillis()/1000))).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
                    builder.setTitle("Success :");
                    builder.setMessage("Package Reconciled... ");
                    builder.setPositiveButton("Scan More", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            scannerView.startCamera();
                            scannerView.resumeCameraPreview(ScanActivity.this);
                        }
                    }).setNeutralButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    final AlertDialog dlg = builder.create();
                    dlg.show();
                    dlg.setCancelable(false);
                    MediaPlayer mp = MediaPlayer.create(ScanActivity.this, R.raw.success_sound);
                    mp.start();
                }
            });


        }


    }


}
