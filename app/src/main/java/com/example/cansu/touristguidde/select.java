package com.example.cansu.touristguidde;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class select extends AppCompatActivity {

    public Spinner sp1;
    public List<String> list = new ArrayList<String>();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    public  int secim=0;

    public TextView sicakliktext;
    public String sehir;
    public Button goster,filtresiz;
    public ImageView imageView;
    public String durum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        sp1 =  findViewById(R.id.spinner);
        sicakliktext=findViewById(R.id.textView);
        goster=findViewById(R.id.button3);
        filtresiz=findViewById(R.id.button4);
        imageView = (ImageView)findViewById(R.id.img);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sehirlerial();

        goster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), touristMap.class);
                i.putExtra("send_string",sehir+","+durum);
                startActivity(i);
                //Toast.makeText(getApplicationContext(),sehir+","+durum,Toast.LENGTH_LONG).show();

                //Intent intocan = new Intent(select.this, touristMap.class);
                //startActivity(intocan);
            }
        });

        filtresiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), touristMap.class);
                i.putExtra("send_string",sehir+",nofilter");
                Toast.makeText(getApplicationContext(),sehir+"nofilter",Toast.LENGTH_LONG).show();
                startActivity(i);

                //Intent intocan = new Intent(select.this, touristMap.class);
                //startActivity(intocan);
            }
        });



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


                clickekle();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void clickekle(){
        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adp1);


        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                if(secim>0) {
                    Toast.makeText(getBaseContext(), list.get(position), Toast.LENGTH_SHORT).show();

                    JsonParse jsonParse = new JsonParse();
                     sehir = ""+list.get(position).toString();

                    new JsonParse().execute();//jsonParse AsynTask metodumuzu çalıştırdık.


                }
                secim=1;

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                Toast.makeText(getBaseContext(), "seçim yapılmadı", Toast.LENGTH_SHORT).show();
            }
        });
    }



    //AsynTask olayını başka bir yazıda açıklayacağım inşallah ama internetten araştırabilirsiniz çok güzel anlatan siteler var.
    protected class JsonParse extends AsyncTask<Void, Void, Void> {
        String result_main ="";
        String result_description = "";
        String result_icon = "";
        int result_temp;
        String result_city;
        Bitmap bitImage;

        @Override
        protected Void doInBackground(Void... params) {
            String result="";
            try {
                String country = sehir.toLowerCase().replace("ı","i").
                        replace("ğ","g").replace("ş","s").
                        replace("ç","c").replace("ü","u");
                URL weather_url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+country+"&appid=0cea4a1ff8d6e6a7d2ac74862c0089fc");
                //URL weather_url = new URL("api.openweathermap.org/data/2.5/forecast?q="+sehir+"&APPID=0cea4a1ff8d6e6a7d2ac74862c0089fc");//Url'mizi    BufferedReader bufferedReader = null;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(weather_url.openStream()));//url'yi okuyacak bufferReader'a gönderdik
                String line = null;
                while((line = bufferedReader.readLine()) != null){//satırları tek tek aldık ve ekledik
                    result += line;
                }
                bufferedReader.close();

                JSONObject jsonObject = new JSONObject(result);//string ifadeye çevirdik
                JSONArray jsonArray = jsonObject.getJSONArray("weather");//şimdi jsona bakarsanız weather isimli bir dizi var o diziyi aldık
                JSONObject jsonObject_weather = jsonArray.getJSONObject(0);//ilk indexi aldık
                result_main = jsonObject_weather.getString("main");//ilk indexin main adlı değişkenini çektik
                result_description = jsonObject_weather.getString("description");
                result_icon = jsonObject_weather.getString("icon");
//tek tek işimize yarayacakları aldık

                JSONObject jsonObject_main = jsonObject.getJSONObject("main");//main diye son kısımlarda bir değişken var onuda aldık
                Double temp = jsonObject_main.getDouble("temp");//main'in içinden sıcaklığı aldık

                result_city = jsonObject.getString("name");//en sondaki kısımdan city ismini aldık

                result_temp = (int) (temp-273);//Kelvin olduğu için Celcius'a çevirdik

                URL icon_url = new URL("http://openweathermap.org/img/w/"+result_icon+".png");//resimda saklıyor api adresimiz
                InputStream is = icon_url.openConnection().getInputStream();
                bitImage = BitmapFactory.decodeStream(is);//Android'de image olarak kullanamadığımız için bitmap formatına çevirdik
                //if(bitImage != null

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("hata: ", e.getMessage());
                //sicakliktext.setText(""+e);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("hata: ", e.getMessage());
                //sicakliktext.setText(""+e);
            }catch (Exception e){
               // e.printStackTrace();
                Log.e("hata: ", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            durum=result_main;
            sicakliktext.setText("Sehir: "+sehir+String.valueOf(result_temp)+"  "+result_main);
            imageView.setImageBitmap(bitImage);

        }
    }


}
