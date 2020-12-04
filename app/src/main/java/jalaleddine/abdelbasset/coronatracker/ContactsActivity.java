package jalaleddine.abdelbasset.coronatracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import dmax.dialog.SpotsDialog;
import jalaleddine.abdelbasset.coronatracker.Adapter.ListAdapter;
import jalaleddine.abdelbasset.coronatracker.CustomObjects.ContactInformation;

import static jalaleddine.abdelbasset.coronatracker.ContactAdderActivity.dateToLong;
import static jalaleddine.abdelbasset.coronatracker.ContactAdderActivity.milliseconds;
import static java.util.Collections.sort;

public class ContactsActivity extends AppCompatActivity {

    ArrayList<ContactInformation> contactInformationArrayList;
    ArrayList<ContactInformation> FalseCoronaList;

    String Name,PhoneNumber,Gender,LastSeenEditText,PickedCalenderDate;
    boolean Hascorona;
    ListAdapter adapter;
    ListView listView;
    Map<String,String> NameDate;
    Map.Entry<String,String> entry;
    boolean coronaAlert = false;
    AlertDialog spotsDialog;
    ArrayList<String> Phones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Phones = new ArrayList<>();
        spotsDialog = new SpotsDialog.Builder().setContext(this).
                setMessage("Getting Contact Information...").build();
        spotsDialog.show();
        NameDate =  new LinkedHashMap<>();
        FalseCoronaList = new ArrayList<>();
        contactInformationArrayList = new ArrayList<>();
        contactInformationArrayList.add(new ContactInformation (Name,Gender,LastSeenEditText,Hascorona));
        listView = (ListView) findViewById(R.id.listview);
        // Getting a reference to listview of main.xml layout file
        // Setting the adapter to the listView
        adapter = new ListAdapter(this, R.layout.listview_layout, contactInformationArrayList);
        listView.setAdapter(adapter);
        DataBaseID();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Object listy = listView.getItemAtPosition(position);
                String listyy = listy.toString();
                System.out.println(listyy);*/
                // Intent intent = new Intent(ContactsActivity.this, ContactAdderActivity.class);
               //System.out.println("View to String " + view.toString());
               //System.out.println("Selected Item " + parent.getSelectedItem());
               //System.out.println("Selected View " + parent.getSelectedView());
               // FirebaseDatabase.getInstance().getReference().child("Users").child(PhoneNumber.getText().toString()).child("Name").setValue(Name.getText().toString());
                //FirebaseDatabase.getInstance().getReference().child("Users").child(PhoneNumber.getText().toString()).child("Gender").setValue(gender.getSelectedItem().toString());
                //FirebaseDatabase.getInstance().getReference().child("Users").child(PhoneNumber.getText().toString()).child("HasCorona").setValue(Hascorona);
                //FirebaseDatabase.getInstance().getReference().child(FirebaseInstanceId.getInstance().getId()).child(PhoneNumber.getText().toString()).child("Picked Date").setValue(LastSeenEditText.getText().toString());
                //startActivity(intent);
            }
            });
    }

    private void DataBaseID() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseInstanceId.getInstance().getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                  try{
                  if(!snapshot.getKey().equals("Name") && !snapshot.getKey().equals("Phone Number") && !snapshot.getKey().equals("Gender")) {
                     // System.out.println(snapshot.getKey() + " " + snapshot.getValue().toString());
                      String Date = snapshot.getValue().toString().replace("Picked Date=","").replace("{","").replace("}","");
                    NameDate.put(snapshot.getKey(),Date);
                     // System.out.println("Babies " + NameDate );
                  }}catch (Exception e){
                      Toast.makeText(ContactsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                  }
              }
              GetMyUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       // FirebaseDatabase.getInstance().getReference().child("Name").child("ID").setValue(FirebaseInstanceId.getInstance().getId());
    }

    private void GetMyUsers() {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        contactInformationArrayList.clear();
        coronaAlert = false;
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(NameDate.containsKey(snapshot.getKey())) {
                        entry = NameDate.entrySet().iterator().next();
                        String subby = snapshot.getValue().toString();
                        String Corona = subby.substring(subby.indexOf("HasCorona"),subby.indexOf(",")).replace("HasCorona=","");
                        String Gender = subby.substring(subby.indexOf("Gender="),subby.indexOf(", N")).replace("Gender=","");
                        String Name = subby.substring(subby.indexOf("Name=")).replace("Name=","").replace("}","");
                        String Phone = snapshot.getKey();
                        Phones.add(Phone);
                        if(Corona.equals("true")){
                            coronaAlert = true;
                            contactInformationArrayList.add(new ContactInformation (Name,Gender,milliseconds(entry.getValue()),true,dateToLong(entry.getValue())));
                            NameDate.remove(entry.getKey());
                        }
                        else if (Corona.equals("false")){

                            FalseCoronaList.add(new ContactInformation (Name,Gender,milliseconds(entry.getValue()),false,dateToLong(entry.getValue())));
                            NameDate.remove(entry.getKey());
                        }

                    }
                }
                if(coronaAlert){
                    for(int i =0;i<Phones.size();i++)
                    FirebaseDatabase.getInstance().getReference().child("Users").child(Phones.get(i)).child("HasCorona").setValue(true);
                }
                coronaAlert = false;
                sort(FalseCoronaList, new Comparator<ContactInformation>() {
                    @Override
                    public int compare(ContactInformation o1, ContactInformation o2) {
                        int one = (int)(o1.getTimestamp());
                        int two = (int)(o2.getTimestamp());
                        return (one-two);
                    }
                });
                sort(contactInformationArrayList, new Comparator<ContactInformation>() {
                    @Override
                    public int compare(ContactInformation o1, ContactInformation o2) {
                        long one = (o1.getTimestamp());
                        long two = (o2.getTimestamp());
                        return (int) (one-two);
                    }
                });

                while (FalseCoronaList.size()!=0){
                    contactInformationArrayList.add(FalseCoronaList.remove(0));
                }
                adapter.notifyDataSetChanged();
                NameDate.clear();
                spotsDialog.dismiss();
                CoronaAlert(coronaAlert);
                coronaAlert = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CoronaEveryone() {

    }

    private void CoronaAlert(boolean alert) {
        if(alert){
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
                            }      Intent activityIntent = new Intent(this,EmergencyActivity.class);
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
                                    //.addAction(R.drawable.ic_cancel,"Cancel",pIntent)
                                    .setSmallIcon(R.mipmap.ic_launcher);
                            // Issue the initial notification with zero progress
                            notificationManager.notify(notificationId, builder.build());
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, ContactAdderActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contacts, menu);
        return true;
    }

    }
