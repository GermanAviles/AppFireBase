package com.example.german.appfirebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;


import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class PublicarActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference ObjetoRef;

    private EditText txtproducname;
    private EditText txtmumcontac;
    private EditText txtpreciop;
    private EditText txtnumberFijo;
    private Button btnpublicar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar);

        txtproducname = (EditText) findViewById(R.id.txtProductoname);
        txtmumcontac = (EditText) findViewById(R.id.txtNumbercontac);
        txtnumberFijo = (EditText) findViewById(R.id.txtNumberFijo);
        txtpreciop = (EditText) findViewById(R.id.txtPreciop);


        btnpublicar = (Button) findViewById(R.id.btnPublicarr);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("LoginActivity", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("LoginActivity", "onAuthStateChanged:signed_out");
                }
            }
        };

        database = FirebaseDatabase.getInstance();
        ObjetoRef = database.getReference("Publicaciones");

        btnpublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserProfileChangeRequest usuario = new UserProfileChangeRequest.Builder()
                        .setDisplayName(toString().trim()).build();

                mAuth.getCurrentUser().updateProfile(usuario);
                ObjetoRef = database.getReference("UID del publicador").child(mAuth.getCurrentUser().getUid());

                ObjetoRef.child("Nombre del Producto").setValue(txtproducname.getText().toString());
                ObjetoRef.child("Precio").setValue(txtpreciop.getText().toString());
                ObjetoRef.child("telefonos").child("celular").setValue(txtmumcontac.getText().toString());
                ObjetoRef.child("telefonos").child("fijo").setValue(txtnumberFijo.getText().toString());
                ObjetoRef.child("dispositivoDondeGuardo").setValue(FirebaseInstanceId.getInstance().getToken());

                txtproducname.setText("");
                txtpreciop.setText("");
                txtmumcontac.setText("");
                txtnumberFijo.setText("");
                Intent intent = new Intent(PublicarActivity.this,MainActivity.class);
                startActivity(intent);

                ObjetoRef.getParent().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        System.out.println(dataSnapshot.getChildrenCount());
                        //Log.d("Home Activity", "Value is: " + valor);

                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("Home Activity", "Failed to read value.", error.toException());
                    }
                });

            }
        });

    }

//    public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//
//        ListView listView;
//        ArrayAdapter<String> ArrAdapter;
//        ArrayList<String> displayArray;
//        ArrayList<String> keysArray;
//
//        @Override
//        public void onClick(View view) {
//
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
