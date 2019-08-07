package com.lini.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    EditText title_text, price_text, desc_text;
    TravelDeal deal;
    private static int pic_result = 42;
    ImageView img;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        //FireBaseUtil.openFbReference("traveldeal", this);
        mFirebaseDatabase = FireBaseUtil.mFirebaseDatabase;
        mDatabaseReference = FireBaseUtil.mDatabaseReference;

        //casting views
        title_text = findViewById(R.id.title);
        price_text= findViewById(R.id.price);
        desc_text = findViewById(R.id.description);
        Intent mIntent = getIntent();
        TravelDeal deal = (TravelDeal) getIntent().getSerializableExtra("Deal");
        if(deal == null){
            deal = new TravelDeal();
        }
        this.deal = deal;
        title_text.setText(deal.getTitle());
        price_text.setText(deal.getPrice());
        desc_text.setText(deal.getDescription());
        btn = findViewById(R.id.btn_image);
        img = findViewById(R.id.image);
        showImage(deal.getImageUrl());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Insert Picture"), pic_result);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        android.view.MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        if(FireBaseUtil.isAdmin == true){
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.savemenu).setVisible(true);
            enableEditTexts(true);
            btn.setVisibility(View.VISIBLE);
        }else{
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.savemenu).setVisible(false);
            enableEditTexts(false);
            btn.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.savemenu:
                save_travel_deal();
                Toast.makeText(this, "Deal saved successfully", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return  true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal deleted", Toast.LENGTH_LONG).show();
                backToList();
                return  true;
                default:
                    return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == pic_result && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            final StorageReference ref = FireBaseUtil.mStorageReference.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    //String url = taskSnapshot.getUploadSessionUri().toString();
                    //deal.setImageUrl(url);
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //deal.setImageUrl();
                            Uri dUri = uri;
                            String url = dUri.toString();
                            deal.setImageUrl(url);

                            String PicName = taskSnapshot.getStorage().getPath();
                            deal.setImageName(PicName);
                            showImage(url);


                        }
                    });

                }
            });
        }
    }

    private void save_travel_deal() {
        deal.setTitle(title_text.getText().toString());
        deal.setPrice(price_text.getText().toString());
        deal.setDescription(desc_text.getText().toString());
        if(deal.getId() == null){
            mDatabaseReference.push().setValue(deal);
        }else{
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }
    }

    private void deleteDeal(){
        if(deal == null){
            Toast.makeText(this, "No deal saved", Toast.LENGTH_LONG).show();
        }else{
            mDatabaseReference.child(deal.getId()).removeValue();
        }
        if(deal.getImageName() != null && deal.getImageName().isEmpty() == false){
            StorageReference picRef = FireBaseUtil.mFirebaseStorage.getReference().child(deal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private void backToList(){
        Intent intent = new Intent(DealActivity.this, ListActivity.class);
        startActivity(intent);
    }

    private void clean() {
        title_text.setText("");
        price_text.setText("");
        desc_text.setText("");
        title_text.requestFocus();
    }
    private void enableEditTexts(Boolean isEnabled){
        title_text.setEnabled(isEnabled);
        price_text.setEnabled(isEnabled);
        desc_text.setEnabled(isEnabled);
    }
    private void showImage(String url){
        if(url != null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, width * 2/3)
                    .centerCrop()
                    .into(img);
        }
    }
}

