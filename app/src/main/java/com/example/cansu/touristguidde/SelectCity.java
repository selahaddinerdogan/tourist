package com.example.cansu.touristguidde;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectCity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ListView listView;
    ListViewAdapter adapter;
    SearchView editsearch;
    RelativeLayout listLayout;
    String[] cityNameList;
    ArrayList<String> cityies = new ArrayList<String>();
    public List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        listLayout=findViewById(R.id.listLayout);
        listView = (ListView) findViewById(R.id.listview);

        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);
        listLayout.bringToFront();
        sehirlerial();

    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }
    private void sehirlerial(){

        DatabaseReference okuma = FirebaseDatabase.getInstance().getReference("CITIES");
        okuma.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                list.add("Ankara");
                for (DataSnapshot key: keys) {
                    list.add(""+key.getKey());
                }
                list.add("Van");
                list.add("İzmir");
                list.add("Bursa");
                adapter = new ListViewAdapter(getApplicationContext(), list);
                listView.setAdapter(adapter);

                // clickekle();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
