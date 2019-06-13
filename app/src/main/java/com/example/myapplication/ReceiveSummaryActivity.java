package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReceiveSummaryActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseFirestore db;
    private RecyclerView.LayoutManager layoutManager;
    private SummaryRecyclerAdapter adapter;

    CollectionReference packagesref;
    static List<Package> totallist;
    static List<Package> totalliststring,reconciledlist,unreconciledlist,extralist;
    CardView total,reconciled,unreconciled,extra;
    TextView source,target,challan,totalcount,reconciledcount,unreconciledcount,extracount;
    int totalnum=0,reconcilednum=0,unreconcilednum=0,extranum=0;
    String sourcestring;
    TextView texthead;
    RecyclerView recycler;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_summary);

        texthead = (TextView) findViewById(R.id.texthead);
        recycler = (RecyclerView) findViewById(R.id.recyclercontainer);


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
        totalliststring = new ArrayList<Package>();
        reconciledlist = new ArrayList<Package>();
        unreconciledlist = new ArrayList<Package>();
        extralist = new ArrayList<Package>();



        db = FirebaseFirestore.getInstance();
        packagesref = db.collection(Constants.PackageCollectionName);
        packagesref.whereEqualTo("groupId",ReceiveActivity.groupID).whereEqualTo("currentNode",ReceiveActivity.target).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(QueryDocumentSnapshot d:queryDocumentSnapshots){
                        Package p = d.toObject(Package.class);
                        p.setId(d.getId());
                        totallist.add(p);
                    }
                    findViewById(R.id.cardView).setVisibility(View.VISIBLE);
                    findViewById(R.id.progress).setVisibility(View.GONE);

                    initialize();
                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveSummaryActivity.this);
                    builder.setTitle("Error :");
                    builder.setMessage("Nothing to Show ... ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    final AlertDialog dlg = builder.create();
                    dlg.show();
                    dlg.setCancelable(false);
                    MediaPlayer mp = MediaPlayer.create(ReceiveSummaryActivity.this, R.raw.warning_sound);
                    mp.start();
                    findViewById(R.id.progress).setVisibility(View.GONE);

                }
            }
        });
    }

    private void initialize() {
        sourcestring = totallist.get(0).getSourceNode();
        source.setText(" Source :  "+sourcestring);
        target.setText(" Target :  "+ReceiveActivity.target);
        challan.setText(" Challan No :  "+ReceiveActivity.groupID);

        getList();

        if(ReceiveActivity.isSCFC){

            total.setVisibility(View.VISIBLE);
            reconciled.setVisibility(View.VISIBLE);
            unreconciled.setVisibility(View.VISIBLE);
            extra.setVisibility(View.VISIBLE);

            totalcount.setText("( "+totalnum+" )");
            total.setOnClickListener(this);
            reconciledcount.setText("( "+reconcilednum+" )");
            unreconciledcount.setText("( "+unreconcilednum+" )");
            extracount.setText("( "+extranum+" )");
            reconciled.setOnClickListener(this);
            unreconciled.setOnClickListener(this);
            extra.setOnClickListener(this);

        }else {
            texthead.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.VISIBLE);

            layoutManager = new LinearLayoutManager(this);
            recycler.setLayoutManager(layoutManager);
            adapter = new SummaryRecyclerAdapter(reconciledlist);
            recycler.setAdapter(adapter);
            recycler.setHasFixedSize(true);
        }

    }

    private void getList() {

        for(Package p:totallist){

            if(p.getReconcilationState().equals(Constants.Reconciled) || p.getReconcilationState().equals(Constants.Intransit)){
                totalliststring.add(p);
            }
            if(p.getReconcilationState().equals(Constants.Reconciled)){
                reconciledlist.add(p);
            }else if(p.getReconcilationState().equals(Constants.Intransit)){
                unreconciledlist.add(p);
            }else{
                extralist.add(p);
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
