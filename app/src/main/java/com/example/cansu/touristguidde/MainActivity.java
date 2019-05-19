package com.example.cansu.touristguidde;

import android.content.Intent;
import android.support.annotation.NonNull;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button kayitbuton = (Button)findViewById(R.id.button2);
        Button girisbuton = (Button)findViewById(R.id.button);
        final EditText emailtext=(EditText)findViewById(R.id.editText2);
        final EditText passwordtext=(EditText)findViewById(R.id.editText3);


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
                            Toast.makeText(getBaseContext(),"Giriş Başarılı. Kullanıcı adı: "+user.getUid(), Toast.LENGTH_SHORT).show();

                            Intent intocan = new Intent(MainActivity.this, SelectCity.class);
                            startActivity(intocan);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getBaseContext(),"Giriş başarısız.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }





}
