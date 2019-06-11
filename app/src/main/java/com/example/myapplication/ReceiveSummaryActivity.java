package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReceiveSummaryActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseFirestore db;
    CollectionReference packagesref;
    static List<Package> totallist;
    static List<String> totalliststring,reconciledlist,unreconciledlist,extralist;
    CardView total,reconciled,unreconciled,extra;
    TextView source,target,challan,totalcount,reconciledcount,unreconciledcount,extracount;
    int totalnum=0,reconcilednum=0,unreconcilednum=0,extranum=0;
    String sourcestring;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_summary);


        source = findViewById(R.id.sourcedetail);
        target = findViewById(R.id.targetdetail);
        challan = findViewById(R.id.challandetail);

        total = findViewById(R.id.totalsent);
        reconciled = findViewById(R.id.reconciled);
        unreconciled = findViewById(R.id.unreconciled);
        extra = findViewById(R.id.extrareconciled);

        totalcount = findViewById(R.id.totalsentcount);
        reconciledcount = findViewById(R.id.reconciledcount);
        unreconciledcount = findViewById(R.id.unreconciledcount);
        extracount = findViewById(R.id.extrareconciledcount);

        totallist = new ArrayList<Package>();
        totalliststring = new ArrayList<String>();
        reconciledlist = new ArrayList<String>();
        unreconciledlist = new ArrayList<String>();
        extralist = new ArrayList<String>();



        db = FirebaseFirestore.getInstance();
        packagesref = db.collection(Constants.PackageCollectionName);
        packagesref.whereEqualTo("groupId",ReceiveActivity.groupID).whereEqualTo("targetNode",ReceiveActivity.target).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot d:queryDocumentSnapshots){
                    Package p = d.toObject(Package.class);
                    p.setId(d.getId());
                    totallist.add(p);
                }
                findViewById(R.id.cardView).setVisibility(View.VISIBLE);
                findViewById(R.id.container).setVisibility(View.VISIBLE);
                findViewById(R.id.progress).setVisibility(View.GONE);
                initialize();
            }
        });
    }

    private void initialize() {
        sourcestring = totallist.get(0).getSourceNode();
        source.setText(" Source :  "+sourcestring);
        target.setText(" Target :  "+ReceiveActivity.target);
        challan.setText(" Challan No :  "+ReceiveActivity.groupID);

        getList();

        totalcount.setText("( "+totalnum+" )");
        reconciledcount.setText("( "+reconcilednum+" )");
        unreconciledcount.setText("( "+unreconcilednum+" )");
        extracount.setText("( "+extranum+" )");

        total.setOnClickListener(this);
        reconciled.setOnClickListener(this);
        unreconciled.setOnClickListener(this);
        extra.setOnClickListener(this);


    }

    private void getList() {

        for(Package p:totallist){


            if(p.getSourceNode().equals(sourcestring)){
                totalliststring.add(p.getTrackingId());
            }
            if(p.getReconcilationState().equals(Constants.Reconciled)){
                reconciledlist.add(p.getTrackingId());
            }else if(p.getReconcilationState().equals(Constants.Intransit)){
                unreconciledlist.add(p.getTrackingId());
            }else{
                extralist.add(p.getTrackingId());
            }
        }

        totalnum = totalliststring.size();
        reconcilednum = reconciledlist.size();
        unreconcilednum = unreconciledlist.size();
        extranum = extralist.size();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.totalsent:{
                intent = new Intent(ReceiveSummaryActivity.this,DetailActivity.class);
                intent.putExtra(Constants.Type,Constants.TotalSent);
                startActivity(intent);
                break;
            }

            case R.id.reconciled:{
                intent = new Intent(ReceiveSummaryActivity.this,DetailActivity.class);
                intent.putExtra(Constants.Type,Constants.Reconciled);
                startActivity(intent);
                break;
            }

            case R.id.unreconciled:{
                intent = new Intent(ReceiveSummaryActivity.this,DetailActivity.class);
                intent.putExtra(Constants.Type,Constants.Unreconciled);
                startActivity(intent);
                break;
            }

            case R.id.extrareconciled:{
                intent = new Intent(ReceiveSummaryActivity.this,DetailActivity.class);
                intent.putExtra(Constants.Type,Constants.Extrareconciled);
                startActivity(intent);
                break;
            }

        }
    }
}
