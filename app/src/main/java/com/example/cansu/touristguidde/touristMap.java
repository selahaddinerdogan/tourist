package com.example.cansu.touristguidde;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class touristMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    public String bulundugumuz_sehir;
    public String secilimekantipi = "";

    public List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        try {
            Bundle extras = getIntent().getExtras();
            String value = extras.getString("send_string");
            Toast.makeText(getApplicationContext(),value,Toast.LENGTH_LONG).show();
            //String dizi[] = {"ELAZIG","nofilter"};

            String dizi[]=value.split(",");

            if(dizi[1].equals(("nofilter"))){
                secilimekantipi="nofilter";
            }
            else if(dizi[1].equals("Rain")){
                secilimekantipi="Kapalı";
            }else{
                secilimekantipi="ACIK";
            }

            Toast.makeText(getBaseContext(), "İşte bu şehirdesin " + dizi[0]+", Gösterilecek mekan tipi "+dizi[1], Toast.LENGTH_SHORT).show();
            ilegit(dizi[0]);
        }catch (Exception ex){
            Toast.makeText(getBaseContext(), "hata :"+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void ilegit(final String sehir) {

        this.bulundugumuz_sehir = sehir;


        DatabaseReference okuma = FirebaseDatabase.getInstance().getReference("CITIES/" + sehir);

        okuma.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key : keys) {
                    Log.e("key------- ",key.getKey());
                    if (key.getKey().equals("Koordinat")) {
                        Toast.makeText(getBaseContext(), "Koordinat " + key.getValue(), Toast.LENGTH_SHORT).show();
                        String[] koordinat = ("" + key.getValue()).split(",");
                        double x = Double.parseDouble(koordinat[0]);
                        double y = Double.parseDouble(koordinat[1]);
                        LatLng secilisehir = new LatLng(x, y);
                        float zoomLevel = 8.0f; //This goes up to 21
                        mMap.addMarker(new MarkerOptions().position(secilisehir).title("" + sehir)).setTag("sehir");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(secilisehir, zoomLevel));

                    } else if (key.getKey().equalsIgnoreCase("Ilceler")) {
                        Iterable<DataSnapshot> ilceler = key.getChildren();
                        for (DataSnapshot ilce : ilceler) {
                            Iterable<DataSnapshot> ilcebilgi = ilce.getChildren();
                            for (DataSnapshot bilgi : ilcebilgi) {
                                Log.e("bilgi key3------- ",bilgi.getKey());

                                Iterable<DataSnapshot> yerdetay = bilgi.getChildren();
                                String koord = "";
                                String tip = "";
                                for (DataSnapshot detay : yerdetay) {
                                    if (detay.getKey().equalsIgnoreCase("Koordinat")) {
                                        koord = detay.getValue().toString();
                                    }if (detay.getKey().equalsIgnoreCase("Tipi")) {
                                        tip = detay.getValue().toString();
                                    }

                                    if ((koord.length() > 2 && tip.equalsIgnoreCase(secilimekantipi))||secilimekantipi.equalsIgnoreCase("nofilter")) {
                                        //Log.e("bilgi key2-------- ", detay.getKey());

                                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                                    /*
                                        float   HUE_AZURE
                                        float   HUE_BLUE
                                        float   HUE_CYAN
                                        float   HUE_GREEN
                                        float   HUE_MAGENTA
                                        float   HUE_ORANGE
                                        float   HUE_RED
                                        float   HUE_ROSE
                                        float   HUE_VIOLET
                                        float   HUE_YELLOW
                                    */
                                        String[] koordinat = ("" + koord).split(",");
                                        double x = Double.parseDouble(koordinat[0]);
                                        double y = Double.parseDouble(koordinat[1]);
                                        LatLng ilcekoordinat = new LatLng(x, y);
                                        mMap.addMarker(new MarkerOptions().position(ilcekoordinat).title("" + bilgi.getKey()).icon(bitmapDescriptor)).setTag("ilce");
                                    }
                                }

                            }
                        }
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */



    public void rotaOlustur(LatLng latLng) {
        Location myLocation = mMap.getMyLocation();
        List<LatLng> path = new ArrayList();
        //Execute Directions API request
        String strlat = String.valueOf(myLocation.getLatitude());
        String strlon = String.valueOf(myLocation.getLongitude());
        String lat_lon = strlat+","+strlon;
        String strdestlat = String.valueOf(latLng.latitude);
        String strdestlon = String.valueOf(latLng.longitude);
        String dest_lat_lon = strdestlat+","+strdestlon;
        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyAPU_3J6KmVxgL5cGajEbQOvjEWkNN6zCQ");
        DirectionsApiRequest req = DirectionsApi.getDirections(context, lat_lon, dest_lat_lon);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e("Hata: ", ex.toString());
        }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            mMap.addPolyline(opts);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6));
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(),"Location izinleri verilmedi!!!",Toast.LENGTH_LONG).show();
        }
        mMap.setMyLocationEnabled(true);
        Location myLocation = mMap.getMyLocation();
        if (myLocation != null)
        Log.e("location ",myLocation.getLatitude()+" "+myLocation.getLongitude());

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                float zoomLevel = 12.0f;
                String secim = marker.getTag().toString();
                if (secim.equals("ilce")) {
                    zoomLevel = 15.0f; //This goes up to 21
                    //Listeyeekle(marker.getTitle());
                } else if (secim.equals("mekan")) {
                    //zoomLevel = 18.0f; //This goes up to 21
                    Listeyeekle(marker.getTitle());

                }
                //Toast.makeText(getBaseContext(), "click " + marker.getTitle() + " bu bir " + secim, Toast.LENGTH_SHORT).show();
                String title = marker.getTitle();

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoomLevel));

                gezilecek_yer_olustur(title);

                rotaOlustur(marker.getPosition());
                return false;
            }
        });


    }


    public void Listeyeekle(final String mekan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mekan);
        builder.setCancelable(false);
        builder.setPositiveButton("Listeye Ekle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getBaseContext(), mekan +" Listeye eklendi", Toast.LENGTH_SHORT).show();
                list.add("" + mekan);

            }
        });


        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setNeutralButton("Listeyi Gör", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               Listeyigoster();

            }
        });

        builder.create().show();

    }


    public void Listeyigoster(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seçili Mekanlar");
        builder.setMessage(TextUtils.join(", ", list));

        builder.setCancelable(false);
        builder.setPositiveButton("Rota Oluştur", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               RotaOlustur();

            }
        });


        builder.setNegativeButton("Geri", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        builder.create().show();
    }

    public void RotaOlustur(){
        Toast.makeText(getBaseContext(), "Konum alınacak.", Toast.LENGTH_SHORT).show();
        Toast.makeText(getBaseContext(), "Rota oluşturulacak", Toast.LENGTH_SHORT).show();
    }

    public void gezilecek_yer_olustur(String ilce) {
        DatabaseReference yerler = FirebaseDatabase.getInstance().getReference("CITIES/" + bulundugumuz_sehir + "/Ilceler/" + ilce);
        yerler.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key : keys) {
                    if (key.getKey().equals("Koordinat")) {

                    } else {
                        Iterable<DataSnapshot> mekan = key.getChildren();
                        String[] koordinat = new String[2];
                        String tipi = "";
                        for (DataSnapshot bilgi : mekan) {
                            if (bilgi.getKey().equals("Koordinat")) {
                                koordinat = ("" + bilgi.getValue()).split(",");
                            } else if (bilgi.getKey().equals("Tipi")) {
                                tipi = ("" + bilgi.getValue());
                            } else {

                            }
                        }

                        if(secilimekantipi.equals("nofilter")){
                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                            double x = Double.parseDouble(koordinat[0]);
                            double y = Double.parseDouble(koordinat[1]);
                            LatLng mekankoordinat = new LatLng(x, y);
                            mMap.addMarker(new MarkerOptions().position(mekankoordinat).title("" + key.getKey()).icon(bitmapDescriptor)).setTag("mekan");

                        }else{
                            if (tipi.equals(secilimekantipi)) {
                                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                                double x = Double.parseDouble(koordinat[0]);
                                double y = Double.parseDouble(koordinat[1]);
                                LatLng mekankoordinat = new LatLng(x, y);
                                mMap.addMarker(new MarkerOptions().position(mekankoordinat).title("" + key.getKey()).icon(bitmapDescriptor)).setTag("mekan");
                            }
                        }


                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
