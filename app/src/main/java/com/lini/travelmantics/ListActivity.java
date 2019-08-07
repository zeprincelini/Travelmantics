package com.lini.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ArrayList<TravelDeal> deals;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    ChildEventListener mChildEventListener;
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.insert_menu, menu);
        MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        if(FireBaseUtil.isAdmin == true){
            insertMenu.setVisible(true);
        }else{
            insertMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert_menu:
                Intent mIntent = new Intent(ListActivity.this, DealActivity.class);
                startActivity(mIntent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>(){
                            public void onComplete(@NonNull Task<Void> task){
                                FireBaseUtil.attachListener();
                            }
                        });
                FireBaseUtil.detachListener();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FireBaseUtil.detachListener();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        FireBaseUtil.openFbReference("traveldeal", this);
        RecyclerView recyclerView = findViewById(R.id.rvdeals);
        final DealAdapter adapter = new DealAdapter();
        recyclerView.setAdapter(adapter);
        LinearLayoutManager dealsLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(dealsLayout);

        FireBaseUtil.attachListener();
    }
    public void showMenu(){
        invalidateOptionsMenu();
    }

}
