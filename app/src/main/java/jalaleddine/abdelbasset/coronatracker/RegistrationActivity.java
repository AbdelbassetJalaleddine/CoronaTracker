package jalaleddine.abdelbasset.coronatracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class RegistrationActivity extends AppCompatActivity {


    private CountryCodePicker countryCodePicker;
    private EditText editText;
    private EditText editTextName;
    private Spinner spinnerGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        countryCodePicker = findViewById(R.id.ccp);
        countryCodePicker.setCountryForPhoneCode(961);
        editTextName = findViewById(R.id.editTextName);
        editText = findViewById(R.id.editTextPhone);
        spinnerGender = findViewById(R.id.genderSpinner);

        findViewById(R.id.SignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gender = spinnerGender.getSelectedItem().toString();
                String number = editText.getText().toString().trim();
                String code = "+"+ countryCodePicker.getSelectedCountryCode() + number;

                if (number.isEmpty() || number == null) {
                    editText.setError("Valid number is required");
                    editText.requestFocus();
                    return;
                }
                PhoneHere(code,gender);
            }
        });
    }

    private void PhoneHere(String phoneNumber,String gender) {
        SharedPreferences prefs = getSharedPreferences("UsersData", MODE_PRIVATE);
        String number = prefs.getString("number", "1");
        if(!number.equals("1")){
            Toast.makeText(RegistrationActivity.this, "User already registered! Logging IN!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegistrationActivity.this, VerifyPhoneActivity.class);
            intent.putExtra("phonenumber", phoneNumber);
            intent.putExtra("Sign Up",false);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(RegistrationActivity.this, VerifyPhoneActivity.class);
            intent.putExtra("phonenumber", phoneNumber);
            intent.putExtra("Sign Up",true);
            intent.putExtra("name",editTextName.getText().toString().trim());
            intent.putExtra("gender",gender);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public void GotoSignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
