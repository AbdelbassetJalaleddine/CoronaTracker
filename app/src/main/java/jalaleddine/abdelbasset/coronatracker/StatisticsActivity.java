package jalaleddine.abdelbasset.coronatracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class StatisticsActivity extends AppCompatActivity {

    String url;
    HashMap<String,Integer> CountriesMap;
    ArrayList<String> CountriesList;
    EditText globaleditTextNewConfirmed;
    EditText globaleditTextTotalConfirmed;
    EditText globaleditTextNewRecovered;
    EditText globaleditTextTotalRecovered;
    EditText globaleditTextNewDeaths;
    EditText globaleditTextTotalDeaths;

    EditText countryeditTextNewConfirmed;
    EditText countryeditTextTotalConfirmed;
    EditText countryeditTextNewRecovered;
    EditText countryeditTextTotalRecovered;
    EditText countryeditTextNewDeaths;
    EditText countryeditTextTotalDeaths;

    AutoCompleteTextView spinner;
    AlertDialog spotsDialog;
    JSONObject CountriesObj;
    JSONArray CountriesArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        spotsDialog = new SpotsDialog.Builder().setContext(this).
                setMessage("Getting Statistics...").build();
        spotsDialog.show();
        globaleditTextNewConfirmed = findViewById(R.id.GlobalNewConfirmededitText);
        globaleditTextTotalConfirmed = findViewById(R.id.GlobalTotalConfirmededitText);
        globaleditTextNewRecovered = findViewById(R.id.GlobalNewRecoverededitText);
        globaleditTextTotalRecovered = findViewById(R.id.GlobalTotalRecoverededitText);
        globaleditTextNewDeaths = findViewById(R.id.GlobalNewDeathseditText);
        globaleditTextTotalDeaths = findViewById(R.id.GlobalTotalDeathseditText);

        countryeditTextNewConfirmed = findViewById(R.id.CountryNewConfirmededitText);
        countryeditTextTotalConfirmed = findViewById(R.id.CountryTotalConfirmededitText);
        countryeditTextNewRecovered = findViewById(R.id.CountryNewRecoverededitText);
        countryeditTextTotalRecovered = findViewById(R.id.CountryTotalRecoverededitText);
        countryeditTextNewDeaths = findViewById(R.id.CountryNewDeathseditText);
        countryeditTextTotalDeaths = findViewById(R.id.CountryTotalDeathseditText);
        spinner = findViewById(R.id.Country);


        CountriesMap = new HashMap<>();
        CountriesList = new ArrayList<>();
        url = "https://api.covid19api.com/summary";
        getGlobalValues();
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountriesList));
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                spotsDialog.show();
                getCountryValues(spinner.getText().toString());
            }
        });
       }

    private void getCountryValues(String country) {
        try {
           // Toast.makeText(this, "The index of " + country + " is: " + CountriesMap.get(country), Toast.LENGTH_SHORT).show();
            CountriesObj = CountriesArray.getJSONObject(CountriesMap.get(country));
            int CountryNewConfirmed = CountriesObj.getInt("NewConfirmed");
            int CountryTotalConfirmed = CountriesObj.getInt("TotalConfirmed");
            int CountryNewDeaths = CountriesObj.getInt("NewDeaths");
            int CountryTotalDeaths = CountriesObj.getInt("TotalDeaths");
            int CountryNewRecovered = CountriesObj.getInt("NewRecovered");
            int CountryTotalRecovered = CountriesObj.getInt("TotalRecovered");

            countryeditTextNewConfirmed.setText(CountryNewConfirmed + "");
            countryeditTextTotalConfirmed.setText(CountryTotalConfirmed + "");
            countryeditTextNewRecovered.setText(CountryNewRecovered + "");
            countryeditTextTotalRecovered.setText(CountryTotalRecovered + "");
            countryeditTextNewDeaths.setText(CountryNewDeaths + "");
            countryeditTextTotalDeaths.setText(CountryTotalDeaths + "");
            spotsDialog.dismiss();

        }catch (Exception e){
            e.printStackTrace();
            spotsDialog.dismiss();
        }
    }

    public void getGlobalValues() {
        // Initialize a new StringRequest
            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            // Initialize a new StringRequest
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Do something with response string

                            try {
                                // TODO: object to get "Global" then get ints inside it
                                // TODO: object Countries then ARRAY then ints
                                JSONTokener tokener2 = new JSONTokener(response);
                                JSONObject mainObj2 = new JSONObject(tokener2);
                                JSONObject GlobalObj = mainObj2.getJSONObject("Global");
                                int GlobalNewConfirmed = GlobalObj.getInt("NewConfirmed");
                                int GlobalTotalConfirmed = GlobalObj.getInt("TotalConfirmed");
                                int GlobalNewDeaths = GlobalObj.getInt("NewDeaths");
                                int GlobalTotalDeaths = GlobalObj.getInt("TotalDeaths");
                                int GlobalNewRecovered = GlobalObj.getInt("NewRecovered");
                                int GlobalTotalRecovered = GlobalObj.getInt("TotalRecovered");
                                /*System.out.println("New Confirmed " + GlobalNewConfirmed + "\n" +
                                        "Total Confirmed " + GlobalTotalConfirmed + "\n" +
                                        "New Recovered " + GlobalNewRecovered + "\n" +
                                        "Total Recovered " + GlobalTotalRecovered + "\n" +
                                        "New Deaths " + GlobalNewDeaths + "\n" +
                                        "Total Deaths " + GlobalTotalDeaths);*/
                                globaleditTextNewConfirmed.setText(GlobalNewConfirmed + "");
                                 globaleditTextTotalConfirmed.setText(GlobalTotalConfirmed + "");
                                 globaleditTextNewRecovered.setText(GlobalNewRecovered + "");
                                 globaleditTextTotalRecovered.setText(GlobalTotalRecovered + "");
                                 globaleditTextNewDeaths.setText(GlobalNewDeaths + "");
                                 globaleditTextTotalDeaths.setText(GlobalTotalDeaths + "");
                                 CountriesArray = mainObj2.getJSONArray("Countries");
                                for(int i =0;i<CountriesArray.length();i++){
                                     CountriesObj = CountriesArray.getJSONObject(i);
                                    String Countries = CountriesObj.getString("Country");
                                    System.out.println("Country " + i + " " + Countries);
                                    CountriesList.add(Countries);
                                    CountriesMap.put(Countries,i);
                                    spotsDialog.dismiss();
                                }
                            } catch (Exception e) {
                                Toast.makeText(StatisticsActivity.this, R.string.Toast_Something_Didnt_Work, Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                spotsDialog.dismiss();
                            }
                            requestQueue.stop();}

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Do something when get error
                            //Snackbar.make(mCLayout,"Error.",Snackbar.LENGTH_LONG).show();
                            requestQueue.stop();
                        }
                    }
            );

            // Add StringRequest to the RequestQueue
            requestQueue.add(stringRequest);



        }



    }





