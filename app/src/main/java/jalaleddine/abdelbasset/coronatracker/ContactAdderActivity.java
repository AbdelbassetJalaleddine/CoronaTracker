package jalaleddine.abdelbasset.coronatracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.rtoshiro.util.format.MaskFormatter;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.pattern.MaskPattern;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import jalaleddine.abdelbasset.coronatracker.CustomObjects.ContactInformation;

import static android.os.Build.VERSION_CODES.N;

public class ContactAdderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    DatePickerDialog datePickerDialog;
    static String LastSeen = 0 + "Day(s) ago";
    EditText LastSeenEditText;
    Spinner gender;
    Spinner Corona;
    boolean Hascorona;
    EditText Name;
    EditText PhoneNumber;
    boolean pickedDate = false;
    String PickedCalenderDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_adder);
        LastSeenEditText = findViewById(R.id.LastSeenEditText);
        datePickerDialog = new DatePickerDialog(
                this, ContactAdderActivity.this, 2020, 0, 1);
        gender = findViewById(R.id.Gender);
        Corona = findViewById(R.id.Corona);
        Name = findViewById(R.id.Name);
        PhoneNumber = findViewById(R.id.phoneNumber);
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN-NN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(LastSeenEditText, smf);
        LastSeenEditText.addTextChangedListener(mtw);
    }
    public void AddContact(View view) {
        PhoneNumber.setText(PhoneNumber.getText().toString().replace(" ",""));
        if(Name.getText().toString().isEmpty() || PhoneNumber.getText().toString().isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please Add Missing Information");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
        else{
        if(Corona.getSelectedItem().toString().equals("Yes")){
            Hascorona = true;
        }
        else{
            Hascorona = false;
        }
            Intent intent = new Intent(this, ContactsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            FirebaseDatabase.getInstance().getReference().child("Users").child(PhoneNumber.getText().toString().trim()).child("Name").setValue(Name.getText().toString());
            FirebaseDatabase.getInstance().getReference().child("Users").child(PhoneNumber.getText().toString().trim()).child("Gender").setValue(gender.getSelectedItem().toString());
            FirebaseDatabase.getInstance().getReference().child("Users").child(PhoneNumber.getText().toString().trim()).child("HasCorona").setValue(Hascorona);
            FirebaseDatabase.getInstance().getReference().child(FirebaseInstanceId.getInstance().getId()).child(PhoneNumber.getText().toString().trim()).child("Picked Date").setValue(LastSeenEditText.getText().toString());

            startActivity(intent);
        }
    }
    public void ImportFromContactsApp(View view){
        requestContactPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            retrieveContactName();
            retrieveContactNumber();
           // retrieveContactPhoto();

        }
    }

    private void retrieveContactNumber() {

        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();
        EditText PhoneNumber = findViewById(R.id.phoneNumber);
        PhoneNumber.setText(contactNumber);
        Log.d(TAG, "Contact Phone Number: " + contactNumber);
    }

    private void retrieveContactName() {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();
        EditText Name = findViewById(R.id.Name);
        Name.setText(contactName);
        Log.d(TAG, "Contact Name: " + contactName);

    }

    public static long dateToLong(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date mDate = sdf.parse(date);
            return mDate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return 0;
    }

    public static String milliseconds(String date)
    {
        //String date_ = date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try
        {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            //System.out.println("Date in milli " + timeInMilliseconds);
            long timeInMillis = System.currentTimeMillis();
            long NowTime = timeInMillis - timeInMilliseconds;
            long days = TimeUnit.MILLISECONDS.toDays(NowTime);
            long weeks = 0;
            if(days < 0 ){
                days = 0;
            }
            else if(days >= 7){
                weeks = days/7;
                days = days - (weeks * 7);
            }
            if(weeks == 0){
                LastSeen = days + " Day(s) ago";
            }
            else if(weeks >0 && days > 0) {
                LastSeen = weeks + " Week(s) and " + days + " Day(s) ago";
            }
            else{
                LastSeen = weeks + " Week(s) ago";
            }

        }
        catch (Exception e)
        {
            LastSeen = 0 + "Day(s) ago";
            e.printStackTrace();
            return LastSeen;
        }

        return LastSeen;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
    }
    private void getContacts() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
    }
    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                getContacts();
            }
        } else {
            getContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(this, "You have disabled contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void AddDateLastSeen(View view) {
        if(Build.VERSION.SDK_INT > N){
            datePickerDialog.show();
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String monthy = String.valueOf(month + 1);
                    if(monthy.length() == 1){
                        monthy = 0 + monthy;
                    }
                    String dayy = String.valueOf(dayOfMonth);
                    if(dayy.length() == 1){
                        dayy = 0 + dayy;
                    }
                    PickedCalenderDate = dayy + "-" + monthy + "-" + year;
                    LastSeenEditText.setText(PickedCalenderDate);
                   // LastSeenEditText.setText(milliseconds(PickedCalenderDate));
                    pickedDate = true;
                }
            });
        }
        else{
            Toast.makeText(this, "Saved the Date!", Toast.LENGTH_SHORT).show();
        }

    }


}
