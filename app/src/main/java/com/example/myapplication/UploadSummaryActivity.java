package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import static android.view.View.VISIBLE;
import static android.widget.GridLayout.HORIZONTAL;

public class UploadSummaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    RelativeLayout container;
    FloatingActionButton add;
    EditText input;
    ImageView done;
    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        container = (RelativeLayout) findViewById(R.id.inputcontainer);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        input = (EditText) findViewById(R.id.input);
        done = (ImageView) findViewById(R.id.done);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(UploadActivity.trId);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerView.setAlpha(1.0f);
                container.setVisibility(View.INVISIBLE);

                String id = input.getText().toString();
                if(!id.equals("")){
                    adapter.addItem(id);

                }else{
                    Snackbar.make(container,"Empty Tracking Id ...",Snackbar.LENGTH_LONG).show();
                }

            }
        });

        add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(container.getVisibility() == VISIBLE){
                    recyclerView.setAlpha(1.0f);
                    container.setVisibility(View.INVISIBLE);
                }else{
                    recyclerView.setAlpha(0.5f);
                    input.setText("");
                    container.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 121:
                adapter.removeItem(item.getGroupId());
                display_msg("Item Removed Successfully....");
                return true;


                default:
                    return super.onContextItemSelected(item);

        }
    }

    public  void display_msg(String message){
        Snackbar.make(recyclerView,message,Snackbar.LENGTH_SHORT).show();
    }
}
