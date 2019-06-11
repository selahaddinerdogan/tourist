package com.example.cansu.touristguidde;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private Context context;
    private int REQUEST_CODE = 99;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=getApplicationContext();
        Button kayitbuton = (Button)findViewById(R.id.button2);
        Button girisbuton = (Button)findViewById(R.id.button);
        final EditText emailtext=(EditText)findViewById(R.id.editText2);
        final EditText passwordtext=(EditText)findViewById(R.id.editText3);
        sharedPref = MainActivity.this.getSharedPreferences("sharedPref",Context.MODE_PRIVATE);
        boolean loginStatus = sharedPref.getBoolean("login",false);
        if(loginStatus){
            Intent intocan = new Intent(MainActivity.this, NavbarActivity.class);
            startActivity(intocan);
        }


        mAuth = FirebaseAuth.getInstance();

        kayitbuton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Toast.makeText(getBaseContext(), "Harita açılıyor.", Toast.LENGTH_SHORT).show();
                //Intent intocan = new Intent(MainActivity.this, select.class);
              //  startActivity(intocan);

               String email=""+emailtext.getText();
               String password=""+passwordtext.getText();
               kaydol(email,password);

            }
        });
        girisbuton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email=""+emailtext.getText();
                String password=""+passwordtext.getText();
                giris(email,password);

            }
        });

    }

    public void kaydol(final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getBaseContext(),"Kayıt başarılı.", Toast.LENGTH_SHORT).show();
                            giris(email,password);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getBaseContext(),"Kaydolurken hata oluştu.", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
    public void giris(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(getBaseContext(),"Giriş Başarılı. Kullanıcı adı: "+user.getEmail(), Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPref.edit(); //SharedPreferences'a kayıt eklemek için editor oluşturuyoruz
                            editor.putString("useremail",user.getEmail());
                            editor.putBoolean("login",true);
                            editor.commit();
                            Intent intocan = new Intent(MainActivity.this, NavbarActivity.class);
                            startActivity(intocan);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getBaseContext(),"Giriş başarısız.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Permission(){
        if (context.checkCallingPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
            else {
                Toast.makeText(context,getResources().getString(R.string.no_permission),Toast.LENGTH_LONG).show();
            }
        }
    }





}
