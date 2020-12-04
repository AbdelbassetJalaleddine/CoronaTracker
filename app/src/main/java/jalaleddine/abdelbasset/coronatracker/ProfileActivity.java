package jalaleddine.abdelbasset.coronatracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;

public class ProfileActivity extends AppCompatActivity implements ForceUpdateChecker.OnUpdateNeededListener {

    ImageView iv;
    EditText nameeditText;
    EditText phoneeditText;
    AlertDialog spotsDialog;
    FancyButton emergencybutton;
    FancyButton statisticsbutton;
    FancyButton contactslistbutton;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        iv = findViewById(R.id.imageView);
        nameeditText = findViewById(R.id.NameeditText);
        phoneeditText = findViewById(R.id.PhoneNumbereditText);
        emergencybutton = findViewById(R.id.Emergencybutton);
        emergencybutton.setIconResource(R.drawable.emergency);
        emergencybutton.setIconPosition(FancyButton.POSITION_BOTTOM);

        statisticsbutton = findViewById(R.id.Statisticsbutton);
        statisticsbutton.setIconResource(R.drawable.statistics);
        statisticsbutton.setIconPosition(FancyButton.POSITION_BOTTOM);

        contactslistbutton = findViewById(R.id.ContactsListButton);
        contactslistbutton.setIconResource(R.drawable.contacts_list);
        contactslistbutton.setIconPosition(FancyButton.POSITION_BOTTOM);

        spotsDialog = new SpotsDialog.Builder().setContext(ProfileActivity.this).
        setMessage("Loading Information...").build();
        spotsDialog.show();
        GetInfo();
        FirebaseChecker(); // check if the app is up to date or if it needs an update
        // then send the user to the store
        checkLocationPermission();
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("Phone Number",phoneeditText.getText().toString());
        startService(intent);
    }

    private void GetInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseInstanceId.getInstance().getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    try{
                            if(snapshot.getKey().equals("Gender")){
                                if(snapshot.getValue().equals("Male")){
                                    iv.setImageResource(R.mipmap.male_avatar);
                                }
                                else{
                                    iv.setImageResource(R.mipmap.female_avatar);
                                }
                            }
                            if(snapshot.getKey().equals("Name")){
                                nameeditText.setText(snapshot.getValue().toString());
                            }
                            if(snapshot.getKey().equals("Phone Number")){
                                phoneeditText.setText(snapshot.getValue().toString());
                            }
                        spotsDialog.dismiss();
                    }catch (Exception e){
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        spotsDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);        }
        if(id == R.id.action_credits){
            Intent intent = new Intent(ProfileActivity.this, CreditsActivity.class);
            startActivity(intent);
        }
        if(id == R.id.action_emergency){
            Intent intent = new Intent(ProfileActivity.this, EmergencyActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }

    public void FirebaseChecker(){

        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, "1.0");
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://dowanghami.en.aptoide.com/?store_name=abdelbassetj&app_id=46985875");

        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "remote config is fetched.");
                            firebaseRemoteConfig.fetchAndActivate();
                        }
                    }
                });
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
    }

    @Override
    public void onUpdateNeeded(final String updateUrl) {
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update the app to the newest version.")
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                redirectStore(updateUrl);
                            }
                        }).setNegativeButton("No, thanks",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish(); if i want to force the update i just put this
                                // so that the user either updates the app
                                // or he gets kicked out

                            }
                        }).create();
        dialog.show();
    }
    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void ContactsActivity(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    public void StatisticsActivity(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void EmergencyActivity(View view) {
        Intent intent = new Intent(this, EmergencyActivity.class);
        startActivity(intent);
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

               // Alert Dialog to show why i want the user's location
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //permission for user location
                                ActivityCompat.requestPermissions(ProfileActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

    }



}
