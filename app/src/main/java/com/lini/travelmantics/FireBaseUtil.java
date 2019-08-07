package com.lini.travelmantics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FireBaseUtil {
    private static final int RC_SIGN_IN =123 ;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private  static FireBaseUtil sFireBaseUtil;
    public static FirebaseAuth mAuth;
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageReference;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static ArrayList<TravelDeal> deals;
    private static ListActivity caller;
    public static boolean isAdmin;

    private FireBaseUtil(){}

    public static void openFbReference(String ref, final ListActivity callerActivity){
        if(sFireBaseUtil == null){
            sFireBaseUtil = new FireBaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                   if(firebaseAuth.getCurrentUser() == null){
                       FireBaseUtil.signIn();
                   }else{
                       String userId = firebaseAuth.getUid();
                       checkAdmin(userId);
                       Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                   }

                }
            };
            connectStorage();

        }
        deals = new ArrayList<TravelDeal>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }
    private static void signIn(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        caller.startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers).build()
                ,RC_SIGN_IN);
    }
    public static  void attachListener(){
        mAuth.addAuthStateListener(mAuthListener);
    }
    public static  void detachListener(){
        mAuth.removeAuthStateListener(mAuthListener);
    }
    private static void checkAdmin(String Uid ){
        FireBaseUtil.isAdmin = false;
        DatabaseReference ref = mFirebaseDatabase.getReference().child("admin").child(Uid);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FireBaseUtil.isAdmin = true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener);
    }
    public static void connectStorage(){
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("deals_picture");
    }
}
