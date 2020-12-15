package jalaleddine.abdelbasset.coronatracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {
    private EditText editText;
    AlertDialog alertDialog;
    private CountryCodePicker countryCodePicker;
    String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait...")
                .setCancelable(false)
                .build();
        editText = findViewById(R.id.editTextPhone2);
        countryCodePicker = findViewById(R.id.ccp);
        countryCodePicker.setCountryForPhoneCode(961);
    }
    public void SignIn(View view) {
        alertDialog.show();
        String number = editText.getText().toString().trim();
        code = "+"+ countryCodePicker.getSelectedCountryCode() + number;
        code = code.replace(" ","");
        GetInfo(code);
    }

    private void GetInfo(String phonenumber) {
        String meow = FirebaseDatabase.getInstance().getReference().child("Users").child(phonenumber).child("Name").getKey();
        SharedPreferences prefs = getSharedPreferences("UsersData", MODE_PRIVATE);
        String number = prefs.getString("number", "1");
        if(!number.equals("1")){
            Intent intent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
            intent.putExtra("Phone Number", code);
            startActivity(intent);
            alertDialog.hide();
        }
        else{
            alertDialog.hide();
            Toast.makeText(LoginActivity.this, "User is not registered", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        }
}
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signup) {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signin, menu);
        return true;
    }


}