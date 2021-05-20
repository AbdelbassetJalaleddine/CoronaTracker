package jalaleddine.abdelbasset.coronatracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;


import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;


public class MyService extends Service {

    String MyPhoneNumber;
    ArrayList<String> Phones;
    double longy;
    double laty;
    String NameTemp;
    double latTemp;
    double longTemp;
    boolean done = false;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Phones = new ArrayList<>();
        MyPhoneNumber = intent.getStringExtra("Phone Number");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "no permission", Toast.LENGTH_SHORT).show();

                return START_NOT_STICKY;
            }
                Toast.makeText(this, "yay permission", Toast.LENGTH_SHORT).show();
                TrackerSettings settings =
                        new TrackerSettings()
                                .setUseGPS(true)
                                .setUseNetwork(true)
                                .setUsePassive(true)
                                .setTimeBetweenUpdates(1000);
                                //.setMetersBetweenUpdates(3);
                LocationTracker tracker = new LocationTracker(this, settings) {

                    @Override
                    public void onLocationFound(Location location) {
                        longy = location.getLongitude();
                        laty = location.getLatitude();
                        System.out.println("long " + longy + " lat " + laty);
                        //UpdateLocation(laty,longy);
                        //FindNumbers();
                    }
                    @Override
                    public void onTimeout() {
                        Toast.makeText(MyService.this, "Location Timeout", Toast.LENGTH_SHORT).show();
                    }
                };
                tracker.startListening();
        }

        return START_STICKY;
    }

    private void FindNumbers() {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseInstanceId.getInstance().getId());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        try{
                            if(snapshot.getKey().equals("Phone Number")){
                                MyPhoneNumber = snapshot.getValue().toString();
                            }
                            if(!snapshot.getKey().equals("Gender") && !snapshot.getKey().equals("Phone Number") && !snapshot.getKey().equals("Name"))
                            {
                               // System.out.println("Da Keysss " + snapshot.getKey());
                                Phones.add(snapshot.getKey());
                            }
                        }catch (Exception e){
                            Toast.makeText(MyService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                   if(Phones.size() > 0)
                       CheckCorona();
                   try{
                        CheckOthersLocation();}
                   catch (Exception e){
                       e.printStackTrace();
                   }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

    }

    private void CheckOthersLocation() {
        latTemp = 0;
        longTemp = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(Phones.get(0));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    try{
                        if(snapshot.getKey().equals("Lat") && snapshot.getValue() != null){
                            latTemp = (double) snapshot.getValue();
                            System.out.println("Lat " + latTemp);
                        }
                        else if(snapshot.getKey().equals("Long") && snapshot.getValue() != null){
                            longTemp = (double) snapshot.getValue();
                            System.out.println("Longy is " + longTemp);
                        }
                        if(longTemp != 0 && latTemp != 0 && !done){
                            done = true;
                            distance(laty,longy,latTemp,longTemp,Phones.get(0));
                            Phones.remove(0);

                        }
                    }catch (Exception e){
                        Toast.makeText(MyService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                done = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateLocation(double lat,double lon) {
        System.out.println("Location Here");
        FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseDatabase.getInstance().getReference().child("Users").child(MyPhoneNumber).child("Long").setValue(lon);
        FirebaseDatabase.getInstance().getReference().child("Users").child(MyPhoneNumber).child("Lat").setValue(lat);
    }
    private void CheckCorona() {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Phones.get(0));
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    try{
                        if(snapshot.getKey().toString().contains("HasCorona")){
                            System.out.println("Coronaaaa ");
                        String subby = snapshot.getValue().toString();
                        if(subby.equals("true")){
                            CoronaAlertNotification();
                        }}}catch(Exception e){
                        e.printStackTrace();
                    }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
}

    private void CoronaAlertNotification() {
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Songs");

        final int notificationId = 1;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager NotifyManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("Songs", "Songs",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Alert!");
            assert NotifyManager != null;
            NotifyManager.createNotificationChannel(channel);
        }     Intent activityIntent = new Intent(this,EmergencyActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,activityIntent,0);

        builder
                .setContentText(getResources().getString(R.string.Corona_Alert_Text))
                .setContentTitle(getResources().getString(R.string.Corona_Alert))
                .setOnlyAlertOnce(true)
                .setChannelId("Songs")
                .setStyle(new NotificationCompat.BigTextStyle())
                .setAutoCancel(true)
                .setColor(Color.rgb(128,0,128))
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher);
        // Issue the notification
        notificationManager.notify(notificationId, builder.build());
    }

    private double distance(double lat1, double lon1, double lat2, double lon2,String phone) {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
                dist = (dist * 1.609344) / 1000; // to calculate the distance in meters
            if(dist < 2){
               ContactWithPerson(phone);
                System.out.println("distance is " + dist);
                //Toast.makeText(this, "distance is " + dist, Toast.LENGTH_SHORT).show();
            }

            return (dist);
        }

    private void ContactWithPerson(String phone) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);
        FirebaseDatabase.getInstance().getReference().child(FirebaseInstanceId.getInstance().getId())
                .child(phone).child("Picked Date").setValue(formattedDate);
        System.out.println("Contact With Person");
    }
}


