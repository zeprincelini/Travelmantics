package com.lini.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder>{
    ArrayList<TravelDeal> deals;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    ChildEventListener mChildEventListener;
    private ImageView mView;

    public DealAdapter(){
        //FireBaseUtil.openFbReference("traveldeal", this);
        mFirebaseDatabase = FireBaseUtil.mFirebaseDatabase;
        mDatabaseReference = FireBaseUtil.mDatabaseReference;
        deals = FireBaseUtil.deals;
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                //Log.d("deal",  td.getTitle());
                td.setId(dataSnapshot.getKey());
                deals.add(td);
                notifyItemInserted(deals.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }
    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View itemview = LayoutInflater.from(context).inflate(R.layout.rv_row, viewGroup, false);
        return new DealViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder dealViewHolder, int i) {
        TravelDeal deal = deals.get(i);
        dealViewHolder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mText, mPrice, mDesc;
        public DealViewHolder(@NonNull View itemView) {
            super(itemView);

            mText = itemView.findViewById(R.id.tvtitle);
            mPrice = itemView.findViewById(R.id.tvprice);
            mDesc = itemView.findViewById(R.id.tvdescription);
            mView = itemView.findViewById(R.id.tvimage);
            itemView.setOnClickListener(this);

        }
        public void bind(TravelDeal deal){
            mText.setText(deal.getTitle());
            mPrice.setText(deal.getPrice());
            mDesc.setText(deal.getDescription());
            showImage(deal.getImageUrl());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            TravelDeal selectedDeal = deals.get(position);
            Intent intent = new Intent(view.getContext(), DealActivity.class);
            intent.putExtra("Deal", selectedDeal);
            view.getContext().startActivity(intent);
        }
        private void showImage(String url){
            if(url != null && url.isEmpty() == false){
                Picasso.with(mView.getContext())
                        .load(url)
                        //.load("http://placekitten.com/200/300")
                        .resize(160, 160)
                        .centerCrop()
                        .into(mView);
            }
        }
    }
}
